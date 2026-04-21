using MongoDB.Bson;
using MongoDBEntitiesMicroservice.Model.Relational;

namespace MongoDBEntitiesMicroservice.Repository;

public interface IRelationalQueryRepository
{
    Task<List<LineitemR>> A1();
    Task<List<OrdersR>> A2();
    Task<List<CustomerR>> A3();
    Task<List<OrdersR>> A4();
    Task<List<BsonDocument>> B1();
    Task<List<BsonDocument>> B2();
    Task<List<BsonDocument>> C1();
    Task<List<BsonDocument>> C2();
    Task<List<BsonDocument>> C3();
    Task<List<BsonDocument>> C4();
    Task<List<BsonDocument>> C5();
    Task<List<BsonDocument>> D1();
    Task<List<BsonDocument>> D2();
    Task<List<BsonDocument>> D3();
    Task<List<BsonDocument>> E1();
    Task<List<BsonDocument>> E2();
    Task<List<BsonDocument>> E3();
    Task<List<BsonDocument>> Q1();
    Task<List<BsonDocument>> Q2();
    Task<List<BsonDocument>> Q3();
    Task<List<BsonDocument>> Q4();
    Task<List<BsonDocument>> Q5();
}