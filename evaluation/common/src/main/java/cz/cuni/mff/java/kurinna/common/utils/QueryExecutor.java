package cz.cuni.mff.java.kurinna.common.utils;

import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.util.Statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Executes a query supplier under JMH measurement.
 *
 * Replaces the previous hand-rolled JFR implementation. JMH handles:
 * <ul>
 *   <li>One warmup iteration (discarded from stats, matching the old behaviour
 *       of the orchestrator ignoring the first run)</li>
 *   <li>GC between every iteration via {@code shouldDoGC(true)}</li>
 *   <li>Dead-code elimination prevention via {@link org.openjdk.jmh.infra.Blackhole}</li>
 *   <li>Statistical aggregation (mean / min / max)</li>
 * </ul>
 *
 * Memory is measured per-iteration inside {@link DynamicBenchmark} using
 * {@code com.sun.management.ThreadMXBean#getThreadAllocatedBytes}, which is
 * the same per-thread allocation counter the previous JFR recording relied on.
 *
 * <p><b>Concurrency note:</b> uses a static volatile field in
 * {@link SupplierHolder} — not safe if two requests benchmark the same
 * microservice concurrently. The orchestrator calls microservices sequentially
 * so this is not a problem in practice.</p>
 */
public class QueryExecutor {

    /**
     * Executes the given query supplier under JMH and returns aggregated
     * timing and memory metrics together with per-iteration detail.
     *
     * @param supplier    the query to benchmark (must return a non-null Collection)
     * @param repetitions total number of runs; the first is treated as warmup
     *                    and excluded from statistics (minimum effective value: 2)
     * @return map with keys: elapsed, minTime, maxTime, delta, minMemory,
     *         maxMemory, repetitions, iterationResults, result, status
     */
    public static <T extends Collection> Map<String, Object> executeWithMeasurement(
            Supplier<T> supplier, int repetitions) {

        SupplierHolder.current = supplier;
        SupplierHolder.iterationResults.clear();

        Map<String, Object> response = new HashMap<>();

        try {
            // this mimics the original - not a good design
            // now it is repetitions + warmup
            //int measurementIterations = Math.max(1, repetitions - 1);
            int measurementIterations = Math.max(1, repetitions );

            Options opt = new OptionsBuilder()
                    .include(DynamicBenchmark.class.getName())
                    .forks(0)                    // must be 0 — already inside a running JVM
                    .warmupIterations(1)         // first run discarded, matching old behaviour
                    .measurementIterations(measurementIterations)
                    .shouldDoGC(true)            // replaces the old System.gc() + sleep(100)
                    .build();

            Collection<RunResult> jmhResults = new Runner(opt).run();
            RunResult runResult = jmhResults.iterator().next();

            // JMH primary result covers only measurement iterations
            Statistics timeStats = runResult.getPrimaryResult().getStatistics();

            // iterationResults has warmup(1) + measurement entries; drop the warmup entry
            List<Map<String, Object>> allIters =
                    new ArrayList<>(SupplierHolder.iterationResults);
            List<Map<String, Object>> measurementIters =
                    allIters.size() > 1 ? allIters.subList(1, allIters.size()) : allIters;

            // Memory stats derived from per-iteration ThreadMXBean data
            double avgDelta = measurementIters.stream()
                    .mapToLong(m -> ((Number) m.get("delta")).longValue())
                    .average()
                    .orElse(0.0);
            long minDelta = measurementIters.stream()
                    .mapToLong(m -> ((Number) m.get("delta")).longValue())
                    .min()
                    .orElse(0L);
            long maxDelta = measurementIters.stream()
                    .mapToLong(m -> ((Number) m.get("delta")).longValue())
                    .max()
                    .orElse(0L);

            response.put("elapsed",     timeStats.getMean());
            response.put("minTime",     timeStats.getMin());
            response.put("maxTime",     timeStats.getMax());
            response.put("delta",       avgDelta);
            response.put("minMemory",   minDelta);
            response.put("maxMemory",   maxDelta);
            response.put("result",      SupplierHolder.lastSize);
            response.put("repetitions", measurementIters.size());
            response.put("iterationResults", new ArrayList<>(measurementIters));
            response.put("status",      "success");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("error",  e.getMessage());
        } finally {
            SupplierHolder.current = null;
            SupplierHolder.iterationResults.clear();
        }

        return response;
    }
}
