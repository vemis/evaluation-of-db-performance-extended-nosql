using CommonCSharp.Utils;

namespace CommonCSharp.Service;

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