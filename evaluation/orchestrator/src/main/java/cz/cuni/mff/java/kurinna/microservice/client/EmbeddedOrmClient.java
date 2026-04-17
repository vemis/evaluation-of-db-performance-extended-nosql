package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface EmbeddedOrmClient {

    @GetMapping("/r1")
    ResponseEntity<Map<String, Object>> getEmbeddedR1(@RequestParam int repetitions);

    @GetMapping("/r2")
    ResponseEntity<Map<String, Object>> getEmbeddedR2(@RequestParam int repetitions);

    @GetMapping("/r3")
    ResponseEntity<Map<String, Object>> getEmbeddedR3(@RequestParam int repetitions);

    @GetMapping("/r4")
    ResponseEntity<Map<String, Object>> getEmbeddedR4(@RequestParam int repetitions);

    @GetMapping("/r5")
    ResponseEntity<Map<String, Object>> getEmbeddedR5(@RequestParam int repetitions);

    @GetMapping("/r6")
    ResponseEntity<Map<String, Object>> getEmbeddedR6(@RequestParam int repetitions);

    @GetMapping("/r7")
    ResponseEntity<Map<String, Object>> getEmbeddedR7(@RequestParam int repetitions);

    @GetMapping("/r8")
    ResponseEntity<Map<String, Object>> getEmbeddedR8(@RequestParam int repetitions);

    @GetMapping("/r9")
    ResponseEntity<Map<String, Object>> getEmbeddedR9(@RequestParam int repetitions);
}
