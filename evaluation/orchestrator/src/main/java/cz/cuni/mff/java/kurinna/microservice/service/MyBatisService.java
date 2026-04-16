package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMysqlMyBatisClient;
import cz.cuni.mff.java.kurinna.microservice.client.OrmClient;
import org.springframework.stereotype.Service;

@Service
public class MyBatisService extends AbstractOrmService {

    private final MicroserviceMysqlMyBatisClient client;

    public MyBatisService(MicroserviceMysqlMyBatisClient client) {
        this.client = client;
    }

    @Override
    protected OrmClient client() { return client; }
}
