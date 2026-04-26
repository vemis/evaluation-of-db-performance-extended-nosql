package cz.cuni.mff.java.kurinna.common.utils;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JMH benchmark class used by QueryExecutor.
 *
 * The annotation processor generates the benchmark harness for this class at
 * compile time. At runtime, QueryExecutor sets SupplierHolder.current to the
 * query lambda, then calls Runner.run() which drives warmup and measurement
 * iterations through this method.
 *
 * Each invocation records elapsed time (nanoTime) and memory allocated
 * (ThreadMXBean) and appends them to SupplierHolder.iterationResults so the
 * per-iteration detail table in the frontend continues to work.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class DynamicBenchmark {

    private static final com.sun.management.ThreadMXBean THREAD_MX;

    static {
        java.lang.management.ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        THREAD_MX = bean instanceof com.sun.management.ThreadMXBean ?
                (com.sun.management.ThreadMXBean) bean : null;
    }

    @Benchmark
    public void run(Blackhole bh) {
        long threadId = Thread.currentThread().threadId();

        long allocBefore = THREAD_MX != null ? THREAD_MX.getThreadAllocatedBytes(threadId) : 0L;
        long t0 = System.nanoTime();

        Collection<?> result = SupplierHolder.current.get();

        //==================================
        // Print result for testing purposes
        System.out.println("Query results:");
        result.stream()
                .limit(3)
                .forEach(System.out::println);

        System.out.println("Query size: " + result.size());
        //==================================

        long elapsedNs = System.nanoTime() - t0;
        long allocAfter = THREAD_MX != null ? THREAD_MX.getThreadAllocatedBytes(threadId) : 0L;
        long delta = Math.max(0L, allocAfter - allocBefore);

        // Consume result to prevent JIT dead-code elimination
        bh.consume(result);
        SupplierHolder.lastSize = result.size();

        // Build per-iteration entry matching the frontend's ServiceMetricType shape.
        // jfr sub-map keeps the same keys as before; only totalAllocated is populated
        // since JMH/ThreadMXBean does not provide the JFR-specific TLAB breakdown.
        Map<String, Object> jfr = new HashMap<>();
        jfr.put("totalAllocated", delta);
        jfr.put("gcCount", 0);
        jfr.put("heapUsedAvg", 0);
        jfr.put("allocatedInsideTLAB", 0);
        jfr.put("allocatedOutsideTLAB", 0);

        Map<String, Object> iterResult = new HashMap<>();
        iterResult.put("elapsed", elapsedNs / 1_000_000.0);
        iterResult.put("delta", delta);
        iterResult.put("result", result.size());
        iterResult.put("status", "success");
        iterResult.put("jfr", jfr);

        SupplierHolder.iterationResults.add(iterResult);
    }
}
