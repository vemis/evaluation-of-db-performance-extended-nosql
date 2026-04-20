package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMongodbMongooseJavascriptClient;
import cz.cuni.mff.java.kurinna.microservice.client.IORMClient;
import org.springframework.stereotype.Service;

@Service
public class MongooseMongoDBService extends AbstractOrmService {

    private final MicroserviceMongodbMongooseJavascriptClient mongooseClient;

    public MongooseMongoDBService(MicroserviceMongodbMongooseJavascriptClient mongooseClient) {
        this.mongooseClient = mongooseClient;
    }

    @Override
    protected IORMClient client() { return mongooseClient; }

    public String health() { return mongooseClient.health().getBody(); }

    public String load() { return mongooseClient.load().getBody(); }
}
