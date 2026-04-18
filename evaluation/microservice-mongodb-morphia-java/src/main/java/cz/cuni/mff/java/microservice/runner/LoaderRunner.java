package cz.cuni.mff.java.microservice.runner;

import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderMorphiaE;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderMorphiaR;
import dev.morphia.Datastore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "loader.mode", havingValue = "true")
public class LoaderRunner implements ApplicationRunner {

    private static final List<String> R_COLLECTIONS = List.of(
            "regionR", "nationR", "customerR", "ordersR",
            "lineitemR", "partsuppR", "partR", "supplierR");

    private static final List<String> E_COLLECTIONS = List.of(
            "ordersEWithLineitems", "ordersEWithLineitemsArrayAsTags",
            "ordersEWithLineitemsArrayAsTagsIndexed", "ordersEWithCustomerWithNationWithRegion",
            "ordersEOnlyOComment", "ordersEOnlyOCommentIndexed");

    private final TPCHDatasetLoaderMorphiaR loaderR;
    private final TPCHDatasetLoaderMorphiaE loaderE;
    private final Datastore datastore;

    @Value("${tpch.data.path:/data/tpch-data-small}")
    private String dataPath;

    public LoaderRunner(TPCHDatasetLoaderMorphiaR loaderR, TPCHDatasetLoaderMorphiaE loaderE, Datastore datastore) {
        this.loaderR = loaderR;
        this.loaderE = loaderE;
        this.datastore = datastore;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("=== LOADER: dropping and reloading relational collections ===");
        R_COLLECTIONS.forEach(col -> datastore.getDatabase().getCollection(col).drop());
        loaderR.loadAll(dataPath);

        System.out.println("=== LOADER: dropping and reloading embedded collections ===");
        E_COLLECTIONS.forEach(col -> datastore.getDatabase().getCollection(col).drop());
        loaderE.loadAll(dataPath);

        System.out.println("=== LOADER: all data loaded, exiting ===");
        System.exit(0);
    }
}
