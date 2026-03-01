using MongoDB.Bson;
using MongoDB.Driver;
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
        public static async Task<List<LineitemR>> A1Async()
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
                    x => x.o_orderdate.DateTime.Year.ToString() + "-" + x.o_orderdate.DateTime.Month.ToString(),//.ToString("yyyy-MM")
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
            /* ALTERNATIVE SOLUTION USING INTERMEDIATE-CLASSES
             * NOT TESTED, BUT SHOULD WORK WITH SMALL CHANGES
             * public class Order : Entity
            {
                public int o_custkey { get; set; }
                public DateTime o_orderdate { get; set; }
                public decimal o_totalprice { get; set; }
            }

            public class CustomerWithOrders : CustomerR
            {
                public Order Orders { get; set; }
            }

            public class CustomerOrderResult
            {
                public string c_name { get; set; }
                public DateTime o_orderdate { get; set; }
                public decimal o_totalprice { get; set; }
            }
                         * 
                         * var result = await DB.Fluent<CustomerR>()
                .Lookup<Order, CustomerWithOrders>(
                    DB.Collection<Order>(),
                    c => c.c_custkey,
                    o => o.o_custkey,
                    x => x.Orders)
                .Unwind(x => x.Orders)
                .Project<CustomerOrderResult>(x => new CustomerOrderResult
                {
                    c_name = x.c_name,
                    o_orderdate = x.Orders.o_orderdate,
                    o_totalprice = x.Orders.o_totalprice
                })
                .ToListAsync();*/

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
            var supplierPipeline = DB.Collection<SupplierR>();

            var result = await DB.Fluent<CustomerR>()
                .Project(c => new
                {
                    nationkey = c.c_nationkey
                })
                //.UnionWith(
                //    supplierPipeline
               // )
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
