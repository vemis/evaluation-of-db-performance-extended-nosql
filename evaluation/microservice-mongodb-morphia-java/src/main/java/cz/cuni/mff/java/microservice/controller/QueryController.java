package cz.cuni.mff.java.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.IQueryController;
import cz.cuni.mff.java.microservice.loader.TPCHDatasetLoaderMorphiaR;
import cz.cuni.mff.java.microservice.service.QueryService;
import dev.morphia.Datastore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static cz.cuni.mff.java.kurinna.common.utils.QueryExecutor.executeWithMeasurement;

@RestController
public class QueryController implements IQueryController {

    private static final List<String> COLLECTIONS = List.of(
            "regionR", "nationR", "customerR", "ordersR",
            "lineitemR", "partsuppR", "partR", "supplierR"
    );

    private final QueryService queryService;
    private final TPCHDatasetLoaderMorphiaR loader;
    private final Datastore datastore;

    @Value("${tpch.data.path:/data/tpch-data-small}")
    private String dataPath;

    public QueryController(QueryService queryService, TPCHDatasetLoaderMorphiaR loader,
                           Datastore datastore) {
        this.queryService = queryService;
        this.loader = loader;
        this.datastore = datastore;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    /**
     * Drops all TPC-H collections and reloads fresh data from the configured
     * {@code tpch.data.path}. Called by the orchestrator on every startup.
     */
    @PostMapping("/load")
    public ResponseEntity<String> loadData() {
        COLLECTIONS.forEach(col ->
                datastore.getDatabase().getCollection(col).drop());
        loader.loadAll(dataPath);
        return ResponseEntity.ok("Data loaded from: " + dataPath);
    }

    @GetMapping("/a1")
    public ResponseEntity<Map<String, Object>> a1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::a1, repetitions));
    }

    @GetMapping("/a2")
    public ResponseEntity<Map<String, Object>> a2(
            @RequestParam(defaultValue = "1996-01-01") String startDate,
            @RequestParam(defaultValue = "1996-12-31") String endDate,
            @RequestParam(defaultValue = "10") int repetitions) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(executeWithMeasurement(() -> queryService.a2(start, end), repetitions));
    }

    @GetMapping("/a3")
    public ResponseEntity<Map<String, Object>> a3(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::a3, repetitions));
    }

    @GetMapping("/a4")
    public ResponseEntity<Map<String, Object>> a4(
            @RequestParam(defaultValue = "1000") int minOrderKey,
            @RequestParam(defaultValue = "50000") int maxOrderKey,
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(
                () -> queryService.a4(minOrderKey, maxOrderKey), repetitions));
    }

    @GetMapping("/b1")
    public ResponseEntity<Map<String, Object>> b1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::b1, repetitions));
    }

    @GetMapping("/b2")
    public ResponseEntity<Map<String, Object>> b2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::b2, repetitions));
    }

    @GetMapping("/c1")
    public ResponseEntity<Map<String, Object>> c1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::c1, repetitions));
    }

    @GetMapping("/c2")
    public ResponseEntity<Map<String, Object>> c2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::c2, repetitions));
    }

    @GetMapping("/c3")
    public ResponseEntity<Map<String, Object>> c3(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::c3, repetitions));
    }

    @GetMapping("/c4")
    public ResponseEntity<Map<String, Object>> c4(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::c4, repetitions));
    }

    @GetMapping("/c5")
    public ResponseEntity<Map<String, Object>> c5(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::c5, repetitions));
    }

    @GetMapping("/d1")
    public ResponseEntity<Map<String, Object>> d1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::d1, repetitions));
    }

    @GetMapping("/d2")
    public ResponseEntity<Map<String, Object>> d2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::d2, repetitions));
    }

    @GetMapping("/d3")
    public ResponseEntity<Map<String, Object>> d3(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::d3, repetitions));
    }

    @GetMapping("/e1")
    public ResponseEntity<Map<String, Object>> e1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::e1, repetitions));
    }

    @GetMapping("/e2")
    public ResponseEntity<Map<String, Object>> e2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::e2, repetitions));
    }

    @GetMapping("/e3")
    public ResponseEntity<Map<String, Object>> e3(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::e3, repetitions));
    }

    @GetMapping("/q1")
    public ResponseEntity<Map<String, Object>> q1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(() -> queryService.q1(90), repetitions));
    }

    @GetMapping("/q2")
    public ResponseEntity<Map<String, Object>> q2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(
                () -> queryService.q2(15, "%BRASS", "EUROPE"), repetitions));
    }

    @GetMapping("/q3")
    public ResponseEntity<Map<String, Object>> q3(
            @RequestParam(defaultValue = "10") int repetitions) {
        LocalDate orderDate = LocalDate.of(1995, 3, 15);
        LocalDate shipDate  = LocalDate.of(1995, 3, 15);
        return ResponseEntity.ok(executeWithMeasurement(
                () -> queryService.q3("BUILDING", orderDate, shipDate), repetitions));
    }

    @GetMapping("/q4")
    public ResponseEntity<Map<String, Object>> q4(
            @RequestParam(defaultValue = "10") int repetitions) {
        LocalDate orderDate = LocalDate.of(1993, 7, 1);
        return ResponseEntity.ok(executeWithMeasurement(
                () -> queryService.q4(orderDate), repetitions));
    }

    @GetMapping("/q5")
    public ResponseEntity<Map<String, Object>> q5(
            @RequestParam(defaultValue = "10") int repetitions) {
        LocalDate orderDate = LocalDate.of(1994, 1, 1);
        return ResponseEntity.ok(executeWithMeasurement(
                () -> queryService.q5("ASIA", orderDate), repetitions));
    }
}
