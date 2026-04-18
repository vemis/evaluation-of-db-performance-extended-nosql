package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.EmbeddedOrmClient;
import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceCouchbaseSpringdataJavaEmbeddedClient;
import org.springframework.stereotype.Service;

@Service
public class CouchbaseSpringDataEmbeddedService extends AbstractEmbeddedOrmService {

    private final MicroserviceCouchbaseSpringdataJavaEmbeddedClient embeddedClient;

    public CouchbaseSpringDataEmbeddedService(MicroserviceCouchbaseSpringdataJavaEmbeddedClient embeddedClient) {
        this.embeddedClient = embeddedClient;
    }

    @Override
    protected EmbeddedOrmClient embeddedClient() { return embeddedClient; }

    public String loadEmbedded() { return embeddedClient.loadEmbedded().getBody(); }
}
