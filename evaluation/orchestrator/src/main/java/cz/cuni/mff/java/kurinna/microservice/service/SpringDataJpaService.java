package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMysqlSpringDataJpaClient;
import cz.cuni.mff.java.kurinna.microservice.client.IORMClient;
import org.springframework.stereotype.Service;

@Service
public class SpringDataJpaService extends AbstractOrmService {

    private final MicroserviceMysqlSpringDataJpaClient client;

    public SpringDataJpaService(MicroserviceMysqlSpringDataJpaClient client) {
        this.client = client;
    }

    @Override
    protected IORMClient client() { return client; }
}
