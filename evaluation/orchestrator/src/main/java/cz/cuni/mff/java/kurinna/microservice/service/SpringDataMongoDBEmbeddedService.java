package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.IODMEmbeddedClient;
import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMongodbSpringdataJavaIODMEmbeddedClient;
import org.springframework.stereotype.Service;

@Service
public class SpringDataMongoDBEmbeddedService extends AbstractEmbeddedOrmService {

    private final MicroserviceMongodbSpringdataJavaIODMEmbeddedClient embeddedClient;

    public SpringDataMongoDBEmbeddedService(MicroserviceMongodbSpringdataJavaIODMEmbeddedClient embeddedClient) {
        this.embeddedClient = embeddedClient;
    }

    @Override
    protected IODMEmbeddedClient embeddedClient() { return embeddedClient; }

    public String loadEmbedded() { return embeddedClient.loadEmbedded().getBody(); }
}
