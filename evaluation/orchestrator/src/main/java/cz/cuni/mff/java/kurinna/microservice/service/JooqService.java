package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMysqlJooqClient;
import cz.cuni.mff.java.kurinna.microservice.client.IORMClient;
import org.springframework.stereotype.Service;

@Service
public class JooqService extends AbstractOrmService {

    private final MicroserviceMysqlJooqClient client;

    public JooqService(MicroserviceMysqlJooqClient client) {
        this.client = client;
    }

    @Override
    protected IORMClient client() { return client; }
}
