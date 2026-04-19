package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "microservice-mongodb-morphia-java", contextId = "morphiaEmbeddedClient")
public interface MicroserviceMongodbMorphiaJavaIODMEmbeddedClient extends IODMEmbeddedClient {}
