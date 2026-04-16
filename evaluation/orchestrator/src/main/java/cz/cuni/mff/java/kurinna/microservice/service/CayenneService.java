package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMysqlCayenneClient;
import cz.cuni.mff.java.kurinna.microservice.client.OrmClient;
import org.springframework.stereotype.Service;

@Service
public class CayenneService extends AbstractOrmService {

    private final MicroserviceMysqlCayenneClient client;

    public CayenneService(MicroserviceMysqlCayenneClient client) {
        this.client = client;
    }

    @Override
    protected OrmClient client() { return client; }
}
