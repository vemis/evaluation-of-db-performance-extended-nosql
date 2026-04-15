package cz.cuni.mff.java.kurinna.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.IQueryController;
import cz.cuni.mff.java.kurinna.microservice.service.QueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

import static cz.cuni.mff.java.kurinna.common.utils.QueryExecutor.executeWithMeasurement;

@RestController
public class QueryController implements IQueryController {
    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    // A1) Non-Indexed Columns
    @GetMapping("/a1")
    public ResponseEntity<Map<String, Object>> a1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::a1, repetitions));
    }

    // A2) Non-Indexed Columns — Range Query
    @GetMapping("/a2")
    public ResponseEntity<Map<String, Object>> a2(
            @RequestParam(defaultValue = "1996-01-01") String startDate,
            @RequestParam(defaultValue = "1996-12-31") String endDate,
            @RequestParam(defaultValue = "10") int repetitions) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(executeWithMeasurement(() -> queryService.a2(start, end), repetitions));
    }

    // A3) Indexed Columns
    @GetMapping("/a3")
    public ResponseEntity<Map<String, Object>> a3(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::a3, repetitions));
    }

    // A4) Indexed Columns — Range Query
    @GetMapping("/a4")
    public ResponseEntity<Map<String, Object>> a4(
            @RequestParam(defaultValue = "1000") int minOrderKey,
            @RequestParam(defaultValue = "50000") int maxOrderKey,
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(
                () -> queryService.a4(minOrderKey, maxOrderKey), repetitions));
    }

    // B1) COUNT
    @GetMapping("/b1")
    public ResponseEntity<Map<String, Object>> b1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::b1, repetitions));
    }

    // B2) MAX
    @GetMapping("/b2")
    public ResponseEntity<Map<String, Object>> b2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::b2, repetitions));
    }

    // C1) Non-Indexed Columns
    @GetMapping("/c1")
    public ResponseEntity<Map<String, Object>> c1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::c1, repetitions));
    }

    // C2) Indexed Columns
    @GetMapping("/c2")
    public ResponseEntity<Map<String, Object>> c2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::c2, repetitions));
    }

    // C3) Complex Join 1
    @GetMapping("/c3")
    public ResponseEntity<Map<String, Object>> c3(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::c3, repetitions));
    }

    // C4) Complex Join 2
    @GetMapping("/c4")
    public ResponseEntity<Map<String, Object>> c4(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::c4, repetitions));
    }

    // C5) Left Outer Join
    @GetMapping("/c5")
    public ResponseEntity<Map<String, Object>> c5(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::c5, repetitions));
    }

    // D1) UNION
    @GetMapping("/d1")
    public ResponseEntity<Map<String, Object>> d1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::d1, repetitions));
    }

    // D2) INTERSECT
    @GetMapping("/d2")
    public ResponseEntity<Map<String, Object>> d2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::d2, repetitions));
    }

    // D3) DIFFERENCE
    @GetMapping("/d3")
    public ResponseEntity<Map<String, Object>> d3(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::d3, repetitions));
    }

    // E1) Non-Indexed Columns Sorting
    @GetMapping("/e1")
    public ResponseEntity<Map<String, Object>> e1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::e1, repetitions));
    }

    // E2) Indexed Columns Sorting
    @GetMapping("/e2")
    public ResponseEntity<Map<String, Object>> e2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(queryService::e2, repetitions));
    }

    // E3) Distinct
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
