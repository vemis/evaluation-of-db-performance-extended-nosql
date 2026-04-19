package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.IODMEmbeddedClient;
import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMongodbMorphiaJavaIODMEmbeddedClient;
import org.springframework.stereotype.Service;

@Service
public class MorphiaEmbeddedService extends AbstractEmbeddedOrmService {

    private final MicroserviceMongodbMorphiaJavaIODMEmbeddedClient embeddedClient;

    public MorphiaEmbeddedService(MicroserviceMongodbMorphiaJavaIODMEmbeddedClient embeddedClient) {
        this.embeddedClient = embeddedClient;
    }

    @Override
    protected IODMEmbeddedClient embeddedClient() { return embeddedClient; }

    public String loadEmbedded() { return embeddedClient.loadEmbedded().getBody(); }
}
