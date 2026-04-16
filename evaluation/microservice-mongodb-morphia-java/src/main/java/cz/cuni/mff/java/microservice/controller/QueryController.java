package cz.cuni.mff.java.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.AbstractQueryController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderMorphiaR;
import cz.cuni.mff.java.microservice.service.QueryService;
import dev.morphia.Datastore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QueryController extends AbstractQueryController<Object> {

    private static final List<String> COLLECTIONS = List.of(
            "regionR", "nationR", "customerR", "ordersR",
            "lineitemR", "partsuppR", "partR", "supplierR"
    );

    private final TPCHDatasetLoaderMorphiaR loader;
    private final Datastore datastore;

    @Value("${tpch.data.path:/data/tpch-data-small}")
    private String dataPath;

    public QueryController(QueryService service, TPCHDatasetLoaderMorphiaR loader,
                           Datastore datastore) {
        super(service);
        this.loader = loader;
        this.datastore = datastore;
    }

    /**
     * Drops all TPC-H collections and reloads fresh data from the configured
     * {@code tpch.data.path}. Called by the orchestrator on every startup.
     */
    @PostMapping("/load")
    public ResponseEntity<String> loadData() {
        COLLECTIONS.forEach(col ->
                datastore.getDatabase().getCollection(col).drop());
        loader.loadAll(dataPath);
        return ResponseEntity.ok("Data loaded from: " + dataPath);
    }
}
