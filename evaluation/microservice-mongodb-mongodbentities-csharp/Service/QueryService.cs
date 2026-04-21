using CommonCSharp.Service;
using CommonCSharp.Utils;
using MongoDBEntitiesMicroservice.Repository;

namespace MongoDBEntitiesMicroservice.Service
{

    public class QueryService : IQueryService
    {
        private readonly IRelationalQueryRepository _repo;

        public QueryService(IRelationalQueryRepository repo) => _repo = repo;

        public Task<QueryResult> A1(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.A1, rep);
        public Task<QueryResult> A2(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.A2, rep);
        public Task<QueryResult> A3(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.A3, rep);
        public Task<QueryResult> A4(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.A4, rep);
        public Task<QueryResult> B1(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.B1, rep);
        public Task<QueryResult> B2(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.B2, rep);
        public Task<QueryResult> C1(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.C1, rep);
        public Task<QueryResult> C2(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.C2, rep);
        public Task<QueryResult> C3(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.C3, rep);
        public Task<QueryResult> C4(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.C4, rep);
        public Task<QueryResult> C5(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.C5, rep);
        public Task<QueryResult> D1(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.D1, rep);
        public Task<QueryResult> D2(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.D2, rep);
        public Task<QueryResult> D3(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.D3, rep);
        public Task<QueryResult> E1(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.E1, rep);
        public Task<QueryResult> E2(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.E2, rep);
        public Task<QueryResult> E3(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.E3, rep);
        public Task<QueryResult> Q1(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.Q1, rep);
        public Task<QueryResult> Q2(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.Q2, rep);
        public Task<QueryResult> Q3(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.Q3, rep);
        public Task<QueryResult> Q4(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.Q4, rep);
        public Task<QueryResult> Q5(int rep) => QueryExecutor.ExecuteWithMeasurement(_repo.Q5, rep);
    }
}
