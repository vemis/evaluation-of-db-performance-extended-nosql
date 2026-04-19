package cz.cuni.mff.java.microservice.controller;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import cz.cuni.mff.java.kurinna.common.controller.AbstractLoaderController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderCouchbaseE;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmbeddedLoaderController extends AbstractLoaderController {

    private static final String E = "`bucket-main`.`spring_scope_e`";

    private static final List<String> EMBEDDED_COLLECTIONS = List.of(
            "OrdersEWithLineitems",
            "OrdersEWithLineitemsArrayAsTags",
            "OrdersEWithLineitemsArrayAsTagsIndexed",
            "OrdersEWithCustomerWithNationWithRegion",
            "OrdersEOnlyOComment",
            "OrdersEOnlyOCommentIndexed"
    );

    private final TPCHDatasetLoaderCouchbaseE loader;
    private final Cluster cluster;

    public EmbeddedLoaderController(TPCHDatasetLoaderCouchbaseE loader, Cluster cluster) {
        this.loader = loader;
        this.cluster = cluster;
    }

    @PostMapping("/loadEmbedded")
    public ResponseEntity<String> loadEmbeddedData() {
        return executeLoad("Embedded");
    }

    @Override
    public boolean isAlreadyLoaded() {
        return cluster.bucket("bucket-main").defaultCollection().exists("load_e_complete").exists();
    }

    @Override
    public void dropCollections() {
        EMBEDDED_COLLECTIONS.forEach(col -> cluster.query("DELETE FROM " + E + ".`" + col + "`"));
    }

    @Override
    public void loadAllData(String dataPath) {
        loader.loadAll(dataPath);
    }

    @Override
    public void insertSentinel() {
        cluster.bucket("bucket-main").defaultCollection().upsert("load_e_complete", JsonObject.create());
    }
}
