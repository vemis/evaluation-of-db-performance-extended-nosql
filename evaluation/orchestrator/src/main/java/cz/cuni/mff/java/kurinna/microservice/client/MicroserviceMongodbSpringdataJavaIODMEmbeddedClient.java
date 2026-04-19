package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "microservice-mongodb-springdata-java", contextId = "springDataMongoDBEmbeddedClient")
public interface MicroserviceMongodbSpringdataJavaIODMEmbeddedClient extends IODMEmbeddedClient {}
