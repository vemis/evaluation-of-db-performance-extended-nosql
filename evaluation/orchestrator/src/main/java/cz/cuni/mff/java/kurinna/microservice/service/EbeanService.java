package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMysqlEbeanClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EbeanService {
    private final MicroserviceMysqlEbeanClient ebeanClient;

    public EbeanService(MicroserviceMysqlEbeanClient ebeanClient) {
        this.ebeanClient = ebeanClient;
    }

    public String healthCheck() {
        return ebeanClient.health().getBody();
    }

    public Map<String, Object> getPricingSummary(int repetitions) {
        Map<String, Object> response = ebeanClient.getPricingSummary(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getMinimumCostSupplier(int repetitions) {
        Map<String, Object> response = ebeanClient.getMinimumCostSupplier(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getShippingPriority(int repetitions) {
        Map<String, Object> response = ebeanClient.getShippingPriority(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getOrderPriorityChecking(int repetitions) {
        Map<String, Object> response = ebeanClient.getOrderPriorityChecking(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getLocalSupplierVolume(int repetitions) {
        Map<String, Object> response = ebeanClient.getLocalSupplierVolume(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA1(int repetitions) {
        Map<String, Object> response = ebeanClient.getNonIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA2(int repetitions) {
        Map<String, Object> response = ebeanClient.getNonIndexedColumnsRangeQuery("1996-01-01", "1996-12-31", repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA3(int repetitions) {
        Map<String, Object> response = ebeanClient.getIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA4(int repetitions) {
        Map<String, Object> response = ebeanClient.getIndexedColumnsRangeQuery(1000, 50000, repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryB1(int repetitions) {
        Map<String, Object> response = ebeanClient.getCount(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryB2(int repetitions) {
        Map<String, Object> response = ebeanClient.getMax(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC1(int repetitions) {
        Map<String, Object> response = ebeanClient.getJoinNonIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC2(int repetitions) {
        Map<String, Object> response = ebeanClient.getJoinIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC3(int repetitions) {
        Map<String, Object> response = ebeanClient.getComplexJoin1(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC4(int repetitions) {
        Map<String, Object> response = ebeanClient.getComplexJoin2(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC5(int repetitions) {
        Map<String, Object> response = ebeanClient.getLeftOuterJoin(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD1(int repetitions) {
        Map<String, Object> response = ebeanClient.getUnion(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD2(int repetitions) {
        Map<String, Object> response = ebeanClient.getIntersect(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD3(int repetitions) {
        Map<String, Object> response = ebeanClient.getDifference(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE1(int repetitions) {
        Map<String, Object> response = ebeanClient.getNonIndexedColumnsSorting(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE2(int repetitions) {
        Map<String, Object> response = ebeanClient.getIndexedColumnsSorting(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE3(int repetitions) {
        Map<String, Object> response = ebeanClient.getDistinct(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }
}
