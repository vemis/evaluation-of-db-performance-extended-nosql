package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.MicroserviceMongodbMorphiaJavaClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MorphiaService {

    private final MicroserviceMongodbMorphiaJavaClient morphiaClient;

    public MorphiaService(MicroserviceMongodbMorphiaJavaClient morphiaClient) {
        this.morphiaClient = morphiaClient;
    }

    public String health() {
        return morphiaClient.health().getBody();
    }

    public String load() {
        return morphiaClient.load().getBody();
    }

    public Map<String, Object> executeQueryA1(int repetitions) {
        Map<String, Object> response = morphiaClient.getNonIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA2(int repetitions) {
        Map<String, Object> response = morphiaClient.getNonIndexedColumnsRangeQuery("1996-01-01", "1996-12-31", repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA3(int repetitions) {
        Map<String, Object> response = morphiaClient.getIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryA4(int repetitions) {
        Map<String, Object> response = morphiaClient.getIndexedColumnsRangeQuery(1000, 50000, repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryB1(int repetitions) {
        Map<String, Object> response = morphiaClient.getCount(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryB2(int repetitions) {
        Map<String, Object> response = morphiaClient.getMax(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC1(int repetitions) {
        Map<String, Object> response = morphiaClient.getJoinNonIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC2(int repetitions) {
        Map<String, Object> response = morphiaClient.getJoinIndexedColumns(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC3(int repetitions) {
        Map<String, Object> response = morphiaClient.getComplexJoin1(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC4(int repetitions) {
        Map<String, Object> response = morphiaClient.getComplexJoin2(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryC5(int repetitions) {
        Map<String, Object> response = morphiaClient.getLeftOuterJoin(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD1(int repetitions) {
        Map<String, Object> response = morphiaClient.getUnion(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD2(int repetitions) {
        Map<String, Object> response = morphiaClient.getIntersect(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryD3(int repetitions) {
        Map<String, Object> response = morphiaClient.getDifference(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE1(int repetitions) {
        Map<String, Object> response = morphiaClient.getNonIndexedColumnsSorting(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE2(int repetitions) {
        Map<String, Object> response = morphiaClient.getIndexedColumnsSorting(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> executeQueryE3(int repetitions) {
        Map<String, Object> response = morphiaClient.getDistinct(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getPricingSummary(int repetitions) {
        Map<String, Object> response = morphiaClient.getPricingSummary(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getMinimumCostSupplier(int repetitions) {
        Map<String, Object> response = morphiaClient.getMinimumCostSupplier(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getShippingPriority(int repetitions) {
        Map<String, Object> response = morphiaClient.getShippingPriority(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getOrderPriorityChecking(int repetitions) {
        Map<String, Object> response = morphiaClient.getOrderPriorityChecking(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }

    public Map<String, Object> getLocalSupplierVolume(int repetitions) {
        Map<String, Object> response = morphiaClient.getLocalSupplierVolume(repetitions).getBody();
        if (response == null || response.isEmpty()) throw new RuntimeException("No data found");
        return response;
    }
}
