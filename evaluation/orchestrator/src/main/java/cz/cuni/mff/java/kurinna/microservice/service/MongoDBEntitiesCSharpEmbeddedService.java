package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.IODMEmbeddedClient;
import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMongodbMongodbentitiesCsharpIODMEmbeddedClient;
import org.springframework.stereotype.Service;

@Service
public class MongoDBEntitiesCSharpEmbeddedService extends AbstractEmbeddedOrmService {

    private final MicroserviceMongodbMongodbentitiesCsharpIODMEmbeddedClient embeddedClient;

    public MongoDBEntitiesCSharpEmbeddedService(MicroserviceMongodbMongodbentitiesCsharpIODMEmbeddedClient embeddedClient) {
        this.embeddedClient = embeddedClient;
    }

    @Override
    protected IODMEmbeddedClient embeddedClient() { return embeddedClient; }

    public String loadEmbedded() { return embeddedClient.loadEmbedded().getBody(); }
}
