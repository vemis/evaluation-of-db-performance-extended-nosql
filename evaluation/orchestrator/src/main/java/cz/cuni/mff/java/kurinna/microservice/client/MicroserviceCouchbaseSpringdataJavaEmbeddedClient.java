package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "microservice-couchbase-springdata-java", contextId = "couchbaseSpringDataEmbeddedClient")
public interface MicroserviceCouchbaseSpringdataJavaEmbeddedClient extends EmbeddedOrmClient {

    @PostMapping("/loadEmbedded")
    ResponseEntity<String> loadEmbedded();
}
