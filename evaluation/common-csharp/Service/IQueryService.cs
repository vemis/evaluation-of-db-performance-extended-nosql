using CommonCSharp.Utils;

namespace CommonCSharp.Service;

public interface IQueryService
{
    Task<QueryResult> A1(int repetitions);
    Task<QueryResult> A2(int repetitions);
    Task<QueryResult> A3(int repetitions);
    Task<QueryResult> A4(int repetitions);
    Task<QueryResult> B1(int repetitions);
    Task<QueryResult> B2(int repetitions);
    Task<QueryResult> C1(int repetitions);
    Task<QueryResult> C2(int repetitions);
    Task<QueryResult> C3(int repetitions);
    Task<QueryResult> C4(int repetitions);
    Task<QueryResult> C5(int repetitions);
    Task<QueryResult> D1(int repetitions);
    Task<QueryResult> D2(int repetitions);
    Task<QueryResult> D3(int repetitions);
    Task<QueryResult> E1(int repetitions);
    Task<QueryResult> E2(int repetitions);
    Task<QueryResult> E3(int repetitions);
    Task<QueryResult> Q1(int repetitions);
    Task<QueryResult> Q2(int repetitions);
    Task<QueryResult> Q3(int repetitions);
    Task<QueryResult> Q4(int repetitions);
    Task<QueryResult> Q5(int repetitions);
}