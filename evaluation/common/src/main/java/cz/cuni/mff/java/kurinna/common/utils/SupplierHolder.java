package cz.cuni.mff.java.kurinna.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * Static bridge between QueryExecutor and DynamicBenchmark.
 *
 * QueryExecutor sets {@code current} before calling Runner.run(), and
 * DynamicBenchmark reads it from its worker threads during each iteration.
 * Per-iteration results are written back via {@code iterationResults}.
 *
 * NOTE: not safe for concurrent HTTP requests — the orchestrator calls
 * microservices sequentially, so in practice only one benchmark runs at a time.
 */
public class SupplierHolder {

    /** The query supplier set by QueryExecutor before each JMH run. */
    static volatile Supplier<? extends Collection> current;

    /** Row count captured on the last iteration (measurement or warmup). */
    static volatile int lastSize;

    /**
     * Per-iteration results appended by DynamicBenchmark.run().
     * First entry is the warmup iteration; QueryExecutor strips it before
     * building the final response.
     */
    static final List<Map<String, Object>> iterationResults = new CopyOnWriteArrayList<>();
}
