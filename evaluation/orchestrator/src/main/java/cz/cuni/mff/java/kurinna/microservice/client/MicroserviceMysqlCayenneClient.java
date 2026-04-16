package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "microservice-mysql-cayenne")
public interface MicroserviceMysqlCayenneClient extends OrmClient {}
