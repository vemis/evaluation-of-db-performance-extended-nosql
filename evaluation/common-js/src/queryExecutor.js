import { performance } from 'perf_hooks';

/**
 * Mirrors Java's QueryExecutor + DynamicBenchmark.
 *
 * Algorithm:
 *   1. One warmup iteration (result discarded), matching JMH warmupIterations=1
 *   2. GC before each iteration via global.gc() when --expose-gc is active,
 *      matching JMH shouldDoGC(true)
 *   3. Per iteration: heapUsed snapshot → run query → heapUsed snapshot → record delta
 *   4. Aggregate: mean/min/max for time and memory delta
 *
 * Memory note: process.memoryUsage().heapUsed is process-level current heap (not
 * thread-local allocated bytes like Java's ThreadMXBean). The delta approximates
 * net heap growth during query execution. GC before each iteration keeps the
 * baseline stable. This is the closest Node.js equivalent to the Java measurement.
 *
 * @param {function(): Promise<Array>} queryFn - async function returning an array
 * @param {number} repetitions - number of measurement iterations (warmup excluded)
 * @returns {Promise<Object>} {elapsed, minTime, maxTime, delta, minMemory, maxMemory,
 *                             repetitions, iterationResults, result, status}
 */
export async function executeWithMeasurement(queryFn, repetitions) {
    const iterationResults = [];
    let lastResultSize = 0;

    try {
        // warmup iteration — discarded, matching Java's warmupIterations=1
        if (global.gc) global.gc();
        await queryFn();

        const count = Math.max(1, repetitions);

        for (let i = 0; i < count; i++) {
            // GC before each measurement, matching Java's shouldDoGC(true)
            if (global.gc) global.gc();

            const memBefore = process.memoryUsage().heapUsed;
            const t0 = performance.now();

            const result = await queryFn();

            const elapsed = performance.now() - t0;   // milliseconds
            const memAfter = process.memoryUsage().heapUsed;
            const delta = Math.max(0, memAfter - memBefore);  // bytes

            lastResultSize = Array.isArray(result) ? result.length : 0;

            // jfr sub-object matches Java's DynamicBenchmark shape;
            // only totalAllocated is populated, the rest are zeros as in Java
            iterationResults.push({
                elapsed,
                delta,
                result: lastResultSize,
                status: 'success',
                jfr: {
                    totalAllocated: delta,
                    gcCount: 0,
                    heapUsedAvg: 0,
                    allocatedInsideTLAB: 0,
                    allocatedOutsideTLAB: 0
                }
            });
        }

        const times  = iterationResults.map(r => r.elapsed);
        const deltas = iterationResults.map(r => r.delta);

        return {
            elapsed:    times.reduce((a, b) => a + b, 0) / times.length,
            minTime:    Math.min(...times),
            maxTime:    Math.max(...times),
            delta:      deltas.reduce((a, b) => a + b, 0) / deltas.length,
            minMemory:  Math.min(...deltas),
            maxMemory:  Math.max(...deltas),
            result:     lastResultSize,
            repetitions: iterationResults.length,
            iterationResults,
            status: 'success'
        };

    } catch (err) {
        return {
            status: 'error',
            error:  err.message
        };
    }
}
