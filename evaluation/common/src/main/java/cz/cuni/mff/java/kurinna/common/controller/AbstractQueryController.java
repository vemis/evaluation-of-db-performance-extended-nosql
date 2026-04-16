package cz.cuni.mff.java.kurinna.common.controller;

import cz.cuni.mff.java.kurinna.common.service.IQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

import static cz.cuni.mff.java.kurinna.common.utils.QueryExecutor.executeWithMeasurement;

public abstract class AbstractQueryController<T> implements IQueryController {

    protected final IQueryService<T> service;

    protected AbstractQueryController(IQueryService<T> service) {
        this.service = service;
    }

    @Override
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @Override
    @GetMapping("/a1")
    public ResponseEntity<Map<String, Object>> a1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::a1, repetitions));
    }

    @Override
    @GetMapping("/a2")
    public ResponseEntity<Map<String, Object>> a2(
            @RequestParam(defaultValue = "1996-01-01") String startDate,
            @RequestParam(defaultValue = "1996-12-31") String endDate,
            @RequestParam(defaultValue = "10") int repetitions) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(executeWithMeasurement(() -> service.a2(start, end), repetitions));
    }

    @Override
    @GetMapping("/a3")
    public ResponseEntity<Map<String, Object>> a3(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::a3, repetitions));
    }

    @Override
    @GetMapping("/a4")
    public ResponseEntity<Map<String, Object>> a4(
            @RequestParam(defaultValue = "1000") int minOrderKey,
            @RequestParam(defaultValue = "50000") int maxOrderKey,
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(
                () -> service.a4(minOrderKey, maxOrderKey), repetitions));
    }

    @Override
    @GetMapping("/b1")
    public ResponseEntity<Map<String, Object>> b1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::b1, repetitions));
    }

    @Override
    @GetMapping("/b2")
    public ResponseEntity<Map<String, Object>> b2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::b2, repetitions));
    }

    @Override
    @GetMapping("/c1")
    public ResponseEntity<Map<String, Object>> c1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::c1, repetitions));
    }

    @Override
    @GetMapping("/c2")
    public ResponseEntity<Map<String, Object>> c2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::c2, repetitions));
    }

    @Override
    @GetMapping("/c3")
    public ResponseEntity<Map<String, Object>> c3(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::c3, repetitions));
    }

    @Override
    @GetMapping("/c4")
    public ResponseEntity<Map<String, Object>> c4(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::c4, repetitions));
    }

    @Override
    @GetMapping("/c5")
    public ResponseEntity<Map<String, Object>> c5(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::c5, repetitions));
    }

    @Override
    @GetMapping("/d1")
    public ResponseEntity<Map<String, Object>> d1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::d1, repetitions));
    }

    @Override
    @GetMapping("/d2")
    public ResponseEntity<Map<String, Object>> d2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::d2, repetitions));
    }

    @Override
    @GetMapping("/d3")
    public ResponseEntity<Map<String, Object>> d3(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::d3, repetitions));
    }

    @Override
    @GetMapping("/e1")
    public ResponseEntity<Map<String, Object>> e1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::e1, repetitions));
    }

    @Override
    @GetMapping("/e2")
    public ResponseEntity<Map<String, Object>> e2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::e2, repetitions));
    }

    @Override
    @GetMapping("/e3")
    public ResponseEntity<Map<String, Object>> e3(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(service::e3, repetitions));
    }

    @Override
    @GetMapping("/q1")
    public ResponseEntity<Map<String, Object>> q1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(() -> service.q1(90), repetitions));
    }

    @Override
    @GetMapping("/q2")
    public ResponseEntity<Map<String, Object>> q2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(
                () -> service.q2(15, "%BRASS", "EUROPE"), repetitions));
    }

    @Override
    @GetMapping("/q3")
    public ResponseEntity<Map<String, Object>> q3(
            @RequestParam(defaultValue = "10") int repetitions) {
        LocalDate orderDate = LocalDate.of(1995, 3, 15);
        LocalDate shipDate  = LocalDate.of(1995, 3, 15);
        return ResponseEntity.ok(executeWithMeasurement(
                () -> service.q3("BUILDING", orderDate, shipDate), repetitions));
    }

    @Override
    @GetMapping("/q4")
    public ResponseEntity<Map<String, Object>> q4(
            @RequestParam(defaultValue = "10") int repetitions) {
        LocalDate orderDate = LocalDate.of(1993, 7, 1);
        return ResponseEntity.ok(executeWithMeasurement(
                () -> service.q4(orderDate), repetitions));
    }

    @Override
    @GetMapping("/q5")
    public ResponseEntity<Map<String, Object>> q5(
            @RequestParam(defaultValue = "10") int repetitions) {
        LocalDate orderDate = LocalDate.of(1994, 1, 1);
        return ResponseEntity.ok(executeWithMeasurement(
                () -> service.q5("ASIA", orderDate), repetitions));
    }
}
