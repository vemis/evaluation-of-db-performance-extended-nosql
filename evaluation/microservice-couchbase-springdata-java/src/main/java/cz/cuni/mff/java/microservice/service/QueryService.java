package cz.cuni.mff.java.microservice.service;

import cz.cuni.mff.java.kurinna.common.service.IQueryService;
import cz.cuni.mff.java.microservice.repository.QueryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class QueryService implements IQueryService<Object> {

    private final QueryRepository queryRepository;

    public QueryService(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Override public List<Object> a1() { return cast(queryRepository.a1()); }
    @Override public List<Object> a2(LocalDate s, LocalDate e) { return cast(queryRepository.a2(s, e)); }
    @Override public List<Object> a3() { return cast(queryRepository.a3()); }
    @Override public List<Object> a4(int min, int max) { return cast(queryRepository.a4(min, max)); }
    @Override public List<Object> b1() { return cast(queryRepository.b1()); }
    @Override public List<Object> b2() { return cast(queryRepository.b2()); }
    @Override public List<Object> c1() { return cast(queryRepository.c1()); }
    @Override public List<Object> c2() { return cast(queryRepository.c2()); }
    @Override public List<Object> c3() { return cast(queryRepository.c3()); }
    @Override public List<Object> c4() { return cast(queryRepository.c4()); }
    @Override public List<Object> c5() { return cast(queryRepository.c5()); }
    @Override public List<Object> d1() { return cast(queryRepository.d1()); }
    @Override public List<Object> d2() { return cast(queryRepository.d2()); }
    @Override public List<Object> d3() { return cast(queryRepository.d3()); }
    @Override public List<Object> e1() { return cast(queryRepository.e1()); }
    @Override public List<Object> e2() { return cast(queryRepository.e2()); }
    @Override public List<Object> e3() { return cast(queryRepository.e3()); }
    @Override public List<Object> q1(int deltaDays) { return cast(queryRepository.q1(deltaDays)); }
    @Override public List<Object> q2(int size, String type, String region) { return cast(queryRepository.q2(size, type, region)); }
    @Override public List<Object> q3(String segment, LocalDate orderDate, LocalDate shipDate) { return cast(queryRepository.q3(segment, orderDate, shipDate)); }
    @Override public List<Object> q4(LocalDate orderDate) { return cast(queryRepository.q4(orderDate)); }
    @Override public List<Object> q5(String region, LocalDate orderDate) { return cast(queryRepository.q5(region, orderDate)); }

    @SuppressWarnings("unchecked")
    private static List<Object> cast(List<?> list) {
        return (List<Object>) list;
    }
}
