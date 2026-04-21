package cz.cuni.mff.java.kurinna.microservice.initializer;

import cz.cuni.mff.java.kurinna.microservice.service.OttomanJSCouchbaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CouchbaseOttomanJSInitializer {

    private static final Logger log = LoggerFactory.getLogger(CouchbaseOttomanJSInitializer.class);

    private static final int POLL_INTERVAL_MS = 5_000;
    private static final int MAX_ATTEMPTS     = 120; // 10 minutes total

    private final OttomanJSCouchbaseService ottomanService;

    public CouchbaseOttomanJSInitializer(OttomanJSCouchbaseService ottomanService) {
        this.ottomanService = ottomanService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void waitForOttomanJS() {
        Thread thread = new Thread(this::pollUntilHealthy, "couchbase-ottomanjs-init");
        thread.setDaemon(true);
        thread.start();
    }

    private void pollUntilHealthy() {
        log.info("Waiting for Ottoman.js Couchbase microservice to become healthy...");

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                ottomanService.health();
                log.info("Ottoman.js Couchbase microservice is healthy. Data was pre-loaded by the loader container.");
                return;
            } catch (Exception e) {
                log.info("Ottoman.js Couchbase not ready yet (attempt {}/{}): {}", attempt, MAX_ATTEMPTS, e.getMessage());
                try {
                    Thread.sleep(POLL_INTERVAL_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("Ottoman.js Couchbase initializer interrupted.");
                    return;
                }
            }
        }

        log.error("Ottoman.js Couchbase microservice did not become available after {} attempts.", MAX_ATTEMPTS);
    }
}
