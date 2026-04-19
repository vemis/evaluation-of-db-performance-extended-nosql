package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "microservice-mysql-jooq")
public interface MicroserviceMysqlJooqClient extends IORMClient {}
