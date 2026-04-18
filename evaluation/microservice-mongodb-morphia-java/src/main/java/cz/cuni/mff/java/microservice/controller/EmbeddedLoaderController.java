package cz.cuni.mff.java.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.IEmbeddedLoaderController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderMorphiaE;
import dev.morphia.Datastore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmbeddedLoaderController implements IEmbeddedLoaderController {

    private static final List<String> EMBEDDED_COLLECTIONS = List.of(
            "ordersEWithLineitems",
            "ordersEWithLineitemsArrayAsTags",
            "ordersEWithLineitemsArrayAsTagsIndexed",
            "ordersEWithCustomerWithNationWithRegion",
            "ordersEOnlyOComment",
            "ordersEOnlyOCommentIndexed"
    );

    private final TPCHDatasetLoaderMorphiaE loader;
    private final Datastore datastore;

    @Value("${tpch.data.path:/data/tpch-data-small}")
    private String dataPath;

    public EmbeddedLoaderController(TPCHDatasetLoaderMorphiaE loader, Datastore datastore) {
        this.loader = loader;
        this.datastore = datastore;
    }

    @Override
    @PostMapping("/loadEmbedded")
    public ResponseEntity<String> loadEmbeddedData() {
        EMBEDDED_COLLECTIONS.forEach(col ->
                datastore.getDatabase().getCollection(col).drop());
        loader.loadAll(dataPath);
        return ResponseEntity.ok("Embedded data loaded from: " + dataPath);
    }
}
