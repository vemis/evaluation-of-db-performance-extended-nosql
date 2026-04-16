package cz.cuni.mff.java.kurinna.microservice.controller;

import cz.cuni.mff.java.kurinna.microservice.service.CayenneService;
import cz.cuni.mff.java.kurinna.microservice.service.EbeanService;
import cz.cuni.mff.java.kurinna.microservice.service.JdbcService;
import cz.cuni.mff.java.kurinna.microservice.service.JooqService;
import cz.cuni.mff.java.kurinna.microservice.service.MorphiaService;
import cz.cuni.mff.java.kurinna.microservice.service.MyBatisService;
import cz.cuni.mff.java.kurinna.microservice.service.SpringDataJpaService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static cz.cuni.mff.java.kurinna.microservice.utils.Utils.ALL_SERVICES;

@RestController
@RequestMapping("/orchestrator")
public class OrchestratorController {
    private final MyBatisService myBatisService;
    private final SpringDataJpaService springDataJpaService;
    private final CayenneService cayenneService;
    private final EbeanService ebeanService;
    private final JdbcService jdbcService;
    private final JooqService jooqService;
    private final MorphiaService morphiaService;

    public OrchestratorController(MyBatisService myBatisService,
            SpringDataJpaService springDataJpaService,
            CayenneService cayenneService, EbeanService ebeanService,
            JdbcService jdbcService, JooqService jooqService,
            MorphiaService morphiaService) {
        this.myBatisService = myBatisService;
        this.springDataJpaService = springDataJpaService;
        this.cayenneService = cayenneService;
        this.ebeanService = ebeanService;
        this.jdbcService = jdbcService;
        this.jooqService = jooqService;
        this.morphiaService = morphiaService;
    }

    private Set<String> parseServices(Optional<String> servicesOpt) {
        List<String> allServicesList = Arrays.asList(ALL_SERVICES);
        return servicesOpt
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim)
                        .filter(allServicesList::contains)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .orElse(new LinkedHashSet<>(allServicesList));
    }

    private int parseRepetitions(Optional<String> repetitions) {
        try {
            return Integer.parseInt(repetitions.orElse("10"));
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    // ── Q-series ──────────────────────────────────────────────────────────────

    @GetMapping(value = "/q1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getPricingSummary(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "Q1) Pricing Summary Report Query",
                "TPC-H Q1 query that reports pricing summary for all items shipped before a given date.");
        runAcrossServices(results, selected,
                () -> myBatisService.getPricingSummary(rep),
                () -> springDataJpaService.getPricingSummary(rep),
                () -> cayenneService.getPricingSummary(rep),
                () -> ebeanService.getPricingSummary(rep),
                () -> jdbcService.getPricingSummary(rep),
                () -> jooqService.getPricingSummary(rep),
                () -> morphiaService.getPricingSummary(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/q2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getMinimumCostSupplier(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "Q2) Minimum Cost Supplier Query",
                "TPC-H Q2 query that finds suppliers who can supply parts of a given type and size at minimum cost.");
        runAcrossServices(results, selected,
                () -> myBatisService.getMinimumCostSupplier(rep),
                () -> springDataJpaService.getMinimumCostSupplier(rep),
                () -> cayenneService.getMinimumCostSupplier(rep),
                () -> ebeanService.getMinimumCostSupplier(rep),
                () -> jdbcService.getMinimumCostSupplier(rep),
                () -> jooqService.getMinimumCostSupplier(rep),
                () -> morphiaService.getMinimumCostSupplier(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/q3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getShippingPriority(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "Q3) Shipping Priority Query",
                "TPC-H Q3 query that retrieves the shipping priority and potential revenue of orders.");
        runAcrossServices(results, selected,
                () -> myBatisService.getShippingPriority(rep),
                () -> springDataJpaService.getShippingPriority(rep),
                () -> cayenneService.getShippingPriority(rep),
                () -> ebeanService.getShippingPriority(rep),
                () -> jdbcService.getShippingPriority(rep),
                () -> jooqService.getShippingPriority(rep),
                () -> morphiaService.getShippingPriority(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/q4", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getOrderPriorityChecking(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "Q4) Order Priority Checking Query",
                "TPC-H Q4 query that counts orders with at least one lineitem that was received later than committed.");
        runAcrossServices(results, selected,
                () -> myBatisService.getOrderPriorityChecking(rep),
                () -> springDataJpaService.getOrderPriorityChecking(rep),
                () -> cayenneService.getOrderPriorityChecking(rep),
                () -> ebeanService.getOrderPriorityChecking(rep),
                () -> jdbcService.getOrderPriorityChecking(rep),
                () -> jooqService.getOrderPriorityChecking(rep),
                () -> morphiaService.getOrderPriorityChecking(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/q5", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getLocalSupplierVolume(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "Q5) Local Supplier Volume Query",
                "TPC-H Q5 query that lists the revenue volume for each nation in a region where suppliers and customers are from the same nation.");
        runAcrossServices(results, selected,
                () -> myBatisService.getLocalSupplierVolume(rep),
                () -> springDataJpaService.getLocalSupplierVolume(rep),
                () -> cayenneService.getLocalSupplierVolume(rep),
                () -> ebeanService.getLocalSupplierVolume(rep),
                () -> jdbcService.getLocalSupplierVolume(rep),
                () -> jooqService.getLocalSupplierVolume(rep),
                () -> morphiaService.getLocalSupplierVolume(rep));
        return ResponseEntity.ok(results);
    }

    // ── A-series ──────────────────────────────────────────────────────────────

    @GetMapping(value = "/a1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryA1(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap("A1) Non-Indexed Columns", "SELECT * FROM lineitem");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryA1(rep),
                () -> springDataJpaService.executeQueryA1(rep),
                () -> cayenneService.executeQueryA1(rep),
                () -> ebeanService.executeQueryA1(rep),
                () -> jdbcService.executeQueryA1(rep),
                () -> jooqService.executeQueryA1(rep),
                () -> morphiaService.executeQueryA1(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/a2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryA2(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "A2) Non-Indexed Columns — Range Query",
                "SELECT * FROM orders WHERE o_orderdate BETWEEN '1996-01-01' AND '1996-12-31';");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryA2(rep),
                () -> springDataJpaService.executeQueryA2(rep),
                () -> cayenneService.executeQueryA2(rep),
                () -> ebeanService.executeQueryA2(rep),
                () -> jdbcService.executeQueryA2(rep),
                () -> jooqService.executeQueryA2(rep),
                () -> morphiaService.executeQueryA2(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/a3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryA3(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap("A3) Indexed Columns", "SELECT * FROM customer");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryA3(rep),
                () -> springDataJpaService.executeQueryA3(rep),
                () -> cayenneService.executeQueryA3(rep),
                () -> ebeanService.executeQueryA3(rep),
                () -> jdbcService.executeQueryA3(rep),
                () -> jooqService.executeQueryA3(rep),
                () -> morphiaService.executeQueryA3(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/a4", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryA4(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "A4) Indexed Columns — Range Query",
                "SELECT * FROM orders WHERE o_orderkey BETWEEN 1000 AND 2000;");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryA4(rep),
                () -> springDataJpaService.executeQueryA4(rep),
                () -> cayenneService.executeQueryA4(rep),
                () -> ebeanService.executeQueryA4(rep),
                () -> jdbcService.executeQueryA4(rep),
                () -> jooqService.executeQueryA4(rep),
                () -> morphiaService.executeQueryA4(rep));
        return ResponseEntity.ok(results);
    }

    // ── B-series ──────────────────────────────────────────────────────────────

    @GetMapping(value = "/b1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryB1(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "B1) COUNT",
                "SELECT COUNT(*) AS order_count FROM orders WHERE o_orderdate BETWEEN '1996-01-01' AND '1996-12-31';");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryB1(rep),
                () -> springDataJpaService.executeQueryB1(rep),
                () -> cayenneService.executeQueryB1(rep),
                () -> ebeanService.executeQueryB1(rep),
                () -> jdbcService.executeQueryB1(rep),
                () -> jooqService.executeQueryB1(rep),
                () -> morphiaService.executeQueryB1(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/b2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryB2(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "B2) MAX",
                "SELECT MAX(l_extendedprice) AS max_price FROM lineitem;");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryB2(rep),
                () -> springDataJpaService.executeQueryB2(rep),
                () -> cayenneService.executeQueryB2(rep),
                () -> ebeanService.executeQueryB2(rep),
                () -> jdbcService.executeQueryB2(rep),
                () -> jooqService.executeQueryB2(rep),
                () -> morphiaService.executeQueryB2(rep));
        return ResponseEntity.ok(results);
    }

    // ── C-series ──────────────────────────────────────────────────────────────

    @GetMapping(value = "/c1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryC1(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "C1) Non-Indexed Columns",
                "SELECT c.c_name, o.o_orderdate, o.o_totalprice FROM customer c, orders o WHERE c.c_mktsegment = 'BUILDING' AND c.c_custkey = o.o_custkey;");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryC1(rep),
                () -> springDataJpaService.executeQueryC1(rep),
                () -> cayenneService.executeQueryC1(rep),
                () -> ebeanService.executeQueryC1(rep),
                () -> jdbcService.executeQueryC1(rep),
                () -> jooqService.executeQueryC1(rep),
                () -> morphiaService.executeQueryC1(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/c2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryC2(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "C2) Indexed Columns",
                "SELECT c.c_name, o.o_orderdate, o.o_totalprice FROM customer c JOIN orders o ON c.c_custkey = o.o_custkey WHERE o.o_orderkey BETWEEN 1000 AND 2000;");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryC2(rep),
                () -> springDataJpaService.executeQueryC2(rep),
                () -> cayenneService.executeQueryC2(rep),
                () -> ebeanService.executeQueryC2(rep),
                () -> jdbcService.executeQueryC2(rep),
                () -> jooqService.executeQueryC2(rep),
                () -> morphiaService.executeQueryC2(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/c3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryC3(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "C3) Complex Join 1",
                "SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice FROM customer c JOIN nation n ON c.c_nationkey = n.n_nationkey JOIN orders o ON c.c_custkey = o.o_custkey WHERE n.n_name = 'GERMANY' AND o.o_orderdate BETWEEN '1996-01-01' AND '1996-12-31';");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryC3(rep),
                () -> springDataJpaService.executeQueryC3(rep),
                () -> cayenneService.executeQueryC3(rep),
                () -> ebeanService.executeQueryC3(rep),
                () -> jdbcService.executeQueryC3(rep),
                () -> jooqService.executeQueryC3(rep),
                () -> morphiaService.executeQueryC3(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/c4", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryC4(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "C4) Complex Join 2",
                "SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice FROM customer c JOIN nation n ON c.c_nationkey = n.n_nationkey JOIN region r ON n.n_regionkey = r.r_regionkey JOIN orders o ON c.c_custkey = o.o_custkey WHERE r.r_name = 'EUROPE' AND o.o_orderdate BETWEEN '1996-01-01' AND '1996-12-31';");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryC4(rep),
                () -> springDataJpaService.executeQueryC4(rep),
                () -> cayenneService.executeQueryC4(rep),
                () -> ebeanService.executeQueryC4(rep),
                () -> jdbcService.executeQueryC4(rep),
                () -> jooqService.executeQueryC4(rep),
                () -> morphiaService.executeQueryC4(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/c5", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryC5(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "C5) Left Outer Join",
                "SELECT c.c_custkey, c.c_name, o.o_orderkey, o.o_orderdate FROM customer c LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey WHERE c.c_nationkey = 3;");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryC5(rep),
                () -> springDataJpaService.executeQueryC5(rep),
                () -> cayenneService.executeQueryC5(rep),
                () -> ebeanService.executeQueryC5(rep),
                () -> jdbcService.executeQueryC5(rep),
                () -> jooqService.executeQueryC5(rep),
                () -> morphiaService.executeQueryC5(rep));
        return ResponseEntity.ok(results);
    }

    // ── D-series ──────────────────────────────────────────────────────────────

    @GetMapping(value = "/d1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryD1(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "D1) UNION",
                "(SELECT c_nationkey FROM customer WHERE c_acctbal > 9000) UNION (SELECT s_nationkey FROM supplier;");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryD1(rep),
                () -> springDataJpaService.executeQueryD1(rep),
                () -> cayenneService.executeQueryD1(rep),
                () -> ebeanService.executeQueryD1(rep),
                () -> jdbcService.executeQueryD1(rep),
                () -> jooqService.executeQueryD1(rep),
                () -> morphiaService.executeQueryD1(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/d2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryD2(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "D2) INTERSECT",
                "SELECT DISTINCT c_nationkey FROM customer WHERE c_acctbal > 9000 AND c_nationkey IN (SELECT s_nationkey FROM supplier;");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryD2(rep),
                () -> springDataJpaService.executeQueryD2(rep),
                () -> cayenneService.executeQueryD2(rep),
                () -> ebeanService.executeQueryD2(rep),
                () -> jdbcService.executeQueryD2(rep),
                () -> jooqService.executeQueryD2(rep),
                () -> morphiaService.executeQueryD2(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/d3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryD3(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "D3) DIFFERENCE",
                "SELECT DISTINCT c_nationkey FROM customer WHERE c_acctbal > 9000 AND c_nationkey NOT IN (SELECT s_nationkey FROM supplier;");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryD3(rep),
                () -> springDataJpaService.executeQueryD3(rep),
                () -> cayenneService.executeQueryD3(rep),
                () -> ebeanService.executeQueryD3(rep),
                () -> jdbcService.executeQueryD3(rep),
                () -> jooqService.executeQueryD3(rep),
                () -> morphiaService.executeQueryD3(rep));
        return ResponseEntity.ok(results);
    }

    // ── E-series ──────────────────────────────────────────────────────────────

    @GetMapping(value = "/e1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryE1(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "E1) Non-Indexed Columns Sorting",
                "SELECT c_name, c_address, c_acctbal FROM customer ORDER BY c_acctbal DESC;");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryE1(rep),
                () -> springDataJpaService.executeQueryE1(rep),
                () -> cayenneService.executeQueryE1(rep),
                () -> ebeanService.executeQueryE1(rep),
                () -> jdbcService.executeQueryE1(rep),
                () -> jooqService.executeQueryE1(rep),
                () -> morphiaService.executeQueryE1(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/e2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryE2(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "E2) Indexed Columns Sorting",
                "SELECT o_orderkey, o_custkey, o_orderdate, o_totalprice FROM orders ORDER BY o_orderkey;");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryE2(rep),
                () -> springDataJpaService.executeQueryE2(rep),
                () -> cayenneService.executeQueryE2(rep),
                () -> ebeanService.executeQueryE2(rep),
                () -> jdbcService.executeQueryE2(rep),
                () -> jooqService.executeQueryE2(rep),
                () -> morphiaService.executeQueryE2(rep));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/e3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> executeQueryE3(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = createResultsMap(
                "E3) Distinct",
                "SELECT DISTINCT c_nationkey, c_mktsegment FROM customer;");
        runAcrossServices(results, selected,
                () -> myBatisService.executeQueryE3(rep),
                () -> springDataJpaService.executeQueryE3(rep),
                () -> cayenneService.executeQueryE3(rep),
                () -> ebeanService.executeQueryE3(rep),
                () -> jdbcService.executeQueryE3(rep),
                () -> jooqService.executeQueryE3(rep),
                () -> morphiaService.executeQueryE3(rep));
        return ResponseEntity.ok(results);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Map<String, Object> createResultsMap(String queryName, String description) {
        Map<String, Object> results = new LinkedHashMap<>();
        results.put("query", queryName);
        results.put("description", description);
        return results;
    }

    /**
     * Calls each selected service once. JMH inside the microservice runs all
     * iterations and returns aggregated stats, so there is no repetition loop here.
     */
    private void runAcrossServices(
            Map<String, Object> results,
            Set<String> services,
            ServiceCall myBatisCall,
            ServiceCall springDataJpaCall,
            ServiceCall cayenneCall,
            ServiceCall ebeanCall,
            ServiceCall jdbcCall,
            ServiceCall jooqCall,
            ServiceCall morphiaCall) {

        if (services.contains("myBatis"))       runService("myBatis",       myBatisCall,       results);
        if (services.contains("springDataJpa")) runService("springDataJpa", springDataJpaCall, results);
        if (services.contains("cayenne"))       runService("cayenne",       cayenneCall,       results);
        if (services.contains("ebean"))         runService("ebean",         ebeanCall,         results);
        if (services.contains("jdbc"))          runService("jdbc",          jdbcCall,          results);
        if (services.contains("jooq"))          runService("jooq",          jooqCall,          results);
        if (services.contains("morphia"))       runService("morphia",       morphiaCall,       results);
    }

    /**
     * Invokes one service call and maps the JMH response keys to the MetricType
     * shape expected by the frontend.
     *
     * <p>JMH response keys: elapsed, minTime, maxTime, delta, minMemory, maxMemory,
     * repetitions, iterationResults, result, status.</p>
     *
     * <p>Frontend MetricType keys: averageExecutionTime, minExecutionTime,
     * maxExecutionTime, averageMemoryUsage, minMemoryUsage, maxMemoryUsage,
     * repetition, iterationResults, status.</p>
     */
    private void runService(String serviceName, ServiceCall call, Map<String, Object> results) {
        try {
            Map<String, Object> response = call.execute();

            Map<String, Object> serviceResults = new LinkedHashMap<>();
            serviceResults.put("status",               response.getOrDefault("status", "success"));
            serviceResults.put("repetition",           response.get("repetitions"));
            serviceResults.put("averageExecutionTime", response.get("elapsed"));
            serviceResults.put("minExecutionTime",     response.get("minTime"));
            serviceResults.put("maxExecutionTime",     response.get("maxTime"));
            serviceResults.put("averageMemoryUsage",   response.get("delta"));
            serviceResults.put("minMemoryUsage",       response.get("minMemory"));
            serviceResults.put("maxMemoryUsage",       response.get("maxMemory"));
            serviceResults.put("iterationResults",     response.get("iterationResults"));
            results.put(serviceName, serviceResults);

        } catch (Exception e) {
            Map<String, Object> serviceResults = new LinkedHashMap<>();
            serviceResults.put("status", "error");
            serviceResults.put("error",  e.getMessage());
            results.put(serviceName, serviceResults);
        }
    }

    @FunctionalInterface
    private interface ServiceCall {
        Map<String, Object> execute() throws Exception;
    }
}
