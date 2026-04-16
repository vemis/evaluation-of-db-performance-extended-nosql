package cz.cuni.mff.java.microservice.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MorphiaConfig {

    @Value("${mongodb.uri:mongodb://localhost:27017}")
    private String mongoUri;

    @Value("${mongodb.database:morphia_tpch_relational}")
    private String database;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean
    public Datastore datastore(MongoClient mongoClient) {
        MapperOptions options = MapperOptions.builder()
                .storeNulls(false)
                .build();

        Datastore datastore = Morphia.createDatastore(mongoClient, database, options);
        datastore.getMapper().mapPackage("cz.cuni.mff.java.microservice.models");
        datastore.ensureIndexes();
        return datastore;
    }
}
