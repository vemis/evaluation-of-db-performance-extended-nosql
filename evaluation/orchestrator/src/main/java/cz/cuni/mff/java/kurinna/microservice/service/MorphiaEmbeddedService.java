package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.EmbeddedOrmClient;
import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMongodbMorphiaJavaEmbeddedClient;
import org.springframework.stereotype.Service;

@Service
public class MorphiaEmbeddedService extends AbstractEmbeddedOrmService {

    private final MicroserviceMongodbMorphiaJavaEmbeddedClient embeddedClient;

    public MorphiaEmbeddedService(MicroserviceMongodbMorphiaJavaEmbeddedClient embeddedClient) {
        this.embeddedClient = embeddedClient;
    }

    @Override
    protected EmbeddedOrmClient embeddedClient() { return embeddedClient; }

    public String loadEmbedded() { return embeddedClient.loadEmbedded().getBody(); }
}
