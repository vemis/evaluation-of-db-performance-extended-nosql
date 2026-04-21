using BenchmarkDotNet.Configs;
using BenchmarkDotNet.Engines;
using BenchmarkDotNet.Jobs;
using BenchmarkDotNet.Loggers;
using BenchmarkDotNet.Reports;
using BenchmarkDotNet.Running;
using BenchmarkDotNet.Toolchains.InProcess.NoEmit;

namespace CommonCSharp.Utils
{
    /// <summary>
    /// Executes a query function under BenchmarkDotNet measurement.
    ///
    /// Mirrors Java's QueryExecutor + DynamicBenchmark + SupplierHolder pattern.
    /// BenchmarkDotNet handles:
    ///   - One warmup iteration (discarded from stats, matching JMH warmupIterations=1)
    ///   - GC between every iteration via DynamicBenchmark.[IterationSetup] (matching shouldDoGC(true))
    ///   - Dead-code elimination prevention by consuming the return value
    ///   - Statistical aggregation (mean / min / max)
    ///
    /// InProcessNoEmitToolchain is the .NET equivalent of JMH's forks(0):
    /// the benchmark runs inside the already-running ASP.NET process.
    /// RunStrategy.ColdStart is the equivalent of JMH's Mode.SingleShotTime:
    /// each iteration invokes the benchmark exactly once.
    ///
    /// Memory is measured per-iteration inside DynamicBenchmark using
    /// GC.GetAllocatedBytesForCurrentThread(), the .NET equivalent of
    /// ThreadMXBean.getThreadAllocatedBytes().
    ///
    /// Concurrency note: uses static state in BenchmarkHolder — not safe if two
    /// requests benchmark concurrently. The orchestrator calls microservices
    /// sequentially so this is not a problem in practice.
    /// </summary>
    public static class QueryExecutor
    {
        public static Task<QueryResult> ExecuteWithMeasurement<T>(Func<Task<List<T>>> queryFn, int repetitions)
        {
            return ExecuteWithMeasurement(async () => (await queryFn()).Count, repetitions);
        }

        public static Task<QueryResult> ExecuteWithMeasurement(Func<Task<int>> queryFn, int repetitions)
        {
            BenchmarkHolder.Current = queryFn;
            lock (BenchmarkHolder.IterationResults)
                BenchmarkHolder.IterationResults.Clear();

            try
            {
                int measurementIterations = Math.Max(1, repetitions);

                var config = ManualConfig.CreateEmpty()
                    .WithOptions(ConfigOptions.DisableOptimizationsValidator)
                    .AddJob(Job.Default
                        .WithToolchain(InProcessNoEmitToolchain.Instance)
                        .WithStrategy(RunStrategy.ColdStart)
                        .WithWarmupCount(1)
                        .WithIterationCount(measurementIterations))
                    .AddLogger(NullLogger.Instance);

                Summary summary = BenchmarkRunner.Run<DynamicBenchmark>(config);
                BenchmarkReport report = summary.Reports.First();

                // BDN timing for measurement iterations only (warmup excluded)
                var bdnTimingsMs = report.AllMeasurements
                    .Where(m => m.Is(IterationMode.Workload, IterationStage.Actual))
                    .Select(m => m.Nanoseconds / 1_000_000.0)
                    .ToList();

                // Per-iteration results: first entry is warmup, strip it (mirrors Java)
                List<IterationResult> allIters;
                lock (BenchmarkHolder.IterationResults)
                    allIters = new List<IterationResult>(BenchmarkHolder.IterationResults);

                var measurementIters = allIters.Count > 1
                    ? allIters.Skip(1).ToList()
                    : allIters;

                var result = new QueryResult
                {
                    elapsed   = bdnTimingsMs.Count > 0 ? bdnTimingsMs.Average() : 0,
                    minTime   = bdnTimingsMs.Count > 0 ? bdnTimingsMs.Min()     : 0,
                    maxTime   = bdnTimingsMs.Count > 0 ? bdnTimingsMs.Max()     : 0,
                    delta     = measurementIters.Count > 0 ? measurementIters.Average(r => (double)r.delta) : 0,
                    minMemory = measurementIters.Count > 0 ? (double)measurementIters.Min(r => r.delta)     : 0,
                    maxMemory = measurementIters.Count > 0 ? (double)measurementIters.Max(r => r.delta)     : 0,
                    result       = BenchmarkHolder.LastSize,
                    repetitions  = measurementIters.Count,
                    iterationResults = measurementIters,
                    status = "success"
                };

                return Task.FromResult(result);
            }
            catch (Exception ex)
            {
                return Task.FromResult(new QueryResult { status = "error", error = ex.Message });
            }
            finally
            {
                BenchmarkHolder.Current = null;
                lock (BenchmarkHolder.IterationResults)
                    BenchmarkHolder.IterationResults.Clear();
            }
        }
    }
}
