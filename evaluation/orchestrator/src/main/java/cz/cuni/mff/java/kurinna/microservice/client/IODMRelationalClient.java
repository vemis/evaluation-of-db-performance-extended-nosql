package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

public interface IODMRelationalClient extends IORMClient {
    @PostMapping("/load")
    ResponseEntity<String> load();
}
