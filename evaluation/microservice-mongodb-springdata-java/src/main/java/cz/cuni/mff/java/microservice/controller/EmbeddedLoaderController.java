package cz.cuni.mff.java.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.AbstractLoaderController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderSpringDataE;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    private final TPCHDatasetLoaderSpringDataE loader;
    private final MongoTemplate mongoTemplate;

    public EmbeddedLoaderController(TPCHDatasetLoaderSpringDataE loader, MongoTemplate mongoTemplate) {
        this.loader = loader;
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("/loadEmbedded")
    public ResponseEntity<String> loadEmbeddedData() {
        return executeLoad("Embedded");
    }

    @Override
    public boolean isAlreadyLoaded() {
        return mongoTemplate.exists(
                Query.query(Criteria.where("_id").is("load_e_complete")),
                "_metadata"
        );
    }

    @Override
    public void dropCollections() {
        EMBEDDED_COLLECTIONS.forEach(mongoTemplate::dropCollection);
    }

    @Override
    public void loadAllData(String dataPath) {
        loader.loadAll(dataPath);
    }

    @Override
    public void insertSentinel() {
        mongoTemplate.insert(new Document("_id", "load_e_complete"), "_metadata");
    }
}
