namespace CommonCSharp.Utils
{
    /// <summary>
    /// Static bridge between QueryExecutor and DynamicBenchmark.
    ///
    /// QueryExecutor sets Current before calling BenchmarkRunner.Run(), and
    /// DynamicBenchmark reads it during each iteration. Per-iteration results
    /// are written back via IterationResults.
    ///
    /// Mirrors Java's SupplierHolder: not safe for concurrent HTTP requests —
    /// the orchestrator calls microservices sequentially so only one benchmark
    /// runs at a time in practice.
    /// </summary>
    internal static class BenchmarkHolder
    {
        internal static volatile Func<Task<int>>? Current;
        internal static volatile int LastSize;
        internal static readonly List<IterationResult> IterationResults = new();
    }
}
