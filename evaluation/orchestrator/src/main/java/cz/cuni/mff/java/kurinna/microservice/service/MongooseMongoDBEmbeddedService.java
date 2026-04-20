package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.IODMEmbeddedClient;
import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMongodbMongooseJavascriptIODMEmbeddedClient;
import org.springframework.stereotype.Service;

@Service
public class MongooseMongoDBEmbeddedService extends AbstractEmbeddedOrmService {

    private final MicroserviceMongodbMongooseJavascriptIODMEmbeddedClient embeddedClient;

    public MongooseMongoDBEmbeddedService(MicroserviceMongodbMongooseJavascriptIODMEmbeddedClient embeddedClient) {
        this.embeddedClient = embeddedClient;
    }

    @Override
    protected IODMEmbeddedClient embeddedClient() { return embeddedClient; }

    public String loadEmbedded() { return embeddedClient.loadEmbedded().getBody(); }
}
