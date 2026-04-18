package cz.cuni.mff.java.microservice.config;

import com.couchbase.client.core.error.CollectionExistsException;
import com.couchbase.client.core.error.ScopeExistsException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Creates Couchbase scopes and collections (spring_scope_r, spring_scope_e) and their
 * primary N1QL indexes on startup. Retries for up to 10 minutes to handle slow
 * Couchbase cluster initialization in Docker.
 */
@Component
public class CouchbaseScopeInitializer {

    private static final Logger log = LoggerFactory.getLogger(CouchbaseScopeInitializer.class);

    private static final List<String> RELATIONAL_COLLECTIONS = List.of(
            "RegionR", "NationR", "CustomerR", "OrdersR",
            "LineitemR", "SupplierR", "PartR", "PartsuppR"
    );

    private static final List<String> EMBEDDED_COLLECTIONS = List.of(
            "OrdersEWithLineitems",
            "OrdersEWithLineitemsArrayAsTags",
            "OrdersEWithLineitemsArrayAsTagsIndexed",
            "OrdersEWithCustomerWithNationWithRegion",
            "OrdersEOnlyOComment",
            "OrdersEOnlyOCommentIndexed"
    );

    private final Cluster cluster;

    public CouchbaseScopeInitializer(Cluster cluster) {
        this.cluster = cluster;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeScopes() {
        Thread thread = new Thread(this::retryInitialize, "couchbase-scope-init");
        thread.setDaemon(true);
        thread.start();
    }

    private void retryInitialize() {
        for (int attempt = 1; attempt <= 120; attempt++) {
            try {
                createScopesCollectionsAndIndexes();
                log.info("Couchbase scopes, collections, and primary indexes initialized.");
                return;
            } catch (Exception e) {
                log.warn("Attempt {}/120: Couchbase not ready yet — {}", attempt, e.getMessage());
                try { Thread.sleep(5_000); } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        log.error("Failed to initialize Couchbase scopes after 120 attempts.");
    }

    private void createScopesCollectionsAndIndexes() {
        CollectionManager cm = cluster.bucket("bucket-main").collections();

        createScopeIfAbsent(cm, "spring_scope_r");
        for (String col : RELATIONAL_COLLECTIONS) {
            createCollectionIfAbsent(cm, "spring_scope_r", col);
        }

        createScopeIfAbsent(cm, "spring_scope_e");
        for (String col : EMBEDDED_COLLECTIONS) {
            createCollectionIfAbsent(cm, "spring_scope_e", col);
        }

        createPrimaryIndexes();
    }

    private void createScopeIfAbsent(CollectionManager cm, String scope) {
        try {
            cm.createScope(scope);
            log.info("Created scope: {}", scope);
        } catch (ScopeExistsException ignored) {
        }
    }

    private void createCollectionIfAbsent(CollectionManager cm, String scope, String collection) {
        try {
            cm.createCollection(CollectionSpec.create(collection, scope));
            log.info("Created collection: {}.{}", scope, collection);
        } catch (CollectionExistsException ignored) {
        }
    }

    private void createPrimaryIndexes() {
        for (String col : RELATIONAL_COLLECTIONS) {
            cluster.query("CREATE PRIMARY INDEX IF NOT EXISTS ON `bucket-main`.`spring_scope_r`.`" + col + "`");
        }
        for (String col : EMBEDDED_COLLECTIONS) {
            cluster.query("CREATE PRIMARY INDEX IF NOT EXISTS ON `bucket-main`.`spring_scope_e`.`" + col + "`");
        }
        // Array index for R2 — l_partkey inside o_lineitems array
        cluster.query("CREATE INDEX IF NOT EXISTS idx_OrdersEWithLineitems_l_partkey" +
                " ON `bucket-main`.`spring_scope_e`.`OrdersEWithLineitems`" +
                " (DISTINCT ARRAY l.l_partkey FOR l IN o_lineitems END)");
        // Array index for R4 — o_lineitems_tags_indexed array
        cluster.query("CREATE INDEX IF NOT EXISTS idx_OrdersEWithLineitemsArrayAsTagsIndexed_tags" +
                " ON `bucket-main`.`spring_scope_e`.`OrdersEWithLineitemsArrayAsTagsIndexed`" +
                " (DISTINCT ARRAY tag FOR tag IN o_lineitems_tags_indexed END)");
        // Regular index for R7 — o_comment field
        cluster.query("CREATE INDEX IF NOT EXISTS idx_OrdersEOnlyOCommentIndexed_o_comment" +
                " ON `bucket-main`.`spring_scope_e`.`OrdersEOnlyOCommentIndexed` (o_comment)");
        log.info("Primary and secondary indexes created.");
    }
}
