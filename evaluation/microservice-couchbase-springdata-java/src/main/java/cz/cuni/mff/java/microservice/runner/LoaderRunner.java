package cz.cuni.mff.java.microservice.runner;

import com.couchbase.client.core.error.ServiceNotAvailableException;
import com.couchbase.client.java.Cluster;
import cz.cuni.mff.java.microservice.config.CouchbaseScopeInitializer;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderCouchbaseE;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderCouchbaseR;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "loader.mode", havingValue = "true")
public class LoaderRunner implements ApplicationRunner {

    private static final String R = "`bucket-main`.`spring_scope_r`";
    private static final String E = "`bucket-main`.`spring_scope_e`";

    private static final List<String> R_COLLECTIONS = List.of(
            "RegionR", "NationR", "CustomerR", "OrdersR",
            "LineitemR", "SupplierR", "PartR", "PartsuppR");

    private static final List<String> E_COLLECTIONS = List.of(
            "OrdersEWithLineitems", "OrdersEWithLineitemsArrayAsTags",
            "OrdersEWithLineitemsArrayAsTagsIndexed", "OrdersEWithCustomerWithNationWithRegion",
            "OrdersEOnlyOComment", "OrdersEOnlyOCommentIndexed");

    private final TPCHDatasetLoaderCouchbaseR loaderR;
    private final TPCHDatasetLoaderCouchbaseE loaderE;
    private final Cluster cluster;
    private final CouchbaseScopeInitializer scopeInitializer;

    @Value("${tpch.data.path:/data/tpch-data-small}")
    private String dataPath;

    public LoaderRunner(TPCHDatasetLoaderCouchbaseR loaderR, TPCHDatasetLoaderCouchbaseE loaderE,
                        Cluster cluster, CouchbaseScopeInitializer scopeInitializer) {
        this.loaderR = loaderR;
        this.loaderE = loaderE;
        this.cluster = cluster;
        this.scopeInitializer = scopeInitializer;
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        waitForQueryService();
        System.out.println("=== LOADER: creating scopes, collections, and indexes ===");
        scopeInitializer.createScopesCollectionsAndIndexes();

        System.out.println("=== LOADER: dropping and reloading relational collections ===");
        R_COLLECTIONS.forEach(col -> cluster.query("DELETE FROM " + R + ".`" + col + "`"));
        loaderR.loadAll(dataPath);

        System.out.println("=== LOADER: dropping and reloading embedded collections ===");
        E_COLLECTIONS.forEach(col -> cluster.query("DELETE FROM " + E + ".`" + col + "`"));
        loaderE.loadAll(dataPath);

        System.out.println("=== LOADER: all data loaded, exiting ===");
        System.exit(0);
    }

    private void waitForQueryService() throws InterruptedException {
        int maxAttempts = 30;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                cluster.query("SELECT 1");
                System.out.println("=== LOADER: Couchbase query service is ready ===");
                return;
            } catch (ServiceNotAvailableException e) {
                System.out.printf("=== LOADER: query service not ready yet (attempt %d/%d), retrying in 5s...%n", attempt, maxAttempts);
                Thread.sleep(5_000);
            }
        }
        throw new IllegalStateException("Couchbase query service did not become available after " + maxAttempts + " attempts");
    }
}
