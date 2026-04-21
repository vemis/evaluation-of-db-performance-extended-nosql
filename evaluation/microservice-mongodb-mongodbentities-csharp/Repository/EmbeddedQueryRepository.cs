using CommonCSharp.Models.TPC_H;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Entities;

namespace MongoDBEntitiesMicroservice.Repository
{
    public interface IEmbeddedQueryRepository
    {
        Task<int> R1();
        Task<int> R2();
        Task<int> R3();
        Task<int> R4();
        Task<int> R5();
        Task<int> R6();
        Task<int> R7();
        Task<int> R8();
        Task<int> R9();
    }

    public class EmbeddedQueryRepository : IEmbeddedQueryRepository
    {
        public async Task<int> R1()
        {
            var result = await DB.Collection<OrdersEWithLineitems>()
                .Aggregate()
                .Match(Builders<OrdersEWithLineitems>.Filter.Gt("o_lineitems.l_quantity", 5))
                .Project<BsonDocument>(new BsonDocument { { "o_orderdate", 1 }, { "o_lineitems.l_partkey", 1 } })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> R2()
        {
            var result = await DB.Collection<OrdersEWithLineitems>()
                .Aggregate()
                .Match(Builders<OrdersEWithLineitems>.Filter.Gt("o_lineitems.l_partkey", 20000))
                .Project<BsonDocument>(new BsonDocument { { "o_orderdate", 1 }, { "o_lineitems.l_partkey", 1 } })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> R3()
        {
            var result = await DB.Collection<OrdersEWithLineitemsArrayAsTags>()
                .Find(Builders<OrdersEWithLineitemsArrayAsTags>.Filter.Eq("o_lineitems_tags", "MAIL"))
                .Project<OrdersEWithLineitemsArrayAsTags>(
                    Builders<OrdersEWithLineitemsArrayAsTags>.Projection
                        .Include("o_orderdate").Include("o_lineitems_tags"))
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> R4()
        {
            var result = await DB.Collection<OrdersEWithLineitemsArrayAsTagsIndexed>()
                .Find(Builders<OrdersEWithLineitemsArrayAsTagsIndexed>.Filter.Eq("o_lineitems_tags_indexed", "MAIL"))
                .Project<OrdersEWithLineitemsArrayAsTagsIndexed>(
                    Builders<OrdersEWithLineitemsArrayAsTagsIndexed>.Projection
                        .Include("o_orderdate").Include("o_lineitems_tags_indexed"))
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> R5()
        {
            var result = await DB.Collection<OrdersEWithCustomerWithNationWithRegion>()
                .Find(Builders<OrdersEWithCustomerWithNationWithRegion>.Filter
                    .Eq("o_customer.c_nation.n_region.r_name", "AMERICA"))
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> R6()
        {
            var result = await DB.Collection<OrdersEOnlyOComment>()
                .Find(Builders<OrdersEOnlyOComment>.Filter.Regex("o_comment",
                    new BsonRegularExpression("furiously", "i")))
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> R7()
        {
            var result = await DB.Collection<OrdersEOnlyOCommentIndexed>()
                .Find(Builders<OrdersEOnlyOCommentIndexed>.Filter.Text("furiously"))
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> R8()
        {
            var result = await DB.Collection<OrdersEWithLineitems>()
                .Aggregate()
                .Unwind("o_lineitems")
                .Project<BsonDocument>(new BsonDocument { { "o_lineitems.l_partkey", 1 } })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> R9()
        {
            var result = await DB.Collection<OrdersEWithLineitems>()
                .Aggregate()
                .Unwind("o_lineitems")
                .Group<BsonDocument>(new BsonDocument
                {
                    { "_id", "$_id" },
                    { "totalRevenue", new BsonDocument("$sum", "$o_lineitems.l_extendedprice") }
                })
                .ToListAsync();
            return result.Count;
        }
    }
}
