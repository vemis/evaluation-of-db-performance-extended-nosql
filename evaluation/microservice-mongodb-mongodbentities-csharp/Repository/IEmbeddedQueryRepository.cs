using MongoDB.Bson;
using MongoDBEntitiesMicroservice.Model.Embedded;

namespace MongoDBEntitiesMicroservice.Repository;

public interface IEmbeddedQueryRepository
{
    Task<List<BsonDocument>> R1();
    Task<List<BsonDocument>> R2();
    Task<List<OrdersEWithLineitemsArrayAsTags>> R3();
    Task<List<OrdersEWithLineitemsArrayAsTagsIndexed>> R4();
    Task<List<OrdersEWithCustomerWithNationWithRegion>> R5();
    Task<List<OrdersEOnlyOComment>> R6();
    Task<List<OrdersEOnlyOCommentIndexed>> R7();
    Task<List<BsonDocument>> R8();
    Task<List<BsonDocument>> R9();
}