package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.IODMEmbeddedClient;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public abstract class AbstractEmbeddedOrmService {

    protected abstract IODMEmbeddedClient embeddedClient();

    private Map<String, Object> fetch(ResponseEntity<Map<String, Object>> response) {
        Map<String, Object> body = response.getBody();
        if (body == null || body.isEmpty()) throw new RuntimeException("No data found");
        return body;
    }

    public Map<String, Object> executeEmbeddedQuery(String queryId, int repetitions) {
        return switch (queryId) {
            case "r1" -> fetch(embeddedClient().getEmbeddedR1(repetitions));
            case "r2" -> fetch(embeddedClient().getEmbeddedR2(repetitions));
            case "r3" -> fetch(embeddedClient().getEmbeddedR3(repetitions));
            case "r4" -> fetch(embeddedClient().getEmbeddedR4(repetitions));
            case "r5" -> fetch(embeddedClient().getEmbeddedR5(repetitions));
            case "r6" -> fetch(embeddedClient().getEmbeddedR6(repetitions));
            case "r7" -> fetch(embeddedClient().getEmbeddedR7(repetitions));
            case "r8" -> fetch(embeddedClient().getEmbeddedR8(repetitions));
            case "r9" -> fetch(embeddedClient().getEmbeddedR9(repetitions));
            default -> throw new IllegalArgumentException("Unknown embedded query: " + queryId);
        };
    }
}
