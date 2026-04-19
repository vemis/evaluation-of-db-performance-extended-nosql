package cz.cuni.mff.java.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.AbstractLoaderController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderMorphiaR;
import dev.morphia.Datastore;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoaderController extends AbstractLoaderController {

    private static final List<String> COLLECTIONS = List.of(
            "regionR", "nationR", "customerR", "ordersR",
            "lineitemR", "partsuppR", "partR", "supplierR"
    );

    private final TPCHDatasetLoaderMorphiaR loader;
    private final Datastore datastore;

    public LoaderController(TPCHDatasetLoaderMorphiaR loader, Datastore datastore) {
        this.loader = loader;
        this.datastore = datastore;
    }

    @PostMapping("/load")
    public ResponseEntity<String> loadData() {
        return executeLoad("Relational");
    }

    @Override
    public boolean isAlreadyLoaded() {
        return datastore.getDatabase().getCollection("_metadata")
                .find(new Document("_id", "load_r_complete")).first() != null;
    }

    @Override
    public void dropCollections() {
        COLLECTIONS.forEach(col -> datastore.getDatabase().getCollection(col).drop());
    }

    @Override
    public void loadAllData(String dataPath) {
        loader.loadAll(dataPath);
    }

    @Override
    public void insertSentinel() {
        datastore.getDatabase().getCollection("_metadata")
                .insertOne(new Document("_id", "load_r_complete"));
    }
}
