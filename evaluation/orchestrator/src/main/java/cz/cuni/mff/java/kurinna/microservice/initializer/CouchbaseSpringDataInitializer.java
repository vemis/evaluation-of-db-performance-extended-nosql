package cz.cuni.mff.java.kurinna.microservice.initializer;

import cz.cuni.mff.java.kurinna.microservice.service.CouchbaseSpringDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Waits for the Couchbase Spring Data microservice to become healthy, then
 * triggers a full data reload (drop all collections + load from TPC-H dataset).
 */
@Component
public class CouchbaseSpringDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(CouchbaseSpringDataInitializer.class);

    private static final int POLL_INTERVAL_MS = 5_000;
    private static final int MAX_ATTEMPTS     = 120; // 10 minutes total

    private final CouchbaseSpringDataService couchbaseService;

    public CouchbaseSpringDataInitializer(CouchbaseSpringDataService couchbaseService) {
        this.couchbaseService = couchbaseService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void waitForCouchbase() {
        Thread thread = new Thread(this::pollUntilHealthy, "couchbase-springdata-init");
        thread.setDaemon(true);
        thread.start();
    }

    private void pollUntilHealthy() {
        log.info("Waiting for Couchbase Spring Data microservice to become healthy...");

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                couchbaseService.health();
                log.info("Couchbase Spring Data microservice is healthy. Data was pre-loaded by the loader container.");
                return;
            } catch (Exception e) {
                log.info("Couchbase Spring Data not ready yet (attempt {}/{}): {}", attempt, MAX_ATTEMPTS, e.getMessage());
                try {
                    Thread.sleep(POLL_INTERVAL_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("Couchbase Spring Data initializer interrupted.");
                    return;
                }
            }
        }

        log.error("Couchbase Spring Data microservice did not become available after {} attempts.", MAX_ATTEMPTS);
    }
}
