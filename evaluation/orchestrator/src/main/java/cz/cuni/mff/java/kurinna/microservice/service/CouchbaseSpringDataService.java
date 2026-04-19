package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceCouchbaseSpringdataJavaClient;
import cz.cuni.mff.java.kurinna.microservice.client.IORMClient;
import org.springframework.stereotype.Service;

@Service
public class CouchbaseSpringDataService extends AbstractOrmService {

    private final MicroserviceCouchbaseSpringdataJavaClient couchbaseClient;

    public CouchbaseSpringDataService(MicroserviceCouchbaseSpringdataJavaClient couchbaseClient) {
        this.couchbaseClient = couchbaseClient;
    }

    @Override
    protected IORMClient client() { return couchbaseClient; }

    public String health() { return couchbaseClient.health().getBody(); }

    public String load() { return couchbaseClient.load().getBody(); }
}
