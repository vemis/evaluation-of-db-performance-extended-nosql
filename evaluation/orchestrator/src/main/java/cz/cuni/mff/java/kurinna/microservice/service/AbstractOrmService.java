package cz.cuni.mff.java.kurinna.microservice.service;

import cz.cuni.mff.java.kurinna.microservice.client.IORMClient;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public abstract class AbstractOrmService {

    protected abstract IORMClient client();

    private Map<String, Object> fetch(ResponseEntity<Map<String, Object>> response) {
        Map<String, Object> body = response.getBody();
        if (body == null || body.isEmpty()) throw new RuntimeException("No data found");
        return body;
    }

    public Map<String, Object> executeQuery(String queryId, int repetitions) {
        return switch (queryId) {
            case "a1" -> fetch(client().getNonIndexedColumns(repetitions));
            case "a2" -> fetch(client().getNonIndexedColumnsRangeQuery("1996-01-01", "1996-12-31", repetitions));
            case "a3" -> fetch(client().getIndexedColumns(repetitions));
            case "a4" -> fetch(client().getIndexedColumnsRangeQuery(1000, 50000, repetitions));
            case "b1" -> fetch(client().getCount(repetitions));
            case "b2" -> fetch(client().getMax(repetitions));
            case "c1" -> fetch(client().getJoinNonIndexedColumns(repetitions));
            case "c2" -> fetch(client().getJoinIndexedColumns(repetitions));
            case "c3" -> fetch(client().getComplexJoin1(repetitions));
            case "c4" -> fetch(client().getComplexJoin2(repetitions));
            case "c5" -> fetch(client().getLeftOuterJoin(repetitions));
            case "d1" -> fetch(client().getUnion(repetitions));
            case "d2" -> fetch(client().getIntersect(repetitions));
            case "d3" -> fetch(client().getDifference(repetitions));
            case "e1" -> fetch(client().getNonIndexedColumnsSorting(repetitions));
            case "e2" -> fetch(client().getIndexedColumnsSorting(repetitions));
            case "e3" -> fetch(client().getDistinct(repetitions));
            case "q1" -> fetch(client().getPricingSummary(repetitions));
            case "q2" -> fetch(client().getMinimumCostSupplier(repetitions));
            case "q3" -> fetch(client().getShippingPriority(repetitions));
            case "q4" -> fetch(client().getOrderPriorityChecking(repetitions));
            case "q5" -> fetch(client().getLocalSupplierVolume(repetitions));
            default -> throw new IllegalArgumentException("Unknown query: " + queryId);
        };
    }
}
