package cz.cuni.mff.mongodb_java.springdata_r.service;

import cz.cuni.mff.mongodb_java.springdata_r.benchmarks.QueriesSpringDataR;
import cz.cuni.mff.mongodb_java.springdata_r.model.CustomerR;
import cz.cuni.mff.mongodb_java.springdata_r.model.LineitemR;
import cz.cuni.mff.mongodb_java.springdata_r.model.OrdersR;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogicServiceR {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public LogicServiceR(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public List<LineitemR> A1(){
        return QueriesSpringDataR.A1(mongoTemplate);
    }

    public List<OrdersR> A2(){
        return QueriesSpringDataR.A2(mongoTemplate);
    }

    public List<CustomerR> A3(){
        return QueriesSpringDataR.A3(mongoTemplate);
    }

    public List<OrdersR> A4(){
        return QueriesSpringDataR.A4(mongoTemplate);
    }

    public List<Document> B1(){
        return QueriesSpringDataR.B1(mongoTemplate);
    }

    public List<Document> B2(){
        return QueriesSpringDataR.B2(mongoTemplate);
    }

    public List<Document> C1(){
        return QueriesSpringDataR.C1(mongoTemplate);
    }

    public List<Document> C2(){
        return QueriesSpringDataR.C2(mongoTemplate);
    }

    public List<Document> C3(){
        return QueriesSpringDataR.C3(mongoTemplate);
    }

    public List<Document> C4(){
        return QueriesSpringDataR.C4(mongoTemplate);
    }

    public List<Document> C5(){
        return QueriesSpringDataR.C5(mongoTemplate);
    }

    public List<Document> D1(){
        return QueriesSpringDataR.D1(mongoTemplate);
    }

    public List<Document> D2(){
        return QueriesSpringDataR.D2(mongoTemplate);
    }

    public List<Document> D3(){
        return QueriesSpringDataR.D3(mongoTemplate);
    }

    public List<Document> E1(){
        return QueriesSpringDataR.E1(mongoTemplate);
    }

    public List<OrdersR> E2(){
        return QueriesSpringDataR.E2(mongoTemplate);
    }

    public List<Document> E3(){
        return QueriesSpringDataR.E3(mongoTemplate);
    }

    public List<Document> Q1(){
        return QueriesSpringDataR.Q1(mongoTemplate);
    }

    public List<Document> Q2(){
        return QueriesSpringDataR.Q2(mongoTemplate);
    }

    public List<Document> Q3(){
        return QueriesSpringDataR.Q3(mongoTemplate);
    }

    public List<Document> Q4(){
        return QueriesSpringDataR.Q4(mongoTemplate);
    }

    public List<Document> Q5(){
        return QueriesSpringDataR.Q5(mongoTemplate);
    }

}
