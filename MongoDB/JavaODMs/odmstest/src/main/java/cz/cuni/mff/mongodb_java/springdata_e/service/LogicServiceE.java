package cz.cuni.mff.mongodb_java.springdata_e.service;

import cz.cuni.mff.mongodb_java.springdata_e.benchmarks.QueriesSpringDataE;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogicServiceE {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public LogicServiceE(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public  List<Document> C2(){
        return QueriesSpringDataE.C2(mongoTemplate);
    }

}
