package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "microservice-mysql-springdatajpa")
public interface MicroserviceMysqlSpringDataJpaClient extends IORMClient {}
