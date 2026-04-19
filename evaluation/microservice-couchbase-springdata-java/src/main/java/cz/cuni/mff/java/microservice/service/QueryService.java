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

    @Override
    public List<Object> a1() {
        return (List<Object>) (List<?>) queryRepository.a1();
    }

    @Override
    public List<Object> a2(LocalDate startDate, LocalDate endDate) {
        return (List<Object>) (List<?>) queryRepository.a2(startDate, endDate);
    }

    @Override
    public List<Object> a3() {
        return (List<Object>) (List<?>) queryRepository.a3();
    }

    @Override
    public List<Object> a4(int minOrderKey, int maxOrderKey) {
        return (List<Object>) (List<?>) queryRepository.a4(minOrderKey, maxOrderKey);
    }

    @Override
    public List<Object> b1() {
        return (List<Object>) (List<?>) queryRepository.b1();
    }

    @Override
    public List<Object> b2() {
        return (List<Object>) (List<?>) queryRepository.b2();
    }

    @Override
    public List<Object> c1() {
        return (List<Object>) (List<?>) queryRepository.c1();
    }

    @Override
    public List<Object> c2() {
        return (List<Object>) (List<?>) queryRepository.c2();
    }

    @Override
    public List<Object> c3() {
        return (List<Object>) (List<?>) queryRepository.c3();
    }

    @Override
    public List<Object> c4() {
        return (List<Object>) (List<?>) queryRepository.c4();
    }

    @Override
    public List<Object> c5() {
        return (List<Object>) (List<?>) queryRepository.c5();
    }

    @Override
    public List<Object> d1() {
        return (List<Object>) (List<?>) queryRepository.d1();
    }

    @Override
    public List<Object> d2() {
        return (List<Object>) (List<?>) queryRepository.d2();
    }

    @Override
    public List<Object> d3() {
        return (List<Object>) (List<?>) queryRepository.d3();
    }

    @Override
    public List<Object> e1() {
        return (List<Object>) (List<?>) queryRepository.e1();
    }

    @Override
    public List<Object> e2() {
        return (List<Object>) (List<?>) queryRepository.e2();
    }

    @Override
    public List<Object> e3() {
        return (List<Object>) (List<?>) queryRepository.e3();
    }

    @Override
    public List<Object> q1(int deltaDays) {
        return (List<Object>) (List<?>) queryRepository.q1(deltaDays);
    }

    @Override
    public List<Object> q2(int size, String type, String region) {
        return (List<Object>) (List<?>) queryRepository.q2(size, type, region);
    }

    @Override
    public List<Object> q3(String segment, LocalDate orderDate, LocalDate shipDate) {
        return (List<Object>) (List<?>) queryRepository.q3(segment, orderDate, shipDate);
    }

    @Override
    public List<Object> q4(LocalDate orderDate) {
        return (List<Object>) (List<?>) queryRepository.q4(orderDate);
    }

    @Override
    public List<Object> q5(String region, LocalDate orderDate) {
        return (List<Object>) (List<?>) queryRepository.q5(region, orderDate);
    }
}
