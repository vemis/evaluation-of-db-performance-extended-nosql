package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMysqlSpringDataJpaClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SpringDataJpaService {
    private final MicroserviceMysqlSpringDataJpaClient springDataJpaClient;

    public SpringDataJpaService(MicroserviceMysqlSpringDataJpaClient springDataJpaClient) {
        this.springDataJpaClient = springDataJpaClient;
    }

    public String healthCheck() {
        return springDataJpaClient.health().getBody();
    }

    public Map<String, Object> getPricingSummary(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getPricingSummary(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getMinimumCostSupplier(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getMinimumCostSupplier(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getShippingPriority(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getShippingPriority(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getOrderPriorityChecking(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getOrderPriorityChecking(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getLocalSupplierVolume(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getLocalSupplierVolume(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA1(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getNonIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA2(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getNonIndexedColumnsRangeQuery("1996-01-01", "1996-12-31", repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA3(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA4(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getIndexedColumnsRangeQuery(1000, 50000, repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryB1(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getCount(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryB2(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getMax(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC1(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getJoinNonIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC2(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getJoinIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC3(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getComplexJoin1(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC4(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getComplexJoin2(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC5(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getLeftOuterJoin(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD1(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getUnion(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD2(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getIntersect(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD3(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getDifference(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE1(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getNonIndexedColumnsSorting(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE2(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getIndexedColumnsSorting(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE3(int repetitions) {
        Map<String, Object> response = springDataJpaClient.getDistinct(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }
}
