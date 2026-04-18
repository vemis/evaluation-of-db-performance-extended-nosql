package cz.cuni.mff.java.microservice.service;

import cz.cuni.mff.java.kurinna.common.service.IEmbeddedQueryService;
import cz.cuni.mff.java.microservice.repository.EmbeddedQueryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddedQueryService implements IEmbeddedQueryService<Object> {

    private final EmbeddedQueryRepository embeddedQueryRepository;

    public EmbeddedQueryService(EmbeddedQueryRepository embeddedQueryRepository) {
        this.embeddedQueryRepository = embeddedQueryRepository;
    }

    @Override public List<Object> r1() { return cast(embeddedQueryRepository.r1()); }
    @Override public List<Object> r2() { return cast(embeddedQueryRepository.r2()); }
    @Override public List<Object> r3() { return cast(embeddedQueryRepository.r3()); }
    @Override public List<Object> r4() { return cast(embeddedQueryRepository.r4()); }
    @Override public List<Object> r5() { return cast(embeddedQueryRepository.r5()); }
    @Override public List<Object> r6() { return cast(embeddedQueryRepository.r6()); }
    @Override public List<Object> r7() { return cast(embeddedQueryRepository.r7()); }
    @Override public List<Object> r8() { return cast(embeddedQueryRepository.r8()); }
    @Override public List<Object> r9() { return cast(embeddedQueryRepository.r9()); }

    @SuppressWarnings("unchecked")
    private static List<Object> cast(List<?> list) {
        return (List<Object>) list;
    }
}
