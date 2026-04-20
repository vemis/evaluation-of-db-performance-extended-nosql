package cz.cuni.mff.java.kurinna.microservice.controller;

import cz.cuni.mff.java.kurinna.microservice.service.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static cz.cuni.mff.java.kurinna.microservice.utils.Utils.ALL_SERVICES;
import static cz.cuni.mff.java.kurinna.microservice.utils.Utils.EMBEDDED_SERVICES;
import static cz.cuni.mff.java.kurinna.microservice.utils.Utils.EMBEDDED_QUERY_DESCRIPTIONS;

@RestController
@RequestMapping("/orchestrator")
public class OrchestratorController {

    private final Map<String, AbstractOrmService> serviceMap;
    private final Map<String, AbstractEmbeddedOrmService> embeddedServiceMap;

    public OrchestratorController(
            CayenneService cayenne, EbeanService ebean, JdbcService jdbc,
            JooqService jooq, MorphiaService morphia, MyBatisService myBatis,
            SpringDataJpaService springDataJpa,
            MorphiaEmbeddedService morphiaEmbedded,
            CouchbaseSpringDataService couchbaseSpringData,
            CouchbaseSpringDataEmbeddedService couchbaseSpringDataEmbedded,
            SpringDataMongoDBService springDataMongoDB,
            SpringDataMongoDBEmbeddedService springDataMongoDBEmbedded,
            MongooseMongoDBService mongooseMongoDBJavascript,
            MongooseMongoDBEmbeddedService mongooseMongoDBJavascriptEmbedded) {
        this.serviceMap = Map.of(
                "cayenne",                   cayenne,
                "ebean",                     ebean,
                "jdbc",                      jdbc,
                "jooq",                      jooq,
                "morphia",                   morphia,
                "myBatis",                   myBatis,
                "springDataJpa",             springDataJpa,
                "couchbaseSpringData",       couchbaseSpringData,
                "springDataMongoDB",         springDataMongoDB,
                "mongooseMongoDBJavascript", mongooseMongoDBJavascript);
        this.embeddedServiceMap = Map.of(
                "morphia",                   morphiaEmbedded,
                "couchbaseSpringData",       couchbaseSpringDataEmbedded,
                "springDataMongoDB",         springDataMongoDBEmbedded,
                "mongooseMongoDBJavascript", mongooseMongoDBJavascriptEmbedded);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    // ── Q-series ──────────────────────────────────────────────────────────────

    @GetMapping(value = "/q1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> q1(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("q1", repetitions, services,
                "Q1) Pricing Summary Report Query",
                "TPC-H Q1 query that reports pricing summary for all items shipped before a given date.");
    }

    @GetMapping(value = "/q2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> q2(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("q2", repetitions, services,
                "Q2) Minimum Cost Supplier Query",
                "TPC-H Q2 query that finds suppliers who can supply parts of a given type and size at minimum cost.");
    }

    @GetMapping(value = "/q3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> q3(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("q3", repetitions, services,
                "Q3) Shipping Priority Query",
                "TPC-H Q3 query that retrieves the shipping priority and potential revenue of orders.");
    }

    @GetMapping(value = "/q4", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> q4(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("q4", repetitions, services,
                "Q4) Order Priority Checking Query",
                "TPC-H Q4 query that counts orders with at least one lineitem that was received later than committed.");
    }

    @GetMapping(value = "/q5", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> q5(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("q5", repetitions, services,
                "Q5) Local Supplier Volume Query",
                "TPC-H Q5 query that lists the revenue volume for each nation in a region where suppliers and customers are from the same nation.");
    }

    // ── A-series ──────────────────────────────────────────────────────────────

    @GetMapping(value = "/a1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> a1(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("a1", repetitions, services,
                "A1) Non-Indexed Columns",
                "SELECT * FROM lineitem");
    }

    @GetMapping(value = "/a2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> a2(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("a2", repetitions, services,
                "A2) Non-Indexed Columns — Range Query",
                "SELECT * FROM orders WHERE o_orderdate BETWEEN '1996-01-01' AND '1996-12-31';");
    }

    @GetMapping(value = "/a3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> a3(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("a3", repetitions, services,
                "A3) Indexed Columns",
                "SELECT * FROM customer");
    }

    @GetMapping(value = "/a4", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> a4(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("a4", repetitions, services,
                "A4) Indexed Columns — Range Query",
                "SELECT * FROM orders WHERE o_orderkey BETWEEN 1000 AND 2000;");
    }

    // ── B-series ──────────────────────────────────────────────────────────────

    @GetMapping(value = "/b1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> b1(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("b1", repetitions, services,
                "B1) COUNT",
                "SELECT COUNT(*) AS order_count FROM orders WHERE o_orderdate BETWEEN '1996-01-01' AND '1996-12-31';");
    }

    @GetMapping(value = "/b2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> b2(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("b2", repetitions, services,
                "B2) MAX",
                "SELECT MAX(l_extendedprice) AS max_price FROM lineitem;");
    }

    // ── C-series ──────────────────────────────────────────────────────────────

    @GetMapping(value = "/c1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> c1(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("c1", repetitions, services,
                "C1) Non-Indexed Columns",
                "SELECT c.c_name, o.o_orderdate, o.o_totalprice FROM customer c, orders o WHERE c.c_mktsegment = 'BUILDING' AND c.c_custkey = o.o_custkey;");
    }

    @GetMapping(value = "/c2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> c2(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("c2", repetitions, services,
                "C2) Indexed Columns",
                "SELECT c.c_name, o.o_orderdate, o.o_totalprice FROM customer c JOIN orders o ON c.c_custkey = o.o_custkey WHERE o.o_orderkey BETWEEN 1000 AND 2000;");
    }

    @GetMapping(value = "/c3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> c3(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("c3", repetitions, services,
                "C3) Complex Join 1",
                "SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice FROM customer c JOIN nation n ON c.c_nationkey = n.n_nationkey JOIN orders o ON c.c_custkey = o.o_custkey WHERE n.n_name = 'GERMANY' AND o.o_orderdate BETWEEN '1996-01-01' AND '1996-12-31';");
    }

    @GetMapping(value = "/c4", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> c4(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("c4", repetitions, services,
                "C4) Complex Join 2",
                "SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice FROM customer c JOIN nation n ON c.c_nationkey = n.n_nationkey JOIN region r ON n.n_regionkey = r.r_regionkey JOIN orders o ON c.c_custkey = o.o_custkey WHERE r.r_name = 'EUROPE' AND o.o_orderdate BETWEEN '1996-01-01' AND '1996-12-31';");
    }

    @GetMapping(value = "/c5", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> c5(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("c5", repetitions, services,
                "C5) Left Outer Join",
                "SELECT c.c_custkey, c.c_name, o.o_orderkey, o.o_orderdate FROM customer c LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey WHERE c.c_nationkey = 3;");
    }

    // ── D-series ──────────────────────────────────────────────────────────────

    @GetMapping(value = "/d1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> d1(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("d1", repetitions, services,
                "D1) UNION",
                "(SELECT c_nationkey FROM customer WHERE c_acctbal > 9000) UNION (SELECT s_nationkey FROM supplier;");
    }

    @GetMapping(value = "/d2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> d2(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("d2", repetitions, services,
                "D2) INTERSECT",
                "SELECT DISTINCT c_nationkey FROM customer WHERE c_acctbal > 9000 AND c_nationkey IN (SELECT s_nationkey FROM supplier;");
    }

    @GetMapping(value = "/d3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> d3(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("d3", repetitions, services,
                "D3) DIFFERENCE",
                "SELECT DISTINCT c_nationkey FROM customer WHERE c_acctbal > 9000 AND c_nationkey NOT IN (SELECT s_nationkey FROM supplier;");
    }

    // ── E-series ──────────────────────────────────────────────────────────────

    @GetMapping(value = "/e1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> e1(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("e1", repetitions, services,
                "E1) Non-Indexed Columns Sorting",
                "SELECT c_name, c_address, c_acctbal FROM customer ORDER BY c_acctbal DESC;");
    }

    @GetMapping(value = "/e2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> e2(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("e2", repetitions, services,
                "E2) Indexed Columns Sorting",
                "SELECT o_orderkey, o_custkey, o_orderdate, o_totalprice FROM orders ORDER BY o_orderkey;");
    }

    @GetMapping(value = "/e3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> e3(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return run("e3", repetitions, services,
                "E3) Distinct",
                "SELECT DISTINCT c_nationkey, c_mktsegment FROM customer;");
    }

    // ── R-series (embedded document model — Morphia only) ─────────────────────

    @GetMapping(value = "/r1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> r1(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return runEmbedded("r1", repetitions, services,
                "R1) Embedded Array Filter — Non-Indexed Field",
                EMBEDDED_QUERY_DESCRIPTIONS.get("r1"));
    }

    @GetMapping(value = "/r2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> r2(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return runEmbedded("r2", repetitions, services,
                "R2) Embedded Array Filter — Indexed Field",
                EMBEDDED_QUERY_DESCRIPTIONS.get("r2"));
    }

    @GetMapping(value = "/r3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> r3(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return runEmbedded("r3", repetitions, services,
                "R3) Array Tags Filter — No Index",
                EMBEDDED_QUERY_DESCRIPTIONS.get("r3"));
    }

    @GetMapping(value = "/r4", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> r4(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return runEmbedded("r4", repetitions, services,
                "R4) Array Tags Filter — Indexed Field",
                EMBEDDED_QUERY_DESCRIPTIONS.get("r4"));
    }

    @GetMapping(value = "/r5", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> r5(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return runEmbedded("r5", repetitions, services,
                "R5) Deeply Nested Document Filter",
                EMBEDDED_QUERY_DESCRIPTIONS.get("r5"));
    }

    @GetMapping(value = "/r6", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> r6(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return runEmbedded("r6", repetitions, services,
                "R6) Regex Text Search — No Text Index",
                EMBEDDED_QUERY_DESCRIPTIONS.get("r6"));
    }

    @GetMapping(value = "/r7", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> r7(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return runEmbedded("r7", repetitions, services,
                "R7) Text Index Search",
                EMBEDDED_QUERY_DESCRIPTIONS.get("r7"));
    }

    @GetMapping(value = "/r8", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> r8(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return runEmbedded("r8", repetitions, services,
                "R8) Unwind Embedded Array",
                EMBEDDED_QUERY_DESCRIPTIONS.get("r8"));
    }

    @GetMapping(value = "/r9", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> r9(
            @RequestParam Optional<String> repetitions,
            @RequestParam Optional<String> services) {
        return runEmbedded("r9", repetitions, services,
                "R9) Aggregation on Embedded Array",
                EMBEDDED_QUERY_DESCRIPTIONS.get("r9"));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> runEmbedded(
            String queryId, Optional<String> repetitions, Optional<String> services,
            String name, String description) {
        Set<String> selected = parseEmbeddedServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = new LinkedHashMap<>();
        results.put("query", name);
        results.put("description", description);
        for (String svc : selected) {
            AbstractEmbeddedOrmService service = embeddedServiceMap.get(svc);
            if (service == null) continue;
            try {
                Map<String, Object> response = service.executeEmbeddedQuery(queryId, rep);
                Map<String, Object> mapped = new LinkedHashMap<>();
                mapped.put("status",               response.getOrDefault("status", "success"));
                mapped.put("repetition",           response.get("repetitions"));
                mapped.put("averageExecutionTime", response.get("elapsed"));
                mapped.put("minExecutionTime",     response.get("minTime"));
                mapped.put("maxExecutionTime",     response.get("maxTime"));
                mapped.put("averageMemoryUsage",   response.get("delta"));
                mapped.put("minMemoryUsage",       response.get("minMemory"));
                mapped.put("maxMemoryUsage",       response.get("maxMemory"));
                mapped.put("iterationResults",     response.get("iterationResults"));
                results.put(svc, mapped);
            } catch (Exception e) {
                results.put(svc, Map.of("status", "error", "error", e.getMessage()));
            }
        }
        return ResponseEntity.ok(results);
    }

    private ResponseEntity<Map<String, Object>> run(
            String queryId, Optional<String> repetitions, Optional<String> services,
            String name, String description) {
        Set<String> selected = parseServices(services);
        int rep = parseRepetitions(repetitions);
        Map<String, Object> results = new LinkedHashMap<>();
        results.put("query", name);
        results.put("description", description);
        for (String svc : selected) {
            AbstractOrmService service = serviceMap.get(svc);
            if (service == null) continue;
            try {
                Map<String, Object> response = service.executeQuery(queryId, rep);
                Map<String, Object> mapped = new LinkedHashMap<>();
                mapped.put("status",               response.getOrDefault("status", "success"));
                mapped.put("repetition",           response.get("repetitions"));
                mapped.put("averageExecutionTime", response.get("elapsed"));
                mapped.put("minExecutionTime",     response.get("minTime"));
                mapped.put("maxExecutionTime",     response.get("maxTime"));
                mapped.put("averageMemoryUsage",   response.get("delta"));
                mapped.put("minMemoryUsage",       response.get("minMemory"));
                mapped.put("maxMemoryUsage",       response.get("maxMemory"));
                mapped.put("iterationResults",     response.get("iterationResults"));
                results.put(svc, mapped);
            } catch (Exception e) {
                results.put(svc, Map.of("status", "error", "error", e.getMessage()));
            }
        }
        return ResponseEntity.ok(results);
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

    private Set<String> parseEmbeddedServices(Optional<String> servicesOpt) {
        List<String> embeddedServicesList = Arrays.asList(EMBEDDED_SERVICES);
        return servicesOpt
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim)
                        .filter(embeddedServicesList::contains)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .orElse(new LinkedHashSet<>(embeddedServicesList));
    }

    private int parseRepetitions(Optional<String> repetitions) {
        try {
            return Integer.parseInt(repetitions.orElse("10"));
        } catch (NumberFormatException e) {
            return 10;
        }
    }
}
