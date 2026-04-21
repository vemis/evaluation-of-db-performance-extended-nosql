namespace CommonCSharp.Utils
{
    public class QueryResult
    {
        public double elapsed { get; set; }
        public double minTime { get; set; }
        public double maxTime { get; set; }
        public double delta { get; set; }
        public double minMemory { get; set; }
        public double maxMemory { get; set; }
        public int result { get; set; }
        public int repetitions { get; set; }
        public List<IterationResult>? iterationResults { get; set; }
        public string status { get; set; } = "success";
        public string? error { get; set; }
    }

    public class IterationResult
    {
        public double elapsed { get; set; }
        public long delta { get; set; }
        public int result { get; set; }
        public string status { get; set; } = "success";
        public JfrResult jfr { get; set; } = new JfrResult();
    }

    public class JfrResult
    {
        public long totalAllocated { get; set; }
        public int gcCount { get; set; }
        public double heapUsedAvg { get; set; }
        public long allocatedInsideTLAB { get; set; }
        public long allocatedOutsideTLAB { get; set; }
    }
}
