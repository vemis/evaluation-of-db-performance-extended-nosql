package cz.cuni.mff.java.microservice.controller;

import com.couchbase.client.java.Cluster;
import cz.cuni.mff.java.kurinna.common.controller.ILoaderController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderCouchbaseR;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoaderController implements ILoaderController {

    private static final String R = "`bucket-main`.`spring_scope_r`";

    private static final List<String> COLLECTIONS = List.of(
            "RegionR", "NationR", "CustomerR", "OrdersR",
            "LineitemR", "SupplierR", "PartR", "PartsuppR"
    );

    private final TPCHDatasetLoaderCouchbaseR loader;
    private final Cluster cluster;

    @Value("${tpch.data.path:/data/tpch-data-small}")
    private String dataPath;

    public LoaderController(TPCHDatasetLoaderCouchbaseR loader, Cluster cluster) {
        this.loader = loader;
        this.cluster = cluster;
    }

    @Override
    @PostMapping("/load")
    public ResponseEntity<String> loadData() {
        COLLECTIONS.forEach(col ->
                cluster.query("DELETE FROM " + R + ".`" + col + "`"));
        loader.loadAll(dataPath);
        return ResponseEntity.ok("Relational data loaded from: " + dataPath);
    }
}
