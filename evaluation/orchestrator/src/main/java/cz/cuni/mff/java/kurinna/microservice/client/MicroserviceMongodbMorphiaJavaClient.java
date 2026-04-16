package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "microservice-mongodb-morphia-java")
public interface MicroserviceMongodbMorphiaJavaClient extends OrmClient {

    @PostMapping("/load")
    ResponseEntity<String> load();
}
