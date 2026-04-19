package cz.cuni.mff.java.microservice.runner;

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

    public LoaderRunner(LoaderController loaderController, EmbeddedLoaderController embeddedLoaderController) {
        this.loaderController = loaderController;
        this.embeddedLoaderController = embeddedLoaderController;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("=== LOADER: loading relational collections ===");
        System.out.println(loaderController.loadData().getBody());

        System.out.println("=== LOADER: loading embedded collections ===");
        System.out.println(embeddedLoaderController.loadEmbeddedData().getBody());

        System.out.println("=== LOADER: finished, exiting ===");
        System.exit(0);
    }
}
