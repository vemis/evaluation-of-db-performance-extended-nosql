package cz.cuni.mff.java.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.AbstractLoaderController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderSpringDataR;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    private final TPCHDatasetLoaderSpringDataR loader;
    private final MongoTemplate mongoTemplate;

    public LoaderController(TPCHDatasetLoaderSpringDataR loader, MongoTemplate mongoTemplate) {
        this.loader = loader;
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("/load")
    public ResponseEntity<String> loadData() {
        return executeLoad("Relational");
    }

    @Override
    public boolean isAlreadyLoaded() {
        return mongoTemplate.exists(
                Query.query(Criteria.where("_id").is("load_r_complete")),
                "_metadata"
        );
    }

    @Override
    public void dropCollections() {
        COLLECTIONS.forEach(mongoTemplate::dropCollection);
    }

    @Override
    public void loadAllData(String dataPath) {
        loader.loadAll(dataPath);
    }

    @Override
    public void insertSentinel() {
        mongoTemplate.insert(new Document("_id", "load_r_complete"), "_metadata");
    }
}
