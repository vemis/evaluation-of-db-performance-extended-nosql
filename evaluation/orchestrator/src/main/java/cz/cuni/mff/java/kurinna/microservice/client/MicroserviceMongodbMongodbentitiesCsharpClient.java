package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "microservice-mongodb-mongodbentities-csharp", contextId = "mongoDBEntitiesCSharpRelationalClient")
public interface MicroserviceMongodbMongodbentitiesCsharpClient extends IODMRelationalClient {}
