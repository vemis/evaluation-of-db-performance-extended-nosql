package cz.cuni.mff.java.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.AbstractEmbeddedQueryController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderMorphiaE;
import cz.cuni.mff.java.microservice.service.EmbeddedQueryService;
import dev.morphia.Datastore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmbeddedQueryController extends AbstractEmbeddedQueryController<Object> {

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

    public EmbeddedQueryController(EmbeddedQueryService service,
                                   TPCHDatasetLoaderMorphiaE loader,
                                   Datastore datastore) {
        super(service);
        this.loader = loader;
        this.datastore = datastore;
    }

    /**
     * Drops all embedded collections and reloads fresh data from the configured
     * {@code tpch.data.path}. Called by the orchestrator on every startup.
     */
    @PostMapping("/loadEmbedded")
    public ResponseEntity<String> loadEmbeddedData() {
        EMBEDDED_COLLECTIONS.forEach(col ->
                datastore.getDatabase().getCollection(col).drop());
        loader.loadAll(dataPath);
        return ResponseEntity.ok("Embedded data loaded from: " + dataPath);
    }
}
