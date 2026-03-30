using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Linq;
using MongoDB.Entities;
using MongoDBEntities.Models;
using MongoDBEntities.Models.TPC_H;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Reflection.Metadata;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace MongoDBEntities.Benchmarks
{
    public class QueriesRMongoDBEntities
    {
        /*
        A1) Non-Indexed Columns          
        This query selects all records from the lineitem table
        ```sql
            SELECT * FROM lineitem;
        ```
         */
        public static async Task<List<LineitemR>> A1()
        {
            var a1 = await DB.Find<LineitemR>()
                .ExecuteAsync();

            return a1;
        }

        /*
        A2) Non-Indexed Columns — Range Query
         
        This query selects all records from the orders table where the order date is between '1996-01-01' and '1996-12-31'
        ```sql
        SELECT * FROM orders
        WHERE o_orderdate
            BETWEEN '1996-01-01' AND '1996-12-31';
        ```
        */
        public static async Task<List<OrdersR>> A2()
        {


            var a2 = await DB.Find<OrdersR>()
                .Match(
                    o => o.o_orderdate.DateTime > DateTime.Parse("1996-01-01")
                    && o.o_orderdate.DateTime < DateTime.Parse("1996-12-31") 
                )
                .ExecuteAsync();

            return a2;        
        }


        /*
         ### A3) Indexed Columns

        This query selects all records from the customer table
        ```sql
        SELECT * FROM customer;
        ```
         */
        public static async Task<List<CustomerR>> A3()
        {
            var a3 = await DB.Find<CustomerR>()
                .ExecuteAsync();

            return a3;
        }

        /*
         ### A4) Indexed Columns — Range Query

        This query selects all records from the orders table where the order key is between 1000 and 50000
        ```sql
        SELECT * FROM orders
        WHERE o_orderkey BETWEEN 1000 AND 50000;
        ```
         */
        public static async Task<List<OrdersR>> A4()
        {


            var a4 = await DB.Find<OrdersR>()
                .Match(
                    o => o.o_orderkey > 1000
                    && o.o_orderkey < 50_000
                )
                .ExecuteAsync();

            return a4;
        }

        /**
        ### B1) COUNT
        
        This query counts the number of orders grouped by order month
        ```sql
        SELECT COUNT(o.o_orderkey) AS order_count,
               DATE_FORMAT(o.o_orderdate, '%Y-%m') AS order_month
        FROM orders o
        GROUP BY order_month;
        ```
        */
        public static async Task<List<BsonDocument>> B1()
        {
            var b1 = await DB.Fluent<OrdersR>()
                .Group(
                    x => x.o_orderdate.DateTime.ToString("%Y-%m"),
                    g => new
                    {
                        OrderMonth = g.Key,
                        OrderCount = g.Count()
                    }
                )
                .Project(x => new BsonDocument
                {
                    { "OrderMonth", x.OrderMonth },
                    { "OrderCount", x.OrderCount }
                })
                .ToListAsync();

            return b1;
        }

        /*
        ### B2) MAX

        This query finds the maximum extended price from the lineitem table grouped by ship month
        ```sql
        SELECT DATE_FORMAT(l.l_shipdate, '%Y-%m') AS ship_month,
               MAX(l.l_extendedprice) AS max_price
        FROM lineitem l
        GROUP BY ship_month;
        ```
        */
        public static async Task<List<BsonDocument>> B2()
        {
            var b2 = await DB.Fluent<LineitemR>()
                .Group(
                    x => x.l_shipdate.DateTime.ToString("%Y-%m"),
                    g => new
                    {
                        ShipMonth = g.Key,
                        MaxPrice = g.Max(x => x.l_extendedprice)
                    }
                )
                .Project(x => new BsonDocument
                {
            { "ShipMonth", x.ShipMonth },
            { "MaxPrice", x.MaxPrice }
                })
                .ToListAsync();

            return b2;
        }

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

            var result = await DB.Fluent<CustomerR>()
                .Lookup(
                    foreignCollectionName: "OrdersR",
                    localField: "c_custkey",
                    foreignField: "o_custkey",
                    @as: "orders")
                .Unwind("orders")
                .Project<BsonDocument>(new BsonDocument
                {
                    { "_id", 0 },
                    { "c_name", 1 },
                    { "o_orderdate", "$orders.o_orderdate" },
                    { "o_totalprice", "$orders.o_totalprice" }
                })
                .ToListAsync();
            
            return result;
        }

        /*
        ### D1) UNION
        
        This query combines customer and supplier nation keys
        ```sql
        (SELECT c_nationkey FROM customer)
        UNION
        (SELECT s_nationkey FROM supplier);
        ```
        */
        public static async Task<List<BsonDocument>> D1()
        {

            var result = await DB.Fluent<CustomerR>()
                .Project(c => new
                {
                    nationkey = c.c_nationkey
                })

                .Group(x => x.nationkey, g => new
                {
                    nationkey = g.Key
                })
                .Project(x => new BsonDocument
                {
                    { "nationkey", x.nationkey }
                })
                .ToListAsync();

            return result;

        }

    }
}
