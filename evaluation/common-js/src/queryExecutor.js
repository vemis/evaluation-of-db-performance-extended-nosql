import Benchmark from 'benchmark';
import { performance } from 'perf_hooks';

/**
 * Mirrors Java's QueryExecutor + DynamicBenchmark, using benchmark.js as the
 * harness in the same role that JMH plays on the Java side.
 *
 * Mapping to JMH options:
 *   warmupIterations=1    → one manual warmup call before the Benchmark run
 *   measurementIterations → minSamples: repetitions, maxTime: 0 (stop at minSamples)
 *   shouldDoGC(true)      → global.gc() before each deferred cycle
 *   fork=0                → in-process (benchmark.js default, same as JMH fork=0)
 *   Mode.SingleShotTime   → defer: true (one async call per sample)
 *
 * Per-iteration timing and memory are measured manually inside the deferred fn,
 * mirroring DynamicBenchmark.java which uses System.nanoTime() + ThreadMXBean
 * inside the @Benchmark method even though JMH controls the outer loop.
 *
 * Memory note: process.memoryUsage().heapUsed is process-level heap (not
 * thread-local like ThreadMXBean). The delta approximates net heap growth during
 * query execution. GC before each cycle keeps the baseline stable.
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
        // Warmup iteration — discarded, matching JMH warmupIterations=1
        if (global.gc) global.gc();
        await queryFn();

        const measurementCount = Math.max(1, repetitions);

        await new Promise((resolve, reject) => {
            const bench = new Benchmark('query', {
                defer: true,        // one async call per sample (Mode.SingleShotTime)
                minSamples: measurementCount,
                maxTime: 0,         // stop as soon as minSamples is reached
                fn(deferred) {
                    // GC before each measurement cycle, matching shouldDoGC(true)
                    if (global.gc) global.gc();

                    const memBefore = process.memoryUsage().heapUsed;
                    const t0 = performance.now();

                    queryFn()
                        .then(result => {
                            const elapsed = performance.now() - t0;   // milliseconds
                            const delta = Math.max(0, process.memoryUsage().heapUsed - memBefore);

                            lastResultSize = Array.isArray(result) ? result.length : 0;

                            //==================================
                            // Print result for testing purposes
                            console.log('Query results:');
                            (Array.isArray(result) ? result : []).slice(0, 3).forEach(row => console.log(row));
                            console.log('Query size: ' + lastResultSize);
                            //==================================

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

                            deferred.resolve();
                        })
                        .catch(err => {
                            deferred.resolve();
                            reject(err);
                        });
                },
                onComplete() { resolve(); },
                onError(event) { reject(event.target.error); }
            });

            bench.run({ async: true });
        });

        // Trim to exactly measurementCount in case benchmark.js ran extra cycles
        const measured = iterationResults.slice(0, measurementCount);
        const times    = measured.map(r => r.elapsed);
        const deltas   = measured.map(r => r.delta);

        return {
            elapsed:          times.reduce((a, b) => a + b, 0) / times.length,
            minTime:          Math.min(...times),
            maxTime:          Math.max(...times),
            delta:            deltas.reduce((a, b) => a + b, 0) / deltas.length,
            minMemory:        Math.min(...deltas),
            maxMemory:        Math.max(...deltas),
            result:           lastResultSize,
            repetitions:      measured.length,
            iterationResults: measured,
            status:           'success'
        };

    } catch (err) {
        return {
            status: 'error',
            error:  err.message
        };
    }
}
