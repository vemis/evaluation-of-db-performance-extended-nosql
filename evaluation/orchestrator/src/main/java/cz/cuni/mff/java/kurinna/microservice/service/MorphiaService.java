package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMongodbMorphiaJavaClient;
import cz.cuni.mff.java.kurinna.microservice.client.OrmClient;
import org.springframework.stereotype.Service;

@Service
public class MorphiaService extends AbstractOrmService {

    private final MicroserviceMongodbMorphiaJavaClient morphiaClient;

    public MorphiaService(MicroserviceMongodbMorphiaJavaClient morphiaClient) {
        this.morphiaClient = morphiaClient;
    }

    @Override
    protected OrmClient client() { return morphiaClient; }

    public String health() { return morphiaClient.health().getBody(); }

    public String load() { return morphiaClient.load().getBody(); }
}
