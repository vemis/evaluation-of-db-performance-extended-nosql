package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMongodbMongodbentitiesCsharpClient;
import cz.cuni.mff.java.kurinna.microservice.client.IORMClient;
import org.springframework.stereotype.Service;

@Service
public class MongoDBEntitiesCSharpService extends AbstractOrmService {

    private final MicroserviceMongodbMongodbentitiesCsharpClient client;

    public MongoDBEntitiesCSharpService(MicroserviceMongodbMongodbentitiesCsharpClient client) {
        this.client = client;
    }

    @Override
    protected IORMClient client() { return client; }

    public String health() { return client.health().getBody(); }

    public String load() { return client.load().getBody(); }
}
