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

        // ## C) Joins

        /*
        ### C1) Non-Indexed Columns

        This query gives customer names, order dates, and total prices for customers
        ```sql
        SELECT c.c_name, o.o_orderdate, o.o_totalprice
        FROM customer c, orders o;
        ```
        */
        public static async Task<List<BsonDocument>> C1()
        {
            var ordersCollection = DB.Collection<OrdersR>();

            var result = await DB.Fluent<CustomerR>()
                .Lookup<OrdersR, OrdersR, OrdersR[], BsonDocument>(
                    foreignCollection: ordersCollection,
                    let: null,//new BsonDocument(), // no variables needed
                    lookupPipeline: new EmptyPipelineDefinition<OrdersR>(),
                    @as: new StringFieldDefinition<BsonDocument, OrdersR[] >("orders")
                )
                .Unwind("orders")
                .Limit(1_500_000)
                .Project(new BsonDocument
                {
            { "_id", 0 },
            { "c_name", "$c_name" },
            { "o_orderdate", "$orders.o_orderdate" },
            { "o_totalprice", "$orders.o_totalprice" }
                })
                .ToListAsync();

            return result;
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
                    localField: "_id",
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
        ### C3) Complex Join 1

        This query gives customer names, nation names, order dates, and total prices for customers
        ```sql
        SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice
        FROM customer c
        JOIN nation n ON c.c_nationkey = n.n_nationkey
        JOIN orders o ON c.c_custkey = o.o_custkey;
        ```
        */
        public static async Task<List<BsonDocument>> C3()
        {
            var result = await DB.Fluent<CustomerR>()

                // JOIN nation
                .Lookup(
                    foreignCollectionName: "NationR",
                    localField: "c_nationkey",
                    foreignField: "_id",//n_nationkey
                    @as: "nation"
                )
                .Unwind("nation")

                // JOIN orders
                .Lookup(
                    foreignCollectionName: "OrdersR",
                    localField: "_id",//c_custkey
                    foreignField: "o_custkey",
                    @as: "orders"
                )
                .Unwind("orders")

                // SELECT projection
                .Project<BsonDocument>(new BsonDocument
                {
            { "_id", 0 },
            { "c_name", 1 },
            { "n_name", "$nation.n_name" },
            { "o_orderdate", "$orders.o_orderdate" },
            { "o_totalprice", "$orders.o_totalprice" }
                })
                .ToListAsync();

            return result;
        }


        /*
        ### C4) Complex Join 2

        This query gives customer names, nation names, region names, order dates, and total prices for customers
        ```sql
        SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice
        FROM customer c
        JOIN nation n ON c.c_nationkey = n.n_nationkey
        JOIN region r ON n.n_regionkey = r.r_regionkey
        JOIN orders o ON c.c_custkey = o.o_custkey;
        ```
        */
        public static async Task<List<BsonDocument>> C4()
        {
            var result = await DB.Fluent<CustomerR>()

                // JOIN nation
                .Lookup(
                    foreignCollectionName: "NationR",
                    localField: "c_nationkey",
                    foreignField: "_id",
                    @as: "nation"
                )
                .Unwind("nation")

                // JOIN region (via nation)
                .Lookup(
                    foreignCollectionName: "RegionR",
                    localField: "nation.n_regionkey",
                    foreignField: "_id",
                    @as: "region"
                )
                .Unwind("region")

                // JOIN orders
                .Lookup(
                    foreignCollectionName: "OrdersR",
                    localField: "_id",
                    foreignField: "o_custkey",
                    @as: "orders"
                )
                .Unwind("orders")

                // SELECT projection
                .Project<BsonDocument>(new BsonDocument
                {
            { "_id", 0 },
            { "c_name", 1 },
            { "n_name", "$nation.n_name" },
            { "r_name", "$region.r_name" },
            { "o_orderdate", "$orders.o_orderdate" },
            { "o_totalprice", "$orders.o_totalprice" }
                })
                .ToListAsync();

            return result;
        }


        /*
        ### C5) Left Outer Join

        This query gives customer names and order dates for all customers, including those without orders
        ```sql
        SELECT c.c_custkey, c.c_name, o.o_orderkey, o.o_orderdate
        FROM customer c
        LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey;
        ```
        */
        public static async Task<List<BsonDocument>> C5()
        {
            var result = await DB.Fluent<CustomerR>()

                // LEFT JOIN orders
                .Lookup(
                    foreignCollectionName: "OrdersR",
                    localField: "_id",              // c_custkey
                    foreignField: "o_custkey",
                    @as: "orders"
                )

                // IMPORTANT: preserve nulls for LEFT JOIN semantics
                .Unwind("orders", new AggregateUnwindOptions<BsonDocument>
                {
                    PreserveNullAndEmptyArrays = true
                })

                // SELECT projection
                .Project<BsonDocument>(new BsonDocument
                {
            { "_id", 0 },
            { "c_custkey", "$_id" },
            { "c_name", 1 },
            { "o_orderkey", "$orders._id" },
            { "o_orderdate", "$orders.o_orderdate" }
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


        /*
        ### D2) INTERSECT

        This query finds common customer and supplier keys
        MySQL doesn't directly support INTERSECT, so I used IN
        ```sql
        SELECT DISTINCT c.c_custkey
        FROM customer c
        WHERE c.c_custkey IN (
            SELECT s.s_suppkey
            FROM supplier s
        );
        ```
        */
        public static async Task<List<BsonDocument>> D2()
        {
            var result = await DB.Fluent<CustomerR>()

                // JOIN supplier on matching _id
                .Lookup(
                    foreignCollectionName: "SupplierR",
                    localField: "_id",
                    foreignField: "_id",
                    @as: "matched_suppliers"
                )

                // keep only those that have at least one match
                .Match(new BsonDocument
                {
                    { "matched_suppliers.0", new BsonDocument { { "$exists", true } } }
                })

                // SELECT DISTINCT c_custkey
                .Project<BsonDocument>(new BsonDocument
                {
                    { "_id", 0 },
                    { "c_custkey", "$_id" }
                })

                .ToListAsync();

            return result;
        }



        /*
        ### D3) DIFFERENCE

        This query finds customer keys that are not in the supplier table
        MySQL doesn't directly support EXCEPT/MINUS, so I used NOT IN
        ```sql
        SELECT DISTINCT c.c_custkey
        FROM customer c
        WHERE c.c_custkey NOT IN (
            SELECT DISTINCT s.s_suppkey
            FROM supplier s
        );
        ```
        */
        public static async Task<List<BsonDocument>> D3()
        {
            var result = await DB.Fluent<CustomerR>()

                // LEFT JOIN supplier on _id
                .Lookup(
                    foreignCollectionName: "SupplierR",
                    localField: "_id",
                    foreignField: "_id",
                    @as: "matched_suppliers"
                )

                // keep only those with NO matches
                .Match(new BsonDocument
                {
            { "matched_suppliers", new BsonDocument { { "$size", 0 } } }
                })

                // SELECT DISTINCT c_custkey
                .Project<BsonDocument>(new BsonDocument
                {
            { "_id", 0 },
            { "c_custkey", "$_id" }
                })

                .ToListAsync();

            return result;
        }

        // ## E) Result Modification

        /*
        ### E1) Non-Indexed Columns Sorting

        This query sorts customer names, addresses, and account balances in descending order of account balance
        ```sql
        SELECT c_name, c_address, c_acctbal
        FROM customer
        ORDER BY c_acctbal DESC
        ```
        */
        public static async Task<List<BsonDocument>> E1()
        {
            var result = await DB.Fluent<CustomerR>()

                // SORT
                .Sort(Builders<CustomerR>.Sort.Descending(c => c.c_acctbal))

                // SELECT projection
                .Project<BsonDocument>(new BsonDocument
                {
                    { "_id", 0 },
                    { "c_name", 1 },
                    { "c_address", 1 },
                    { "c_acctbal", 1 }
                })

                .ToListAsync();

            return result;
        }

        /*

        ### E2) Indexed Columns Sorting

        This query sorts order keys, customer keys, order dates, and total prices in ascending order of order key
        ```sql
        SELECT o_orderkey, o_custkey, o_orderdate, o_totalprice
        FROM orders
        ORDER BY o_orderkey
        ```
        */
        public static async Task<List<BsonDocument>> E2()
        {
            var result = await DB.Fluent<OrdersR>()

                // SORT ascending
                .Sort(Builders<OrdersR>.Sort.Ascending(o => o.o_orderkey))

                // SELECT projection
                .Project<BsonDocument>(new BsonDocument
                {
                    { "_id", 0 },
                    { "o_orderkey", 1 },
                    { "o_custkey", 1 },
                    { "o_orderdate", 1 },
                    { "o_totalprice", 1 }
                })

                .ToListAsync();

            return result;
        }


        /*
        ### E3) Distinct

        This query selects distinct nation keys and market segments from the customer table
        ```sql
        SELECT DISTINCT c_nationkey, c_mktsegment
        FROM customer;
        ```
        */
        public static async Task<List<BsonDocument>> E3()
        {
            var result = await DB.Fluent<CustomerR>()

                // GROUP by the two fields to emulate DISTINCT
                .Group(
                    x => new { x.c_nationkey, x.c_mktsegment },
                    g => new
                    {
                        c_nationkey = g.Key.c_nationkey,
                        c_mktsegment = g.Key.c_mktsegment
                    }
                )

                // Project to clean BsonDocument
                .Project(x => new BsonDocument
                {
                    { "c_nationkey", x.c_nationkey },
                    { "c_mktsegment", x.c_mktsegment }
                })
                .ToListAsync();

            return result;
        }



        /*
        ### Q1) Pricing Summary Report Query

        //This query reports the amount of business that was billed, shipped, and returned
        ```sql
        SELECT
          l_returnflag,
          l_linestatus,
          SUM(l_quantity) AS sum_qty,
          SUM(l_extendedprice) AS sum_base_price,
          SUM(l_extendedprice * (1 - l_discount)) AS sum_disc_price,
          SUM(l_extendedprice * (1 - l_discount) * (1 + l_tax)) AS sum_charge,
          AVG(l_quantity) AS avg_qty,
          AVG(l_extendedprice) AS avg_price,
          AVG(l_discount) AS avg_disc,
          COUNT(*) AS count_order
        FROM lineitem
        WHERE l_shipdate <= DATE_SUB('1998-12-01', INTERVAL 90 DAY)
        GROUP BY l_returnflag, l_linestatus
        ORDER BY l_returnflag, l_linestatus
        ```
        */
        public static async Task<List<BsonDocument>> Q1()
        {
            // Compute the cutoff date: 1998-12-01 - 90 days
            var cutoffDate = new DateTime(1998, 12, 1).Subtract(TimeSpan.FromDays(90));

            var result = await DB.Fluent<LineitemR>()

                // Filter: l_shipdate <= cutoffDate
                .Match(l => l.l_shipdate.DateTime <= cutoffDate)

                // Group by l_returnflag, l_linestatus
                .Group(
                    x => new { x.l_returnflag, x.l_linestatus },
                    g => new
                    {
                        l_returnflag = g.Key.l_returnflag,
                        l_linestatus = g.Key.l_linestatus,
                        sum_qty = g.Sum(x => x.l_quantity),
                        sum_base_price = g.Sum(x => x.l_extendedprice),
                        sum_disc_price = g.Sum(x => x.l_extendedprice * (1 - x.l_discount)),
                        sum_charge = g.Sum(x => x.l_extendedprice * (1 - x.l_discount) * (1 + x.l_tax)),
                        avg_qty = g.Average(x => x.l_quantity),
                        avg_price = g.Average(x => x.l_extendedprice),
                        avg_disc = g.Average(x => x.l_discount),
                        count_order = g.Count()
                    }
                )      

                // Project to BsonDocument
                .Project(x => new BsonDocument
                {
                    { "l_returnflag", x.l_returnflag },
                    { "l_linestatus", x.l_linestatus },
                    { "sum_qty", x.sum_qty },
                    { "sum_base_price", x.sum_base_price },
                    { "sum_disc_price", x.sum_disc_price },
                    { "sum_charge", x.sum_charge },
                    { "avg_qty", x.avg_qty },
                    { "avg_price", x.avg_price },
                    { "avg_disc", x.avg_disc },
                    { "count_order", x.count_order }
                })
                // Sort by l_returnflag, l_linestatus
                .Sort(Builders<BsonDocument>.Sort
                    .Ascending("l_returnflag")
                    .Ascending("l_linestatus")
                   )
                .ToListAsync();

            return result;
        }

        /*
         ### Q2) Minimum Cost Supplier Query

        //This query finds which supplier should be selected to place an order for a given part in a given region
        ```sql
        SELECT
          s.s_acctbal,
          s.s_name,
          n.n_name,
          p.p_partkey,
          p.p_mfgr,
          s.s_address,
          s.s_phone,
          s.s_comment
        FROM
          part p,
          supplier s,
          partsupp ps,
          nation n,
          region r
        WHERE
          p.p_partkey = ps.ps_partkey
          AND s.s_suppkey = ps.ps_suppkey
          AND p.p_size = 15
          AND p.p_type LIKE '%BRASS'
          AND s.s_nationkey = n.n_nationkey
          AND n.n_regionkey = r.r_regionkey
          AND r.r_name = 'EUROPE'
          AND ps.ps_supplycost = (
            SELECT MIN(ps.ps_supplycost)
            FROM
              partsupp ps,
              supplier s,
              nation n,
              region r
            WHERE
              p.p_partkey = ps.ps_partkey
              AND s.s_suppkey = ps.ps_suppkey
              AND s.s_nationkey = n.n_nationkey
              AND n.n_regionkey = r.r_regionkey
              AND r.r_name = 'EUROPE'
          )
        ORDER BY
          s.s_acctbal DESC,
          n.n_name,
          s.s_name,
          p.p_partkey
        ```
         */
        public static async Task<List<BsonDocument>> Q2_From_RegionR()
        {
            var result = await DB.Fluent<RegionR>()
                // Filter by region
                .Match(r => r.r_name == "EUROPE")

                // JOIN nation
                .Lookup(
                    foreignCollectionName: "NationR",
                    localField: "_id",          // region._id = n_regionkey
                    foreignField: "n_regionkey",
                    @as: "nations"
                )
                .Unwind("nations")
                

                // JOIN supplier
                .Lookup(
                    foreignCollectionName: "SupplierR",
                    localField: "nations._id",   // n_nationkey
                    foreignField: "s_nationkey",
                    @as: "suppliers"
                )
                .Unwind("suppliers")
                
                // JOIN partsupp
                .Lookup(
                    foreignCollectionName: "PartsuppR",
                    localField: "suppliers._id", // s_suppkey
                    foreignField: "ps_suppkey",
                    @as: "partsupps"
                )
                .Unwind("partsupps")
                
                // JOIN part
                .Lookup(
                    foreignCollectionName: "PartR",
                    localField: "partsupps.ps_partkey",
                    foreignField: "_id",          // p_partkey
                    @as: "parts"
                )
                .Unwind("parts")
                
                // Filter part by size and type
                .Match(new BsonDocument
                {
                    { "parts.p_size", 15 },
                    { "parts.p_type", new BsonDocument { { "$regex", "BRASS$" }, { "$options", "i" } } }
                })
                
                // Compute minimum supply cost per part
                .Group(new BsonDocument
                {
                    { "_id", "$parts._id" }, // group by part key
                    { "min_supplycost", new BsonDocument { { "$min", "$partsupps.ps_supplycost" } } },
                    { "records", new BsonDocument { { "$push", "$$ROOT" } } }
                })
                
                // Flatten records where supply cost = min_supplycost
                .Unwind("records")
                //$match: {
                //      $expr: { 
                //        $eq: ["$min_supplycost", "$records.partsupps.ps_supplycost"] 
                //      }
                //}
                .Match(new BsonDocument
                {
                    { "$expr", new BsonDocument { { "$eq", new BsonArray {"$min_supplycost", "$records.partsupps.ps_supplycost"} } } }
                    //{ "records.partsupps.ps_supplycost", new BsonDocument { { "$eq", "$$min_supplycost" } } } //
                })
                
                // Project final output
                .Project(new BsonDocument
                {
            { "_id", 0 },
            { "s_acctbal", "$records.suppliers.s_acctbal" },
            { "s_name", "$records.suppliers.s_name" },
            { "n_name", "$records.nations.n_name" },
            { "p_partkey", "$records.parts._id" },
            { "p_mfgr", "$records.parts.p_mfgr" },
            { "s_address", "$records.suppliers.s_address" },
            { "s_phone", "$records.suppliers.s_phone" },
            { "s_comment", "$records.suppliers.s_comment" }
                })

                // Sort by SQL order
                .Sort(Builders<BsonDocument>.Sort
                    .Descending("s_acctbal")
                    .Ascending("n_name")
                    .Ascending("s_name")
                    .Ascending("p_partkey")
                )
                .ToListAsync();

            return result;
        }


        /*
         ### Q2) Minimum Cost Supplier Query

        //This query finds which supplier should be selected to place an order for a given part in a given region
        ```sql
        SELECT
          s.s_acctbal,
          s.s_name,
          n.n_name,
          p.p_partkey,
          p.p_mfgr,
          s.s_address,
          s.s_phone,
          s.s_comment
        FROM
          part p,
          supplier s,
          partsupp ps,
          nation n,
          region r
        WHERE
          p.p_partkey = ps.ps_partkey
          AND s.s_suppkey = ps.ps_suppkey
          AND p.p_size = 15
          AND p.p_type LIKE '%BRASS'
          AND s.s_nationkey = n.n_nationkey
          AND n.n_regionkey = r.r_regionkey
          AND r.r_name = 'EUROPE'
          AND ps.ps_supplycost = (
            SELECT MIN(ps.ps_supplycost)
            FROM
              partsupp ps,
              supplier s,
              nation n,
              region r
            WHERE
              p.p_partkey = ps.ps_partkey
              AND s.s_suppkey = ps.ps_suppkey
              AND s.s_nationkey = n.n_nationkey
              AND n.n_regionkey = r.r_regionkey
              AND r.r_name = 'EUROPE'
          )
        ORDER BY
          s.s_acctbal DESC,
          n.n_name,
          s.s_name,
          p.p_partkey
        ```
         */
        public static async Task<List<BsonDocument>> Q2()
        {
            var result = await DB.Fluent<PartR>()

                // Filter parts
                .Match(new BsonDocument
                {
            { "p_size", 15 },
            { "p_type", new BsonDocument { { "$regex", "BRASS$" }, { "$options", "i" } } }
                })

                // JOIN partsupp
                .Lookup(
                    foreignCollectionName: "PartsuppR",
                    localField: "_id",              // p_partkey
                    foreignField: "ps_partkey",
                    @as: "partsupp"
                )
                .Unwind("partsupp")

                // JOIN supplier
                .Lookup(
                    foreignCollectionName: "SupplierR",
                    localField: "partsupp.ps_suppkey",
                    foreignField: "_id",
                    @as: "supplier"
                )
                .Unwind("supplier")

                // JOIN nation
                .Lookup(
                    foreignCollectionName: "NationR",
                    localField: "supplier.s_nationkey",
                    foreignField: "_id",
                    @as: "nation"
                )
                .Unwind("nation")

                // JOIN region
                .Lookup(
                    foreignCollectionName: "RegionR",
                    localField: "nation.n_regionkey",
                    foreignField: "_id",
                    @as: "region"
                )
                .Unwind("region")

                // Filter region
                .Match(new BsonDocument
                {
                    { "region.r_name", "EUROPE" }
                })

                // GROUP: compute min supply cost per part
                .Group(new BsonDocument
                {
                    { "_id", "$_id" }, // part key
                    { "min_supplycost", new BsonDocument { { "$min", "$partsupp.ps_supplycost" } } },
                    { "docs", new BsonDocument { { "$push", "$$ROOT" } } }
                })

                // Filter only suppliers with min cost
                .Project(new BsonDocument
                {
                    { "docs", new BsonDocument
                        {
                            {
                                "$filter", new BsonDocument
                                {
                                    { "input", "$docs" },
                                    { "as", "doc" },
                                    { "cond", new BsonDocument
                                        {
                                            { "$eq", new BsonArray
                                                {
                                                    "$$doc.partsupp.ps_supplycost",
                                                    "$min_supplycost"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
                .Unwind("docs")

                // Final projection
                .Project(new BsonDocument
                {
                    { "_id", 0 },
                    { "s_acctbal", "$docs.supplier.s_acctbal" },
                    { "s_name", "$docs.supplier.s_name" },
                    { "n_name", "$docs.nation.n_name" },
                    { "p_partkey", "$docs._id" },
                    { "p_mfgr", "$docs.p_mfgr" },
                    { "s_address", "$docs.supplier.s_address" },
                    { "s_phone", "$docs.supplier.s_phone" },
                    { "s_comment", "$docs.supplier.s_comment" }
                })

                // Sort
                .Sort(Builders<BsonDocument>.Sort
                    .Descending("s_acctbal")
                    .Ascending("n_name")
                    .Ascending("s_name")
                    .Ascending("p_partkey")
                )

                .ToListAsync();

            return result;
        }


        /*
         ### Q3) Shipping Priority Query

        //This query retrieves the 10 unshipped orders with the highest value
        ```sql
        SELECT
          l.l_orderkey,
          SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue,
          o.o_orderdate,
          o.o_shippriority
        FROM
          customer c,
          orders o,
          lineitem l
        WHERE
          c.c_mktsegment = 'BUILDING'
          AND c.c_custkey = o.o_custkey
          AND l.l_orderkey = o.o_orderkey
          AND o.o_orderdate < '1995-03-15'
          AND l.l_shipdate > '1995-03-15'
        GROUP BY
          l.l_orderkey,
          o.o_orderdate,
          o.o_shippriority
        ORDER BY
          revenue DESC,
          o.o_orderdate
        ```
         */
        public static async Task<List<BsonDocument>> Q3()
        {
            var date = new DateTime(1995, 3, 15);

            var result = await DB.Fluent<CustomerR>()

                // Filter customers
                .Match(c => c.c_mktsegment == "BUILDING")

                // JOIN orders
                .Lookup(
                    foreignCollectionName: "OrdersR",
                    localField: "_id",              // c_custkey
                    foreignField: "o_custkey",
                    @as: "orders"
                )
                .Unwind("orders")

                // Filter orders
                .Match(new BsonDocument
                {
                    { "orders.o_orderdate.DateTime", new BsonDocument { { "$lt", date } } }
                })
                
                // JOIN lineitem
                .Lookup(
                    foreignCollectionName: "LineitemR",
                    localField: "orders._id",       // o_orderkey
                    foreignField: "l_orderkey",
                    @as: "lineitems"
                )
                .Unwind("lineitems")

                
                // Filter lineitems
                .Match(new BsonDocument
                {
                    { "lineitems.l_shipdate.DateTime", new BsonDocument { { "$gt", date } } }
                })
                
                // GROUP
                .Group(new BsonDocument
                {
                    {
                        "_id", new BsonDocument
                        {
                            { "l_orderkey", "$lineitems.l_orderkey" },
                            { "o_orderdate", "$orders.o_orderdate.DateTime" },
                            { "o_shippriority", "$orders.o_shippriority" }
                        }
                    },
                    {
                        "revenue",
                        new BsonDocument
                        {
                            {
                                "$sum",
                                new BsonDocument
                                {
                                    {
                                        "$multiply",
                                        new BsonArray
                                        {
                                            "$lineitems.l_extendedprice",
                                            new BsonDocument
                                            {
                                                { "$subtract", new BsonArray { 1, "$lineitems.l_discount" } }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
                
                // Project final shape
                .Project(new BsonDocument
                {
                    { "_id", 0 },
                    { "l_orderkey", "$_id.l_orderkey" },
                    { "revenue", 1 },
                    { "o_orderdate", "$_id.o_orderdate" },
                    { "o_shippriority", "$_id.o_shippriority" }
                })
                
                // Sort
                .Sort(Builders<BsonDocument>.Sort
                    .Descending("revenue")
                    .Ascending("o_orderdate")
                )
                
                // Limit (TPC-H spec says 10)
                
                .Limit(10)
                
                .ToListAsync();

            return result;
        }


        /*
        ### Q4) Order Priority Checking Query

        //This query determines how well the order priority system is working and gives an assessment of customer satisfaction
        ```sql
        SELECT
          o_orderpriority,
          COUNT(*) AS order_count
        FROM
          orders
        WHERE
          o_orderdate >= '1993-07-01'
          AND o_orderdate < DATE_ADD('1993-07-01', INTERVAL 3 MONTH)
          AND EXISTS (
            SELECT *
            FROM
              lineitem
            WHERE
              l_orderkey = o_orderkey
              AND l_commitdate < l_receiptdate
          )
        GROUP BY
          o_orderpriority
        ORDER BY
          o_orderpriority
        ```*/
        public static async Task<List<BsonDocument>> Q4()
        {
            var startDate = new DateTime(1993, 7, 1);
            var endDate = startDate.AddMonths(3); // DATE_ADD(..., INTERVAL 3 MONTH)

            var result = await DB.Fluent<OrdersR>()

                // Filter orders by date range
                .Match(new BsonDocument
                {
                    { "o_orderdate.DateTime", new BsonDocument
                        {
                            { "$gte", startDate },
                            { "$lt", endDate }
                        }
                    }
                })

                // LOOKUP lineitems
                .Lookup(
                    foreignCollectionName: "LineitemR",
                    localField: "_id",           // o_orderkey
                    foreignField: "l_orderkey",
                    @as: "lineitems"
                )

                // Keep only matching lineitems (commitdate < receiptdate)
                .Project(new BsonDocument
                {
                    { "o_orderpriority", 1 },
                    {
                        "lineitems",
                        new BsonDocument
                        {
                            {
                                "$filter",
                                new BsonDocument
                                {
                                    { "input", "$lineitems" },
                                    { "as", "li" },
                                    { "cond",
                                        new BsonDocument
                                        {
                                            {
                                                "$lt",
                                                new BsonArray
                                                {
                                                    "$$li.l_commitdate.DateTime",
                                                    "$$li.l_receiptdate.DateTime"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                })

                // EXISTS → keep only orders where filtered lineitems is not empty
                .Match(new BsonDocument
                {
                    { "lineitems.0", new BsonDocument { { "$exists", true } } }
                })

                // GROUP BY o_orderpriority
                .Group(new BsonDocument
                {
                    { "_id", "$o_orderpriority" },
                    { "order_count", new BsonDocument { { "$sum", 1 } } }
                })

                // Final projection
                .Project(new BsonDocument
                {
                    { "_id", 0 },
                    { "o_orderpriority", "$_id" },
                    { "order_count", 1 }
                })

                // ORDER BY o_orderpriority
                .Sort(Builders<BsonDocument>.Sort.Ascending("o_orderpriority"))

                .ToListAsync();

            return result;
        }

        /*### Q5) Local Supplier Volume Query

        //This query lists the revenue volume done through local suppliers
        ```sql
        SELECT
          n.n_name,
          SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue
        FROM
          customer c,
          orders o,
          lineitem l,
          supplier s,
          nation n,
          region r
        WHERE
          c.c_custkey = o.o_custkey
          AND l.l_orderkey = o.o_orderkey
          AND l.l_suppkey = s.s_suppkey
          AND c.c_nationkey = s.s_nationkey
          AND s.s_nationkey = n.n_nationkey
          AND n.n_regionkey = r.r_regionkey
          AND r.r_name = 'ASIA'
          AND o.o_orderdate >= '1994-01-01'
          AND o.o_orderdate < DATE_ADD('1994-01-01', INTERVAL 1 YEAR)
        GROUP BY
          n.n_name
        ORDER BY
          revenue DESC
        ```*/
        public static async Task<List<BsonDocument>> Q5()
        {
            var startDate = new DateTime(1994, 1, 1);
            var endDate = startDate.AddYears(1); // DATE_ADD(..., INTERVAL 1 YEAR)

            var result = await DB.Fluent<CustomerR>()

                // JOIN orders
                .Lookup(
                    foreignCollectionName: "OrdersR",
                    localField: "_id",              // c_custkey
                    foreignField: "o_custkey",
                    @as: "orders"
                )
                .Unwind("orders")

                // Filter orders by date
                .Match(new BsonDocument
                {
                    { "orders.o_orderdate.DateTime", new BsonDocument
                        {
                            { "$gte", startDate },
                            { "$lt", endDate }
                        }
                    }
                })

                // JOIN lineitem
                .Lookup(
                    foreignCollectionName: "LineitemR",
                    localField: "orders._id",       // o_orderkey
                    foreignField: "l_orderkey",
                    @as: "lineitems"
                )
                .Unwind("lineitems")

                // JOIN supplier
                .Lookup(
                    foreignCollectionName: "SupplierR",
                    localField: "lineitems.l_suppkey",
                    foreignField: "_id",
                    @as: "supplier"
                )
                .Unwind("supplier")

                // Enforce local supplier: c_nationkey = s_nationkey
                .Match(new BsonDocument
                {
                    { "$expr", new BsonDocument
                        {
                            { "$eq", new BsonArray { "$c_nationkey", "$supplier.s_nationkey" } }
                        }
                    }
                })

                // JOIN nation
                .Lookup(
                    foreignCollectionName: "NationR",
                    localField: "supplier.s_nationkey",
                    foreignField: "_id",
                    @as: "nation"
                )
                .Unwind("nation")

                // JOIN region
                .Lookup(
                    foreignCollectionName: "RegionR",
                    localField: "nation.n_regionkey",
                    foreignField: "_id",
                    @as: "region"
                )
                .Unwind("region")

                // Filter region
                .Match(new BsonDocument
                {
                    { "region.r_name", "ASIA" }
                })

                // GROUP BY nation name
                .Group(new BsonDocument
                {
                    { "_id", "$nation.n_name" },
                    {
                        "revenue",
                        new BsonDocument
                        {
                            {
                                "$sum",
                                new BsonDocument
                                {
                                    {
                                        "$multiply",
                                        new BsonArray
                                        {
                                            "$lineitems.l_extendedprice",
                                            new BsonDocument
                                            {
                                                { "$subtract", new BsonArray { 1, "$lineitems.l_discount" } }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                })

                // Final projection
                .Project(new BsonDocument
                {
                    { "_id", 0 },
                    { "n_name", "$_id" },
                    { "revenue", 1 }
                })

                // Sort by revenue DESC
                .Sort(Builders<BsonDocument>.Sort.Descending("revenue"))

                .ToListAsync();

            return result;
        }




    }
}
