package cz.cuni.mff.java.kurinna.common.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

public abstract class AbstractLoaderController implements ILoaderController {

    @Value("${tpch.data.path:/data/tpch-data-small}")
    private String dataPath;

    protected final ResponseEntity<String> executeLoad(String loggingQueryType) {
        System.out.println("Executing load for query type: " + loggingQueryType);
        if (isAlreadyLoaded()) {
            return ResponseEntity.ok(loggingQueryType + " data already loaded, skipping.");
        }
        System.out.println("Data NOT already loaded, executing load for query type: " + loggingQueryType);

        dropCollections();
        loadAllData(dataPath);
        insertSentinel();
        return ResponseEntity.ok(loggingQueryType + " data loaded from: " + dataPath);
    }
}
