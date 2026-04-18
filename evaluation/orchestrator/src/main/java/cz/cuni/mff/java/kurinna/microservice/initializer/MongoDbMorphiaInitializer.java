package cz.cuni.mff.java.kurinna.microservice.initializer;

import cz.cuni.mff.java.kurinna.microservice.service.MorphiaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Waits for the Morphia microservice to become healthy, then triggers a full
 * data reload (drop all collections + load from the bundled TPC-H dataset).
 *
 * Runs in a background virtual thread so the orchestrator itself remains
 * immediately available while the load is in progress.
 */
@Component
public class MongoDbMorphiaInitializer {

    private static final Logger log = LoggerFactory.getLogger(MongoDbMorphiaInitializer.class);

    private static final int POLL_INTERVAL_MS = 5_000;
    private static final int MAX_ATTEMPTS     = 120; // 10 minutes total

    private final MorphiaService morphiaService;

    public MongoDbMorphiaInitializer(MorphiaService morphiaService) {
        this.morphiaService = morphiaService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void waitForMorphia() {
        Thread thread = new Thread(this::pollUntilHealthy, "morphia-init");
        thread.setDaemon(true);
        thread.start();
    }

    private void pollUntilHealthy() {
        log.info("Waiting for Morphia microservice to become healthy...");

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                morphiaService.health();
                log.info("Morphia microservice is healthy. Data was pre-loaded by the loader container.");
                return;
            } catch (Exception e) {
                log.info("Morphia not ready yet (attempt {}/{}): {}", attempt, MAX_ATTEMPTS, e.getMessage());
                try {
                    Thread.sleep(POLL_INTERVAL_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("Morphia initializer interrupted.");
                    return;
                }
            }
        }

        log.error("Morphia microservice did not become available after {} attempts.", MAX_ATTEMPTS);
    }
}
