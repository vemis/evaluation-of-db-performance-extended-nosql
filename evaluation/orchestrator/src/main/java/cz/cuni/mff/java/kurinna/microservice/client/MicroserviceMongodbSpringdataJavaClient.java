package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "microservice-mongodb-springdata-java", contextId = "springDataMongoDBRelationalClient")
public interface MicroserviceMongodbSpringdataJavaClient extends IODMRelationalClient {}
