using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Entities;
using MongoDBEntities.Models.TPC_H;
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Linq;

namespace MongoDBEntities.Benchmarks
{
    public class QueriesEMongoDBEntities
    {
        /*
        ### C2) Indexed Columns
        
        This query gives customer names, order dates, and total prices for all customers
        ```sql
        SELECT c.c_name, o.o_orderdate, o.o_totalprice
        FROM customer c
        JOIN orders o ON c.c_custkey = o.o_custkey;
        ```
        */
        public static async Task<List<BsonDocument>> C2()
        {

            var result = await DB.Collection<CustomerEWithOrders>()
                .Aggregate()
                .Unwind(x => x.c_orders)
                .Project<BsonDocument>(new BsonDocument
                {
                    {"c_name", 1},
                    {"o_orderdate", "$c_orders.o_orderdate"},
                    {"o_totalprice", "$c_orders.o_totalprice"}
                })
                .ToListAsync();

            return result;
        }

        /*
        ### R1) Embedded Orders with Lineitems Query

        Test performance of fetching nested documents (1:N relationship embedded).
        ```MongoDB
        db.ordersEWithLineitems.aggregate([
          { $match: { "o_lineitems.l_quantity": { $gt: 5 } } },
          { $project: { o_orderdate: 1, "o_lineitems.l_partkey": 1 } }
        ])
        ```
        */
        public static async Task<List<BsonDocument>> R1()
        {
            var result = await DB.Collection<OrdersEWithLineitems>()
                .Aggregate()
                .Match(Builders<OrdersEWithLineitems>.Filter.Gt("o_lineitems.l_quantity", 5))
                .Project<BsonDocument>(new BsonDocument
                {
                    {"o_orderdate", 1},
                    {"o_lineitems.l_partkey", 1}
                })
                .ToListAsync();

            return result;
        }

        /*
        ### R2) Embedded Orders with Lineitems Query — Indexed Field

        Test performance of fetching nested documents (1:N relationship embedded) on indexed field.
        ```MongoDB
        db.ordersEWithLineitems.aggregate([
          { $match: { "o_lineitems.l_partkey": { $gt: 20000 } } },
          { $project: { o_orderdate: 1, "o_lineitems.l_partkey": 1 } }
        ])
        ```
        */
        public static async Task<List<BsonDocument>> R2()
        {
            var result = await DB.Collection<OrdersEWithLineitems>()
                .Aggregate()
                .Match(Builders<OrdersEWithLineitems>.Filter.Gt("o_lineitems.l_partkey", 20000))
                .Project<BsonDocument>(new BsonDocument
                {
                    {"o_orderdate", 1},
                    {"o_lineitems.l_partkey", 1}
                })
                .ToListAsync();

            return result;
        }

        /*
        ### R3) Array Tags Query — Find Orders by Tag

        Test array indexing and filtering. Finds orders whose o_lineitems_tags array contains the value "MAIL".
        ```MongoDB
        db.ordersEWithLineitemsArrayAsTags.find(
          { o_lineitems_tags: "MAIL" },
          { o_orderdate: 1, o_lineitems_tags: 1 }
        )
        ```
        */
        public static async Task<List<OrdersEWithLineitemsArrayAsTags>> R3()
        {
            var result = await DB.Collection<OrdersEWithLineitemsArrayAsTags>()
                .Find(Builders<OrdersEWithLineitemsArrayAsTags>.Filter.Eq("o_lineitems_tags", "MAIL"))
                .Project<OrdersEWithLineitemsArrayAsTags>(
                    Builders<OrdersEWithLineitemsArrayAsTags>.Projection
                        .Include("o_orderdate")
                        .Include("o_lineitems_tags"))
                .ToListAsync();

            return result;
        }

        /*
        ### R4) Indexed Array Tags Query — Find Orders by Tag

        Test array indexing and filtering on an indexed field. Finds orders whose o_lineitems_tags_indexed array contains the value "MAIL".
        ```MongoDB
        db.ordersEWithLineitemsArrayAsTagsIndexed.find(
          { o_lineitems_tags_indexed: "MAIL" },
          { o_orderdate: 1, o_lineitems_tags_indexed: 1 }
        )
        ```
        */
        public static async Task<List<OrdersEWithLineitemsArrayAsTagsIndexed>> R4()
        {
            var result = await DB.Collection<OrdersEWithLineitemsArrayAsTagsIndexed>()
                .Find(Builders<OrdersEWithLineitemsArrayAsTagsIndexed>.Filter.Eq("o_lineitems_tags_indexed", "MAIL"))
                .Project<OrdersEWithLineitemsArrayAsTagsIndexed>(
                    Builders<OrdersEWithLineitemsArrayAsTagsIndexed>.Projection
                        .Include("o_orderdate")
                        .Include("o_lineitems_tags_indexed"))
                .ToListAsync();

            return result;
        }
    }
}
