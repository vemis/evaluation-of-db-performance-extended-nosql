package cz.cuni.mff.java.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.AbstractLoaderController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderMorphiaE;
import dev.morphia.Datastore;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmbeddedLoaderController extends AbstractLoaderController {

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

    public EmbeddedLoaderController(TPCHDatasetLoaderMorphiaE loader, Datastore datastore) {
        this.loader = loader;
        this.datastore = datastore;
    }

    @PostMapping("/loadEmbedded")
    public ResponseEntity<String> loadEmbeddedData() {
        return executeLoad("Embedded");
    }

    @Override
    public boolean isAlreadyLoaded() {
        return datastore.getDatabase().getCollection("_metadata")
                .find(new Document("_id", "load_e_complete")).first() != null;
    }

    @Override
    public void dropCollections() {
        EMBEDDED_COLLECTIONS.forEach(col -> datastore.getDatabase().getCollection(col).drop());
    }

    @Override
    public void loadAllData(String dataPath) {
        loader.loadAll(dataPath);
    }

    @Override
    public void insertSentinel() {
        datastore.getDatabase().getCollection("_metadata")
                .insertOne(new Document("_id", "load_e_complete"));
    }
}
