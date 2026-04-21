package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "microservice-mongodb-mongodbentities-csharp", contextId = "mongoDBEntitiesCSharpEmbeddedClient")
public interface MicroserviceMongodbMongodbentitiesCsharpIODMEmbeddedClient extends IODMEmbeddedClient {}
