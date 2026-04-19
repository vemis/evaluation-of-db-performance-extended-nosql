package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMysqlEbeanClient;
import cz.cuni.mff.java.kurinna.microservice.client.IORMClient;
import org.springframework.stereotype.Service;

@Service
public class EbeanService extends AbstractOrmService {

    private final MicroserviceMysqlEbeanClient client;

    public EbeanService(MicroserviceMysqlEbeanClient client) {
        this.client = client;
    }

    @Override
    protected IORMClient client() { return client; }
}
