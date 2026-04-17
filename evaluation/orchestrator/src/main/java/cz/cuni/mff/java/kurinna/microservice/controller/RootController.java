package cz.cuni.mff.java.kurinna.microservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

import static cz.cuni.mff.java.kurinna.microservice.utils.Utils.*;

@RestController
@RequestMapping("/")
public class RootController {
    @GetMapping("/getQueryDescriptions")
    public ResponseEntity<Map<String, String>> getQueryDescriptions() {
        return ResponseEntity.ok(new LinkedHashMap<>(QUERY_DESCRIPTIONS));
    }

    @GetMapping("/getQueryEndpoints")
    public ResponseEntity<String[]> getQueryRoutes() {
        return ResponseEntity.ok(ALL_QUERIES);
    }

    @GetMapping("/getEmbeddedQueryEndpoints")
    public ResponseEntity<String[]> getEmbeddedQueryRoutes() {
        return ResponseEntity.ok(ALL_EMBEDDED_QUERIES);
    }

    @GetMapping("/getMicroserviceEndpoints")
    public ResponseEntity<String[]> getMicroserviceRoutes() {
        return ResponseEntity.ok(ALL_SERVICES);
    }
}
