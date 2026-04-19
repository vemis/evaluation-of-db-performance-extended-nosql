package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.IODMEmbeddedClient;
import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceCouchbaseSpringdataJavaIODMEmbeddedClient;
import org.springframework.stereotype.Service;

@Service
public class CouchbaseSpringDataEmbeddedService extends AbstractEmbeddedOrmService {

    private final MicroserviceCouchbaseSpringdataJavaIODMEmbeddedClient embeddedClient;

    public CouchbaseSpringDataEmbeddedService(MicroserviceCouchbaseSpringdataJavaIODMEmbeddedClient embeddedClient) {
        this.embeddedClient = embeddedClient;
    }

    @Override
    protected IODMEmbeddedClient embeddedClient() { return embeddedClient; }

    public String loadEmbedded() { return embeddedClient.loadEmbedded().getBody(); }
}
