package cz.cuni.mff.java.kurinna.common.controller;

import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Interface defining all query endpoints for microservices.
 * Each microservice should implement this interface to provide consistent API.
 */
public interface IQueryController {
    /**
     * Health check endpoint.
     * @return Response with status "OK" if service is running
     */
    ResponseEntity<String> health();

    /**
     * A1) Non-Indexed Columns query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> a1(int repetitions);

    /**
     * A2) Non-Indexed Columns — Range Query.
     * @param startDate   Start date for the range query in ISO format (yyyy-MM-dd)
     * @param endDate     End date for the range query in ISO format (yyyy-MM-dd)
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> a2(String startDate, String endDate, int repetitions);

    /**
     * A3) Indexed Columns query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> a3(int repetitions);

    /**
     * A4) Indexed Columns — Range Query.
     * @param minOrderKey minimum order key value
     * @param maxOrderKey maximum order key value
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> a4(int minOrderKey, int maxOrderKey, int repetitions);

    /**
     * B1) COUNT aggregate function query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> b1(int repetitions);

    /**
     * B2) MAX aggregate function query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> b2(int repetitions);

    /**
     * C1) Non-Indexed Columns join query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> c1(int repetitions);

    /**
     * C2) Indexed Columns join query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> c2(int repetitions);

    /**
     * C3) Complex Join 1 query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> c3(int repetitions);

    /**
     * C4) Complex Join 2 query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> c4(int repetitions);

    /**
     * C5) Left Outer Join query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> c5(int repetitions);

    /**
     * D1) UNION set operation query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> d1(int repetitions);

    /**
     * D2) INTERSECT set operation query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> d2(int repetitions);

    /**
     * D3) DIFFERENCE set operation query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> d3(int repetitions);

    /**
     * E1) Non-Indexed Columns Sorting query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> e1(int repetitions);

    /**
     * E2) Indexed Columns Sorting query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> e2(int repetitions);

    /**
     * E3) Distinct query.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> e3(int repetitions);

    /**
     * Q1) TPC-H Query 1 variant.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> q1(int repetitions);

    /**
     * Q2) TPC-H Query 2 variant.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> q2(int repetitions);

    /**
     * Q3) TPC-H Query 3 variant.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> q3(int repetitions);

    /**
     * Q4) TPC-H Query 4 variant.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> q4(int repetitions);

    /**
     * Q5) TPC-H Query 5 variant.
     * @param repetitions total benchmark runs (first is warmup)
     * @return Response containing query results and execution metrics
     */
    ResponseEntity<Map<String, Object>> q5(int repetitions);
}
