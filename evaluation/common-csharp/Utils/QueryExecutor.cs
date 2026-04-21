using System.Diagnostics;

namespace CommonCSharp.Utils
{
    /// <summary>
    /// Mirrors Java's QueryExecutor + DynamicBenchmark and JS executeWithMeasurement.
    ///
    /// Algorithm:
    ///   1. One warmup iteration (discarded), matching JMH warmupIterations=1
    ///   2. GC.Collect() before each iteration, matching JMH shouldDoGC(true)
    ///   3. Per iteration: GC.GetAllocatedBytesForCurrentThread snapshot → run query → snapshot
    ///      GC.GetAllocatedBytesForCurrentThread is the C# equivalent of Java's
    ///      ThreadMXBean.getThreadAllocatedBytes — both give per-thread allocated bytes.
    ///   4. Aggregate: mean/min/max for time (ms) and memory delta (bytes)
    /// </summary>
    public static class QueryExecutor
    {
        public static async Task<QueryResult> ExecuteWithMeasurement(Func<Task<int>> queryFn, int repetitions)
        {
            var iterationResults = new List<IterationResult>();

            try
            {
                // Warmup
                GC.Collect();
                GC.WaitForPendingFinalizers();
                GC.Collect();
                await queryFn();

                int count = Math.Max(1, repetitions);

                for (int i = 0; i < count; i++)
                {
                    GC.Collect();
                    GC.WaitForPendingFinalizers();
                    GC.Collect();

                    long allocBefore = GC.GetAllocatedBytesForCurrentThread();
                    var sw = Stopwatch.StartNew();

                    int resultSize = await queryFn();

                    sw.Stop();
                    long allocAfter = GC.GetAllocatedBytesForCurrentThread();

                    double elapsedMs = sw.Elapsed.TotalMilliseconds;
                    long memDelta = Math.Max(0, allocAfter - allocBefore);

                    iterationResults.Add(new IterationResult
                    {
                        elapsed = elapsedMs,
                        delta = memDelta,
                        result = resultSize,
                        status = "success",
                        jfr = new JfrResult { totalAllocated = memDelta }
                    });
                }

                return new QueryResult
                {
                    elapsed = iterationResults.Average(r => r.elapsed),
                    minTime = iterationResults.Min(r => r.elapsed),
                    maxTime = iterationResults.Max(r => r.elapsed),
                    delta = iterationResults.Average(r => (double)r.delta),
                    minMemory = iterationResults.Min(r => (double)r.delta),
                    maxMemory = iterationResults.Max(r => (double)r.delta),
                    result = iterationResults.Last().result,
                    repetitions = iterationResults.Count,
                    iterationResults = iterationResults,
                    status = "success"
                };
            }
            catch (Exception ex)
            {
                return new QueryResult { status = "error", error = ex.Message };
            }
        }
    }
}
