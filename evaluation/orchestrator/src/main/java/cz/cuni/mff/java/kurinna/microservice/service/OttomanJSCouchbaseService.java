package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceCouchbaseOttomanjsJavascriptClient;
import cz.cuni.mff.java.kurinna.microservice.client.IORMClient;
import org.springframework.stereotype.Service;

@Service
public class OttomanJSCouchbaseService extends AbstractOrmService {

    private final MicroserviceCouchbaseOttomanjsJavascriptClient ottomanClient;

    public OttomanJSCouchbaseService(MicroserviceCouchbaseOttomanjsJavascriptClient ottomanClient) {
        this.ottomanClient = ottomanClient;
    }

    @Override
    protected IORMClient client() { return ottomanClient; }

    public String health() { return ottomanClient.health().getBody(); }

    public String load() { return ottomanClient.load().getBody(); }
}
