package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "microservice-mysql-ebean")
public interface MicroserviceMysqlEbeanClient extends OrmClient {}
