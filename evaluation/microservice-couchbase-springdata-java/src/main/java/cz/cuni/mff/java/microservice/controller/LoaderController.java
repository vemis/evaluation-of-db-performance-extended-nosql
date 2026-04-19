package cz.cuni.mff.java.microservice.controller;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import cz.cuni.mff.java.kurinna.common.controller.AbstractLoaderController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderCouchbaseR;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoaderController extends AbstractLoaderController {

    private static final String R = "`bucket-main`.`spring_scope_r`";

    private static final List<String> COLLECTIONS = List.of(
            "RegionR", "NationR", "CustomerR", "OrdersR",
            "LineitemR", "SupplierR", "PartR", "PartsuppR"
    );

    private final TPCHDatasetLoaderCouchbaseR loader;
    private final Cluster cluster;

    public LoaderController(TPCHDatasetLoaderCouchbaseR loader, Cluster cluster) {
        this.loader = loader;
        this.cluster = cluster;
    }

    @PostMapping("/load")
    public ResponseEntity<String> loadData() {
        return executeLoad("Relational");
    }

    @Override
    public boolean isAlreadyLoaded() {
        return cluster.bucket("bucket-main").defaultCollection().exists("load_r_complete").exists();
    }

    @Override
    public void dropCollections() {
        COLLECTIONS.forEach(col -> cluster.query("DELETE FROM " + R + ".`" + col + "`"));
    }

    @Override
    public void loadAllData(String dataPath) {
        loader.loadAll(dataPath);
    }

    @Override
    public void insertSentinel() {
        cluster.bucket("bucket-main").defaultCollection().upsert("load_r_complete", JsonObject.create());
    }
}
