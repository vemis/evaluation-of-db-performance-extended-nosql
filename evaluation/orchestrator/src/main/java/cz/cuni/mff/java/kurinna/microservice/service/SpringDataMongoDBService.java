package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMongodbSpringdataJavaClient;
import cz.cuni.mff.java.kurinna.microservice.client.IORMClient;
import org.springframework.stereotype.Service;

@Service
public class SpringDataMongoDBService extends AbstractOrmService {

    private final MicroserviceMongodbSpringdataJavaClient springDataMongoDBClient;

    public SpringDataMongoDBService(MicroserviceMongodbSpringdataJavaClient springDataMongoDBClient) {
        this.springDataMongoDBClient = springDataMongoDBClient;
    }

    @Override
    protected IORMClient client() { return springDataMongoDBClient; }

    public String health() { return springDataMongoDBClient.health().getBody(); }

    public String load() { return springDataMongoDBClient.load().getBody(); }
}
