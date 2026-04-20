package cz.cuni.mff.java.kurinna.microservice.initializer;

import cz.cuni.mff.java.kurinna.microservice.service.MongooseMongoDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MongoDbMongooseInitializer {

    private static final Logger log = LoggerFactory.getLogger(MongoDbMongooseInitializer.class);

    private static final int POLL_INTERVAL_MS = 5_000;
    private static final int MAX_ATTEMPTS     = 120; // 10 minutes total

    private final MongooseMongoDBService mongooseService;

    public MongoDbMongooseInitializer(MongooseMongoDBService mongooseService) {
        this.mongooseService = mongooseService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void waitForMongoose() {
        Thread thread = new Thread(this::pollUntilHealthy, "mongoose-mongodb-init");
        thread.setDaemon(true);
        thread.start();
    }

    private void pollUntilHealthy() {
        log.info("Waiting for Mongoose MongoDB microservice to become healthy...");

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                mongooseService.health();
                log.info("Mongoose MongoDB microservice is healthy. Data was pre-loaded by the loader container.");
                return;
            } catch (Exception e) {
                log.info("Mongoose MongoDB not ready yet (attempt {}/{}): {}", attempt, MAX_ATTEMPTS, e.getMessage());
                try {
                    Thread.sleep(POLL_INTERVAL_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("Mongoose MongoDB initializer interrupted.");
                    return;
                }
            }
        }

        log.error("Mongoose MongoDB microservice did not become available after {} attempts.", MAX_ATTEMPTS);
    }
}
