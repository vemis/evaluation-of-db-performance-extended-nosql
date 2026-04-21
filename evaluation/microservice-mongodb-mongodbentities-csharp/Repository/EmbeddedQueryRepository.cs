using MongoDBEntitiesMicroservice.Model.Embedded;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Entities;

namespace MongoDBEntitiesMicroservice.Repository
{


    public class EmbeddedQueryRepository : IEmbeddedQueryRepository
    {
        public async Task<List<BsonDocument>> R1()
        {
            return await DB.Collection<OrdersEWithLineitems>()
                .Aggregate()
                .Match(Builders<OrdersEWithLineitems>.Filter.Gt("o_lineitems.l_quantity", 5))
                .Project<BsonDocument>(new BsonDocument { { "o_orderdate", 1 }, { "o_lineitems.l_partkey", 1 } })
                .ToListAsync();
        }

        public async Task<List<BsonDocument>> R2()
        {
            return await DB.Collection<OrdersEWithLineitems>()
                .Aggregate()
                .Match(Builders<OrdersEWithLineitems>.Filter.Gt("o_lineitems.l_partkey", 20000))
                .Project<BsonDocument>(new BsonDocument { { "o_orderdate", 1 }, { "o_lineitems.l_partkey", 1 } })
                .ToListAsync();
        }

        public async Task<List<OrdersEWithLineitemsArrayAsTags>> R3()
        {
            return await DB.Collection<OrdersEWithLineitemsArrayAsTags>()
                .Find(Builders<OrdersEWithLineitemsArrayAsTags>.Filter.Eq("o_lineitems_tags", "MAIL"))
                .Project<OrdersEWithLineitemsArrayAsTags>(
                    Builders<OrdersEWithLineitemsArrayAsTags>.Projection
                        .Include("o_orderdate").Include("o_lineitems_tags"))
                .ToListAsync();
        }

        public async Task<List<OrdersEWithLineitemsArrayAsTagsIndexed>> R4()
        {
            return await DB.Collection<OrdersEWithLineitemsArrayAsTagsIndexed>()
                .Find(Builders<OrdersEWithLineitemsArrayAsTagsIndexed>.Filter.Eq("o_lineitems_tags_indexed", "MAIL"))
                .Project<OrdersEWithLineitemsArrayAsTagsIndexed>(
                    Builders<OrdersEWithLineitemsArrayAsTagsIndexed>.Projection
                        .Include("o_orderdate").Include("o_lineitems_tags_indexed"))
                .ToListAsync();
        }

        public async Task<List<OrdersEWithCustomerWithNationWithRegion>> R5()
        {
            return await DB.Collection<OrdersEWithCustomerWithNationWithRegion>()
                .Find(Builders<OrdersEWithCustomerWithNationWithRegion>.Filter
                    .Eq("o_customer.c_nation.n_region.r_name", "AMERICA"))
                .ToListAsync();
        }

        public async Task<List<OrdersEOnlyOComment>> R6()
        {
            return await DB.Collection<OrdersEOnlyOComment>()
                .Find(Builders<OrdersEOnlyOComment>.Filter.Regex("o_comment",
                    new BsonRegularExpression("furiously", "i")))
                .ToListAsync();
        }

        public async Task<List<OrdersEOnlyOCommentIndexed>> R7()
        {
            return await DB.Collection<OrdersEOnlyOCommentIndexed>()
                .Find(Builders<OrdersEOnlyOCommentIndexed>.Filter.Text("furiously"))
                .ToListAsync();
        }

        public async Task<List<BsonDocument>> R8()
        {
            return await DB.Collection<OrdersEWithLineitems>()
                .Aggregate()
                .Unwind("o_lineitems")
                .Project<BsonDocument>(new BsonDocument { { "o_lineitems.l_partkey", 1 } })
                .ToListAsync();
        }

        public async Task<List<BsonDocument>> R9()
        {
            return await DB.Collection<OrdersEWithLineitems>()
                .Aggregate()
                .Unwind("o_lineitems")
                .Group<BsonDocument>(new BsonDocument
                {
                    { "_id", "$_id" },
                    { "totalRevenue", new BsonDocument("$sum", "$o_lineitems.l_extendedprice") }
                })
                .ToListAsync();
        }
    }
}
