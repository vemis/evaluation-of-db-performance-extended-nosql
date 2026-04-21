package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "microservice-couchbase-ottomanjs-javascript", contextId = "ottomanJSCouchbaseEmbeddedClient")
public interface MicroserviceCouchbaseOttomanjsJavascriptIODMEmbeddedClient extends IODMEmbeddedClient {}
