package cz.cuni.mff.java.kurinna.common.controller;

import cz.cuni.mff.java.kurinna.common.service.IEmbeddedQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static cz.cuni.mff.java.kurinna.common.utils.QueryExecutor.executeWithMeasurement;

public abstract class AbstractEmbeddedQueryController<T> implements IEmbeddedQueryController {

    protected final IEmbeddedQueryService<T> embeddedService;

    protected AbstractEmbeddedQueryController(IEmbeddedQueryService<T> embeddedService) {
        this.embeddedService = embeddedService;
    }

    @Override
    @GetMapping("/r1")
    public ResponseEntity<Map<String, Object>> r1(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(embeddedService::r1, repetitions));
    }

    @Override
    @GetMapping("/r2")
    public ResponseEntity<Map<String, Object>> r2(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(embeddedService::r2, repetitions));
    }

    @Override
    @GetMapping("/r3")
    public ResponseEntity<Map<String, Object>> r3(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(embeddedService::r3, repetitions));
    }

    @Override
    @GetMapping("/r4")
    public ResponseEntity<Map<String, Object>> r4(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(embeddedService::r4, repetitions));
    }

    @Override
    @GetMapping("/r5")
    public ResponseEntity<Map<String, Object>> r5(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(embeddedService::r5, repetitions));
    }

    @Override
    @GetMapping("/r6")
    public ResponseEntity<Map<String, Object>> r6(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(embeddedService::r6, repetitions));
    }

    @Override
    @GetMapping("/r7")
    public ResponseEntity<Map<String, Object>> r7(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(embeddedService::r7, repetitions));
    }

    @Override
    @GetMapping("/r8")
    public ResponseEntity<Map<String, Object>> r8(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(embeddedService::r8, repetitions));
    }

    @Override
    @GetMapping("/r9")
    public ResponseEntity<Map<String, Object>> r9(
            @RequestParam(defaultValue = "10") int repetitions) {
        return ResponseEntity.ok(executeWithMeasurement(embeddedService::r9, repetitions));
    }
}
