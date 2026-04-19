package cz.cuni.mff.java.kurinna.microservice.initializer;

import cz.cuni.mff.java.kurinna.microservice.service.SpringDataMongoDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MongoDbSpringDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(MongoDbSpringDataInitializer.class);

    private static final int POLL_INTERVAL_MS = 5_000;
    private static final int MAX_ATTEMPTS     = 120; // 10 minutes total

    private final SpringDataMongoDBService springDataMongoDBService;

    public MongoDbSpringDataInitializer(SpringDataMongoDBService springDataMongoDBService) {
        this.springDataMongoDBService = springDataMongoDBService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void waitForSpringDataMongoDB() {
        Thread thread = new Thread(this::pollUntilHealthy, "springdata-mongodb-init");
        thread.setDaemon(true);
        thread.start();
    }

    private void pollUntilHealthy() {
        log.info("Waiting for Spring Data MongoDB microservice to become healthy...");

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                springDataMongoDBService.health();
                log.info("Spring Data MongoDB microservice is healthy. Data was pre-loaded by the loader container.");
                return;
            } catch (Exception e) {
                log.info("Spring Data MongoDB not ready yet (attempt {}/{}): {}", attempt, MAX_ATTEMPTS, e.getMessage());
                try {
                    Thread.sleep(POLL_INTERVAL_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("Spring Data MongoDB initializer interrupted.");
                    return;
                }
            }
        }

        log.error("Spring Data MongoDB microservice did not become available after {} attempts.", MAX_ATTEMPTS);
    }
}
