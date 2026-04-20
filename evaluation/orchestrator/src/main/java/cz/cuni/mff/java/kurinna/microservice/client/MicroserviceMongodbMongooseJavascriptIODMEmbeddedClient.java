package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "microservice-mongodb-mongoose-javascript", contextId = "mongooseMongoDBEmbeddedClient")
public interface MicroserviceMongodbMongooseJavascriptIODMEmbeddedClient extends IODMEmbeddedClient {}
