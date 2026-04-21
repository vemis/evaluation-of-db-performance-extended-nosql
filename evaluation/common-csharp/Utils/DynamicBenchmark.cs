using BenchmarkDotNet.Attributes;
using System.Diagnostics;

namespace CommonCSharp.Utils
{
    /// <summary>
    /// BenchmarkDotNet benchmark class used by QueryExecutor.
    ///
    /// Mirrors Java's DynamicBenchmark: at runtime QueryExecutor sets
    /// BenchmarkHolder.Current to the query lambda, then calls
    /// BenchmarkRunner.Run() which drives warmup and measurement iterations
    /// through this method.
    ///
    /// [IterationSetup] forces GC before every iteration, matching JMH's
    /// shouldDoGC(true). Each invocation records elapsed time (Stopwatch) and
    /// memory allocated (GC.GetAllocatedBytesForCurrentThread, the .NET
    /// equivalent of ThreadMXBean.getThreadAllocatedBytes) and appends them to
    /// BenchmarkHolder.IterationResults so the per-iteration detail table in
    /// the frontend continues to work.
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
            long allocBefore = GC.GetAllocatedBytesForCurrentThread();
            long t0 = Stopwatch.GetTimestamp();

            int size = await BenchmarkHolder.Current!();

            double elapsedMs = Stopwatch.GetElapsedTime(t0).TotalMilliseconds;
            long allocAfter = GC.GetAllocatedBytesForCurrentThread();
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
