package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "microservice-mongodb-mongoose-javascript", contextId = "mongooseMongoDBRelationalClient")
public interface MicroserviceMongodbMongooseJavascriptClient extends IODMRelationalClient {}
