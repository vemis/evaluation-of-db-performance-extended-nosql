package cz.cuni.mff.java.kurinna.microservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface OrmClient {

    @GetMapping("/health")
    ResponseEntity<String> health();

    @GetMapping("/a1")
    ResponseEntity<Map<String, Object>> getNonIndexedColumns(@RequestParam int repetitions);

    @GetMapping("/a2")
    ResponseEntity<Map<String, Object>> getNonIndexedColumnsRangeQuery(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam int repetitions);

    @GetMapping("/a3")
    ResponseEntity<Map<String, Object>> getIndexedColumns(@RequestParam int repetitions);

    @GetMapping("/a4")
    ResponseEntity<Map<String, Object>> getIndexedColumnsRangeQuery(
            @RequestParam int minOrderKey,
            @RequestParam int maxOrderKey,
            @RequestParam int repetitions);

    @GetMapping("/b1")
    ResponseEntity<Map<String, Object>> getCount(@RequestParam int repetitions);

    @GetMapping("/b2")
    ResponseEntity<Map<String, Object>> getMax(@RequestParam int repetitions);

    @GetMapping("/c1")
    ResponseEntity<Map<String, Object>> getJoinNonIndexedColumns(@RequestParam int repetitions);

    @GetMapping("/c2")
    ResponseEntity<Map<String, Object>> getJoinIndexedColumns(@RequestParam int repetitions);

    @GetMapping("/c3")
    ResponseEntity<Map<String, Object>> getComplexJoin1(@RequestParam int repetitions);

    @GetMapping("/c4")
    ResponseEntity<Map<String, Object>> getComplexJoin2(@RequestParam int repetitions);

    @GetMapping("/c5")
    ResponseEntity<Map<String, Object>> getLeftOuterJoin(@RequestParam int repetitions);

    @GetMapping("/d1")
    ResponseEntity<Map<String, Object>> getUnion(@RequestParam int repetitions);

    @GetMapping("/d2")
    ResponseEntity<Map<String, Object>> getIntersect(@RequestParam int repetitions);

    @GetMapping("/d3")
    ResponseEntity<Map<String, Object>> getDifference(@RequestParam int repetitions);

    @GetMapping("/e1")
    ResponseEntity<Map<String, Object>> getNonIndexedColumnsSorting(@RequestParam int repetitions);

    @GetMapping("/e2")
    ResponseEntity<Map<String, Object>> getIndexedColumnsSorting(@RequestParam int repetitions);

    @GetMapping("/e3")
    ResponseEntity<Map<String, Object>> getDistinct(@RequestParam int repetitions);

    @GetMapping("/q1")
    ResponseEntity<Map<String, Object>> getPricingSummary(@RequestParam int repetitions);

    @GetMapping("/q2")
    ResponseEntity<Map<String, Object>> getMinimumCostSupplier(@RequestParam int repetitions);

    @GetMapping("/q3")
    ResponseEntity<Map<String, Object>> getShippingPriority(@RequestParam int repetitions);

    @GetMapping("/q4")
    ResponseEntity<Map<String, Object>> getOrderPriorityChecking(@RequestParam int repetitions);

    @GetMapping("/q5")
    ResponseEntity<Map<String, Object>> getLocalSupplierVolume(@RequestParam int repetitions);
}
