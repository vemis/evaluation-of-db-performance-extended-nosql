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
    }
}
