package cz.cuni.mff.java.microservice.controller;

import com.couchbase.client.java.Cluster;
import cz.cuni.mff.java.kurinna.common.controller.IEmbeddedLoaderController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderCouchbaseE;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmbeddedLoaderController implements IEmbeddedLoaderController {

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

    @Value("${tpch.data.path:/data/tpch-data-small}")
    private String dataPath;

    public EmbeddedLoaderController(TPCHDatasetLoaderCouchbaseE loader, Cluster cluster) {
        this.loader = loader;
        this.cluster = cluster;
    }

    @Override
    @PostMapping("/loadEmbedded")
    public ResponseEntity<String> loadEmbeddedData() {
        EMBEDDED_COLLECTIONS.forEach(col ->
                cluster.query("DELETE FROM " + E + ".`" + col + "`"));
        loader.loadAll(dataPath);
        return ResponseEntity.ok("Embedded data loaded from: " + dataPath);
    }
}
