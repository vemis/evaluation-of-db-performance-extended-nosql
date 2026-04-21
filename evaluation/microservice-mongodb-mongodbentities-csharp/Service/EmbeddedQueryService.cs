using CommonCSharp.Utils;
using MongoDBEntitiesMicroservice.Repository;

namespace MongoDBEntitiesMicroservice.Service
{
    public interface IEmbeddedQueryService
    {
        Task<QueryResult> R1(int repetitions);
        Task<QueryResult> R2(int repetitions);
        Task<QueryResult> R3(int repetitions);
        Task<QueryResult> R4(int repetitions);
        Task<QueryResult> R5(int repetitions);
        Task<QueryResult> R6(int repetitions);
        Task<QueryResult> R7(int repetitions);
        Task<QueryResult> R8(int repetitions);
        Task<QueryResult> R9(int repetitions);
    }

    public class EmbeddedQueryService : IEmbeddedQueryService
    {
        private readonly IEmbeddedQueryRepository _repo;

        public EmbeddedQueryService(IEmbeddedQueryRepository repo) => _repo = repo;

        public Task<QueryResult> R1(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.R1, rep);
        public Task<QueryResult> R2(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.R2, rep);
        public Task<QueryResult> R3(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.R3, rep);
        public Task<QueryResult> R4(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.R4, rep);
        public Task<QueryResult> R5(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.R5, rep);
        public Task<QueryResult> R6(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.R6, rep);
        public Task<QueryResult> R7(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.R7, rep);
        public Task<QueryResult> R8(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.R8, rep);
        public Task<QueryResult> R9(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.R9, rep);
    }
}
