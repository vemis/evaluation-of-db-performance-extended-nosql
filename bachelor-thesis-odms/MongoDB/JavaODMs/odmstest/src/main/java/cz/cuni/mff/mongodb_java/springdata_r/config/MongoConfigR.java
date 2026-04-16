package cz.cuni.mff.mongodb_java.springdata_r.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import cz.cuni.mff.mongodb_java.springdata_r.benchmarks.QueriesSpringDataR;
import cz.cuni.mff.mongodb_java.springdata_r.model.LineitemR;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Configuration
public class MongoConfigR extends AbstractMongoClientConfiguration {
    private static final String DATABASE_NAME = "springdata_database_r";

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, DATABASE_NAME);
    }

    @Override
    protected String getDatabaseName() {
        return DATABASE_NAME;
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }


}
