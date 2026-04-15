package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMysqlJdbcClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JdbcService {
    private final MicroserviceMysqlJdbcClient jdbcClient;

    public JdbcService(MicroserviceMysqlJdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public String healthCheck() {
        return jdbcClient.health().getBody();
    }

    public Map<String, Object> getPricingSummary(int repetitions) {
        Map<String, Object> response = jdbcClient.getPricingSummary(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getMinimumCostSupplier(int repetitions) {
        Map<String, Object> response = jdbcClient.getMinimumCostSupplier(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getShippingPriority(int repetitions) {
        Map<String, Object> response = jdbcClient.getShippingPriority(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getOrderPriorityChecking(int repetitions) {
        Map<String, Object> response = jdbcClient.getOrderPriorityChecking(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getLocalSupplierVolume(int repetitions) {
        Map<String, Object> response = jdbcClient.getLocalSupplierVolume(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA1(int repetitions) {
        Map<String, Object> response = jdbcClient.getNonIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA2(int repetitions) {
        Map<String, Object> response = jdbcClient.getNonIndexedColumnsRangeQuery("1996-01-01", "1996-12-31", repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA3(int repetitions) {
        Map<String, Object> response = jdbcClient.getIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA4(int repetitions) {
        Map<String, Object> response = jdbcClient.getIndexedColumnsRangeQuery(1000, 50000, repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryB1(int repetitions) {
        Map<String, Object> response = jdbcClient.getCount(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryB2(int repetitions) {
        Map<String, Object> response = jdbcClient.getMax(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC1(int repetitions) {
        Map<String, Object> response = jdbcClient.getJoinNonIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC2(int repetitions) {
        Map<String, Object> response = jdbcClient.getJoinIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC3(int repetitions) {
        Map<String, Object> response = jdbcClient.getComplexJoin1(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC4(int repetitions) {
        Map<String, Object> response = jdbcClient.getComplexJoin2(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC5(int repetitions) {
        Map<String, Object> response = jdbcClient.getLeftOuterJoin(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD1(int repetitions) {
        Map<String, Object> response = jdbcClient.getUnion(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD2(int repetitions) {
        Map<String, Object> response = jdbcClient.getIntersect(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD3(int repetitions) {
        Map<String, Object> response = jdbcClient.getDifference(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE1(int repetitions) {
        Map<String, Object> response = jdbcClient.getNonIndexedColumnsSorting(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE2(int repetitions) {
        Map<String, Object> response = jdbcClient.getIndexedColumnsSorting(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE3(int repetitions) {
        Map<String, Object> response = jdbcClient.getDistinct(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }
}
