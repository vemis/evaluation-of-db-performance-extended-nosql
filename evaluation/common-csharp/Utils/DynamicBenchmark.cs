using BenchmarkDotNet.Attributes;
using System.Diagnostics;

namespace CommonCSharp.Utils
{
    /// <summary>
    /// BenchmarkDotNet benchmark class used by QueryExecutor.
    ///
    /// Mirrors Java's DynamicBenchmark: at runtime QueryExecutor sets
    /// BenchmarkHolder.Current to the query lambda, then calls
    /// BenchmarkRunner.Run() which drives the warmup and measurement iterations
    /// through this method. The warmup iteration is therefore recorded as the
    /// first entry in BenchmarkHolder.IterationResults; QueryExecutor strips it.
    ///
    /// [IterationSetup] forces GC before every iteration, matching JMH's
    /// shouldDoGC(true). Each invocation records elapsed time (Stopwatch) and
    /// memory allocated and appends them to BenchmarkHolder.IterationResults so
    /// the per-iteration detail table in the frontend continues to work.
    ///
    /// Memory uses GC.GetTotalAllocatedBytes (process-wide cumulative allocation),
    /// NOT GetAllocatedBytesForCurrentThread. The query is awaited, and an async
    /// MongoDB call allocates its result on the driver's I/O / continuation
    /// threads while the continuation may resume on a different thread than the
    /// one that captured allocBefore. A thread-local counter therefore compares
    /// two unrelated threads' totals and frequently yields 0 (negative delta
    /// clamped). The process-wide counter is immune to that thread hop. This is
    /// the async-C# analogue of Java's per-thread ThreadMXBean, which is valid
    /// there only because the Java queries are synchronous. It is safe because
    /// the orchestrator benchmarks one query at a time, so almost nothing else
    /// allocates during the measured window. Both counters are cumulative and
    /// unaffected by GC, so a collection cannot zero the measurement.
    ///
    /// Returning the result from [Benchmark] prevents the JIT from eliminating
    /// the call as dead code, matching JMH's Blackhole.consume().
    /// </summary>
    public class DynamicBenchmark
    {
        [IterationSetup]
        public void IterationSetup()
        {
            GC.Collect();
            GC.WaitForPendingFinalizers();
            GC.Collect();
        }

        [Benchmark]
        public async Task<int> Run()
        {
            long allocBefore = GC.GetTotalAllocatedBytes(precise: true);
            long t0 = Stopwatch.GetTimestamp();

            int size = await BenchmarkHolder.Current!();

            double elapsedMs = Stopwatch.GetElapsedTime(t0).TotalMilliseconds;
            long allocAfter = GC.GetTotalAllocatedBytes(precise: true);
            long delta = Math.Max(0L, allocAfter - allocBefore);

            BenchmarkHolder.LastSize = size;

            lock (BenchmarkHolder.IterationResults)
            {
                BenchmarkHolder.IterationResults.Add(new IterationResult
                {
                    elapsed = elapsedMs,
                    delta = delta,
                    result = size,
                    status = "success",
                    jfr = new JfrResult { totalAllocated = delta }
                });
            }

            return size;
        }
    }
}
