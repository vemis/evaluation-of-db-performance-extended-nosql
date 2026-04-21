package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.IODMEmbeddedClient;
import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceCouchbaseOttomanjsJavascriptIODMEmbeddedClient;
import org.springframework.stereotype.Service;

@Service
public class OttomanJSCouchbaseEmbeddedService extends AbstractEmbeddedOrmService {

    private final MicroserviceCouchbaseOttomanjsJavascriptIODMEmbeddedClient embeddedClient;

    public OttomanJSCouchbaseEmbeddedService(MicroserviceCouchbaseOttomanjsJavascriptIODMEmbeddedClient embeddedClient) {
        this.embeddedClient = embeddedClient;
    }

    @Override
    protected IODMEmbeddedClient embeddedClient() { return embeddedClient; }

    public String loadEmbedded() { return embeddedClient.loadEmbedded().getBody(); }
}
