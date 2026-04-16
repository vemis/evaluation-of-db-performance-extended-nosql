package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMysqlJdbcClient;
import cz.cuni.mff.java.kurinna.microservice.client.OrmClient;
import org.springframework.stereotype.Service;

@Service
public class JdbcService extends AbstractOrmService {

    private final MicroserviceMysqlJdbcClient client;

    public JdbcService(MicroserviceMysqlJdbcClient client) {
        this.client = client;
    }

    @Override
    protected OrmClient client() { return client; }
}
