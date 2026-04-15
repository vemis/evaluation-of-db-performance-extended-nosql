package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMysqlJooqClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JooqService {
    private final MicroserviceMysqlJooqClient jooqClient;

    public JooqService(MicroserviceMysqlJooqClient jooqClient) {
        this.jooqClient = jooqClient;
    }

    public String healthCheck() {
        return jooqClient.health().getBody();
    }

    public Map<String, Object> getPricingSummary(int repetitions) {
        Map<String, Object> response = jooqClient.getPricingSummary(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getMinimumCostSupplier(int repetitions) {
        Map<String, Object> response = jooqClient.getMinimumCostSupplier(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getShippingPriority(int repetitions) {
        Map<String, Object> response = jooqClient.getShippingPriority(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getOrderPriorityChecking(int repetitions) {
        Map<String, Object> response = jooqClient.getOrderPriorityChecking(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getLocalSupplierVolume(int repetitions) {
        Map<String, Object> response = jooqClient.getLocalSupplierVolume(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA1(int repetitions) {
        Map<String, Object> response = jooqClient.getNonIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA2(int repetitions) {
        Map<String, Object> response = jooqClient.getNonIndexedColumnsRangeQuery("1996-01-01", "1996-12-31", repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA3(int repetitions) {
        Map<String, Object> response = jooqClient.getIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA4(int repetitions) {
        Map<String, Object> response = jooqClient.getIndexedColumnsRangeQuery(1000, 50000, repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryB1(int repetitions) {
        Map<String, Object> response = jooqClient.getCount(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryB2(int repetitions) {
        Map<String, Object> response = jooqClient.getMax(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC1(int repetitions) {
        Map<String, Object> response = jooqClient.getJoinNonIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC2(int repetitions) {
        Map<String, Object> response = jooqClient.getJoinIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC3(int repetitions) {
        Map<String, Object> response = jooqClient.getComplexJoin1(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC4(int repetitions) {
        Map<String, Object> response = jooqClient.getComplexJoin2(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC5(int repetitions) {
        Map<String, Object> response = jooqClient.getLeftOuterJoin(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD1(int repetitions) {
        Map<String, Object> response = jooqClient.getUnion(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD2(int repetitions) {
        Map<String, Object> response = jooqClient.getIntersect(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD3(int repetitions) {
        Map<String, Object> response = jooqClient.getDifference(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE1(int repetitions) {
        Map<String, Object> response = jooqClient.getNonIndexedColumnsSorting(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE2(int repetitions) {
        Map<String, Object> response = jooqClient.getIndexedColumnsSorting(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE3(int repetitions) {
        Map<String, Object> response = jooqClient.getDistinct(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }
}
