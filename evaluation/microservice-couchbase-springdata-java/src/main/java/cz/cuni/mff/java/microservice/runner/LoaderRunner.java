package cz.cuni.mff.java.microservice.runner;

import com.couchbase.client.core.error.ServiceNotAvailableException;
import com.couchbase.client.java.Cluster;
import cz.cuni.mff.java.microservice.config.CouchbaseScopeInitializer;
import cz.cuni.mff.java.microservice.controller.EmbeddedLoaderController;
import cz.cuni.mff.java.microservice.controller.LoaderController;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "loader.mode", havingValue = "true")
public class LoaderRunner implements ApplicationRunner {

    private final LoaderController loaderController;
    private final EmbeddedLoaderController embeddedLoaderController;
    private final Cluster cluster;
    private final CouchbaseScopeInitializer scopeInitializer;

    public LoaderRunner(LoaderController loaderController, EmbeddedLoaderController embeddedLoaderController,
                        Cluster cluster, CouchbaseScopeInitializer scopeInitializer) {
        this.loaderController = loaderController;
        this.embeddedLoaderController = embeddedLoaderController;
        this.cluster = cluster;
        this.scopeInitializer = scopeInitializer;
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        waitForQueryService();
        System.out.println("=== LOADER: creating scopes, collections, and indexes ===");
        scopeInitializer.createScopesCollectionsAndIndexes();

        System.out.println("=== LOADER: loading relational collections ===");
        System.out.println(loaderController.loadData().getBody());

        System.out.println("=== LOADER: loading embedded collections ===");
        System.out.println(embeddedLoaderController.loadEmbeddedData().getBody());

        System.out.println("=== LOADER: finished, exiting ===");
        System.exit(0);
    }

    private void waitForQueryService() throws InterruptedException {
        int maxAttempts = 30;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                cluster.query("SELECT 1");
                System.out.println("=== LOADER: Couchbase query service is ready ===");
                return;
            } catch (ServiceNotAvailableException e) {
                System.out.printf("=== LOADER: query service not ready yet (attempt %d/%d), retrying in 5s...%n", attempt, maxAttempts);
                Thread.sleep(5_000);
            }
        }
        throw new IllegalStateException("Couchbase query service did not become available after " + maxAttempts + " attempts");
    }
}
