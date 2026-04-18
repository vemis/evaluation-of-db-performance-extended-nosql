package cz.cuni.mff.java.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.ILoaderController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderMorphiaR;
import dev.morphia.Datastore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoaderController implements ILoaderController {

    private static final List<String> COLLECTIONS = List.of(
            "regionR", "nationR", "customerR", "ordersR",
            "lineitemR", "partsuppR", "partR", "supplierR"
    );

    private final TPCHDatasetLoaderMorphiaR loader;
    private final Datastore datastore;

    @Value("${tpch.data.path:/data/tpch-data-small}")
    private String dataPath;

    public LoaderController(TPCHDatasetLoaderMorphiaR loader, Datastore datastore) {
        this.loader = loader;
        this.datastore = datastore;
    }

    @Override
    @PostMapping("/load")
    public ResponseEntity<String> loadData() {
        COLLECTIONS.forEach(col ->
                datastore.getDatabase().getCollection(col).drop());
        loader.loadAll(dataPath);
        return ResponseEntity.ok("Data loaded from: " + dataPath);
    }
}
