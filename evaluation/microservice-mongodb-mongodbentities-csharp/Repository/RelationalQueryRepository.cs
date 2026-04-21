using MongoDBEntitiesMicroservice.Model.Relational;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Entities;

namespace MongoDBEntitiesMicroservice.Repository
{
    public interface IRelationalQueryRepository
    {
        Task<int> A1();
        Task<int> A2();
        Task<int> A3();
        Task<int> A4();
        Task<int> B1();
        Task<int> B2();
        Task<int> C1();
        Task<int> C2();
        Task<int> C3();
        Task<int> C4();
        Task<int> C5();
        Task<int> D1();
        Task<int> D2();
        Task<int> D3();
        Task<int> E1();
        Task<int> E2();
        Task<int> E3();
        Task<int> Q1();
        Task<int> Q2();
        Task<int> Q3();
        Task<int> Q4();
        Task<int> Q5();
    }

    public class RelationalQueryRepository : IRelationalQueryRepository
    {
        public async Task<int> A1()
        {
            var result = await DB.Find<LineitemR>().ExecuteAsync();
            return result.Count;
        }

        public async Task<int> A2()
        {
            var result = await DB.Find<OrdersR>()
                .Match(o => o.o_orderdate.DateTime > DateTime.Parse("1996-01-01")
                         && o.o_orderdate.DateTime < DateTime.Parse("1996-12-31"))
                .ExecuteAsync();
            return result.Count;
        }

        public async Task<int> A3()
        {
            var result = await DB.Find<CustomerR>().ExecuteAsync();
            return result.Count;
        }

        public async Task<int> A4()
        {
            var result = await DB.Find<OrdersR>()
                .Match(o => o.o_orderkey > 1000 && o.o_orderkey < 50_000)
                .ExecuteAsync();
            return result.Count;
        }

        public async Task<int> B1()
        {
            var result = await DB.Fluent<OrdersR>()
                .Group(x => x.o_orderdate.DateTime.ToString("%Y-%m"),
                    g => new { OrderMonth = g.Key, OrderCount = g.Count() })
                .Project(x => new BsonDocument { { "OrderMonth", x.OrderMonth }, { "OrderCount", x.OrderCount } })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> B2()
        {
            var result = await DB.Fluent<LineitemR>()
                .Group(x => x.l_shipdate.DateTime.ToString("%Y-%m"),
                    g => new { ShipMonth = g.Key, MaxPrice = g.Max(x => x.l_extendedprice) })
                .Project(x => new BsonDocument { { "ShipMonth", x.ShipMonth }, { "MaxPrice", x.MaxPrice } })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> C1()
        {
            var ordersCollection = DB.Collection<OrdersR>();
            var result = await DB.Fluent<CustomerR>()
                .Lookup<OrdersR, OrdersR, OrdersR[], BsonDocument>(
                    foreignCollection: ordersCollection,
                    let: null,
                    lookupPipeline: new EmptyPipelineDefinition<OrdersR>(),
                    @as: new StringFieldDefinition<BsonDocument, OrdersR[]>("orders"))
                .Unwind("orders")
                .Limit(1_500_000)
                .Project(new BsonDocument
                {
                    { "_id", 0 }, { "c_name", "$c_name" },
                    { "o_orderdate", "$orders.o_orderdate" }, { "o_totalprice", "$orders.o_totalprice" }
                })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> C2()
        {
            var result = await DB.Fluent<CustomerR>()
                .Lookup(foreignCollectionName: "OrdersR", localField: "_id",
                    foreignField: "o_custkey", @as: "orders")
                .Unwind("orders")
                .Project<BsonDocument>(new BsonDocument
                {
                    { "_id", 0 }, { "c_name", 1 },
                    { "o_orderdate", "$orders.o_orderdate" }, { "o_totalprice", "$orders.o_totalprice" }
                })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> C3()
        {
            var result = await DB.Fluent<CustomerR>()
                .Lookup(foreignCollectionName: "NationR", localField: "c_nationkey",
                    foreignField: "_id", @as: "nation")
                .Unwind("nation")
                .Lookup(foreignCollectionName: "OrdersR", localField: "_id",
                    foreignField: "o_custkey", @as: "orders")
                .Unwind("orders")
                .Project<BsonDocument>(new BsonDocument
                {
                    { "_id", 0 }, { "c_name", 1 }, { "n_name", "$nation.n_name" },
                    { "o_orderdate", "$orders.o_orderdate" }, { "o_totalprice", "$orders.o_totalprice" }
                })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> C4()
        {
            var result = await DB.Fluent<CustomerR>()
                .Lookup(foreignCollectionName: "NationR", localField: "c_nationkey",
                    foreignField: "_id", @as: "nation")
                .Unwind("nation")
                .Lookup(foreignCollectionName: "RegionR", localField: "nation.n_regionkey",
                    foreignField: "_id", @as: "region")
                .Unwind("region")
                .Lookup(foreignCollectionName: "OrdersR", localField: "_id",
                    foreignField: "o_custkey", @as: "orders")
                .Unwind("orders")
                .Project<BsonDocument>(new BsonDocument
                {
                    { "_id", 0 }, { "c_name", 1 }, { "n_name", "$nation.n_name" },
                    { "r_name", "$region.r_name" },
                    { "o_orderdate", "$orders.o_orderdate" }, { "o_totalprice", "$orders.o_totalprice" }
                })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> C5()
        {
            var result = await DB.Fluent<CustomerR>()
                .Lookup(foreignCollectionName: "OrdersR", localField: "_id",
                    foreignField: "o_custkey", @as: "orders")
                .Unwind("orders", new AggregateUnwindOptions<BsonDocument> { PreserveNullAndEmptyArrays = true })
                .Project<BsonDocument>(new BsonDocument
                {
                    { "_id", 0 }, { "c_custkey", "$_id" }, { "c_name", 1 },
                    { "o_orderkey", "$orders._id" }, { "o_orderdate", "$orders.o_orderdate" }
                })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> D1()
        {
            var result = await DB.Fluent<CustomerR>()
                .Project(c => new { nationkey = c.c_nationkey })
                .Group(x => x.nationkey, g => new { nationkey = g.Key })
                .Project(x => new BsonDocument { { "nationkey", x.nationkey } })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> D2()
        {
            var result = await DB.Fluent<CustomerR>()
                .Lookup(foreignCollectionName: "SupplierR", localField: "_id",
                    foreignField: "_id", @as: "matched_suppliers")
                .Match(new BsonDocument { { "matched_suppliers.0", new BsonDocument { { "$exists", true } } } })
                .Project<BsonDocument>(new BsonDocument { { "_id", 0 }, { "c_custkey", "$_id" } })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> D3()
        {
            var result = await DB.Fluent<CustomerR>()
                .Lookup(foreignCollectionName: "SupplierR", localField: "_id",
                    foreignField: "_id", @as: "matched_suppliers")
                .Match(new BsonDocument { { "matched_suppliers", new BsonDocument { { "$size", 0 } } } })
                .Project<BsonDocument>(new BsonDocument { { "_id", 0 }, { "c_custkey", "$_id" } })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> E1()
        {
            var result = await DB.Fluent<CustomerR>()
                .Sort(Builders<CustomerR>.Sort.Descending(c => c.c_acctbal))
                .Project<BsonDocument>(new BsonDocument
                {
                    { "_id", 0 }, { "c_name", 1 }, { "c_address", 1 }, { "c_acctbal", 1 }
                })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> E2()
        {
            var result = await DB.Fluent<OrdersR>()
                .Sort(Builders<OrdersR>.Sort.Ascending(o => o.o_orderkey))
                .Project<BsonDocument>(new BsonDocument
                {
                    { "_id", 0 }, { "o_orderkey", 1 }, { "o_custkey", 1 },
                    { "o_orderdate", 1 }, { "o_totalprice", 1 }
                })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> E3()
        {
            var result = await DB.Fluent<CustomerR>()
                .Group(x => new { x.c_nationkey, x.c_mktsegment },
                    g => new { c_nationkey = g.Key.c_nationkey, c_mktsegment = g.Key.c_mktsegment })
                .Project(x => new BsonDocument
                {
                    { "c_nationkey", x.c_nationkey }, { "c_mktsegment", x.c_mktsegment }
                })
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> Q1()
        {
            var cutoffDate = new DateTime(1998, 12, 1).Subtract(TimeSpan.FromDays(90));
            var result = await DB.Fluent<LineitemR>()
                .Match(l => l.l_shipdate.DateTime <= cutoffDate)
                .Group(x => new { x.l_returnflag, x.l_linestatus },
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
                    })
                .Project(x => new BsonDocument
                {
                    { "l_returnflag", x.l_returnflag }, { "l_linestatus", x.l_linestatus },
                    { "sum_qty", x.sum_qty }, { "sum_base_price", x.sum_base_price },
                    { "sum_disc_price", x.sum_disc_price }, { "sum_charge", x.sum_charge },
                    { "avg_qty", x.avg_qty }, { "avg_price", x.avg_price },
                    { "avg_disc", x.avg_disc }, { "count_order", x.count_order }
                })
                .Sort(Builders<BsonDocument>.Sort.Ascending("l_returnflag").Ascending("l_linestatus"))
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> Q2()
        {
            var result = await DB.Fluent<PartR>()
                .Match(new BsonDocument
                {
                    { "p_size", 15 },
                    { "p_type", new BsonDocument { { "$regex", "BRASS$" }, { "$options", "i" } } }
                })
                .Lookup(foreignCollectionName: "PartsuppR", localField: "_id",
                    foreignField: "ps_partkey", @as: "partsupp")
                .Unwind("partsupp")
                .Lookup(foreignCollectionName: "SupplierR", localField: "partsupp.ps_suppkey",
                    foreignField: "_id", @as: "supplier")
                .Unwind("supplier")
                .Lookup(foreignCollectionName: "NationR", localField: "supplier.s_nationkey",
                    foreignField: "_id", @as: "nation")
                .Unwind("nation")
                .Lookup(foreignCollectionName: "RegionR", localField: "nation.n_regionkey",
                    foreignField: "_id", @as: "region")
                .Unwind("region")
                .Match(new BsonDocument { { "region.r_name", "EUROPE" } })
                .Group(new BsonDocument
                {
                    { "_id", "$_id" },
                    { "min_supplycost", new BsonDocument { { "$min", "$partsupp.ps_supplycost" } } },
                    { "docs", new BsonDocument { { "$push", "$$ROOT" } } }
                })
                .Project(new BsonDocument
                {
                    { "docs", new BsonDocument { { "$filter", new BsonDocument
                        {
                            { "input", "$docs" }, { "as", "doc" },
                            { "cond", new BsonDocument { { "$eq", new BsonArray { "$$doc.partsupp.ps_supplycost", "$min_supplycost" } } } }
                        }
                    } } }
                })
                .Unwind("docs")
                .Project(new BsonDocument
                {
                    { "_id", 0 }, { "s_acctbal", "$docs.supplier.s_acctbal" },
                    { "s_name", "$docs.supplier.s_name" }, { "n_name", "$docs.nation.n_name" },
                    { "p_partkey", "$docs._id" }, { "p_mfgr", "$docs.p_mfgr" },
                    { "s_address", "$docs.supplier.s_address" }, { "s_phone", "$docs.supplier.s_phone" },
                    { "s_comment", "$docs.supplier.s_comment" }
                })
                .Sort(Builders<BsonDocument>.Sort
                    .Descending("s_acctbal").Ascending("n_name").Ascending("s_name").Ascending("p_partkey"))
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> Q3()
        {
            var date = new DateTime(1995, 3, 15);
            var result = await DB.Fluent<CustomerR>()
                .Match(c => c.c_mktsegment == "BUILDING")
                .Lookup(foreignCollectionName: "OrdersR", localField: "_id",
                    foreignField: "o_custkey", @as: "orders")
                .Unwind("orders")
                .Match(new BsonDocument { { "orders.o_orderdate.DateTime", new BsonDocument { { "$lt", date } } } })
                .Lookup(foreignCollectionName: "LineitemR", localField: "orders._id",
                    foreignField: "l_orderkey", @as: "lineitems")
                .Unwind("lineitems")
                .Match(new BsonDocument { { "lineitems.l_shipdate.DateTime", new BsonDocument { { "$gt", date } } } })
                .Group(new BsonDocument
                {
                    { "_id", new BsonDocument
                        {
                            { "l_orderkey", "$lineitems.l_orderkey" },
                            { "o_orderdate", "$orders.o_orderdate.DateTime" },
                            { "o_shippriority", "$orders.o_shippriority" }
                        }
                    },
                    { "revenue", new BsonDocument { { "$sum", new BsonDocument { { "$multiply",
                        new BsonArray { "$lineitems.l_extendedprice",
                            new BsonDocument { { "$subtract", new BsonArray { 1, "$lineitems.l_discount" } } } } } } } } }
                })
                .Project(new BsonDocument
                {
                    { "_id", 0 }, { "l_orderkey", "$_id.l_orderkey" }, { "revenue", 1 },
                    { "o_orderdate", "$_id.o_orderdate" }, { "o_shippriority", "$_id.o_shippriority" }
                })
                .Sort(Builders<BsonDocument>.Sort.Descending("revenue").Ascending("o_orderdate"))
                .Limit(10)
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> Q4()
        {
            var startDate = new DateTime(1993, 7, 1);
            var endDate = startDate.AddMonths(3);
            var result = await DB.Fluent<OrdersR>()
                .Match(new BsonDocument { { "o_orderdate.DateTime", new BsonDocument
                    { { "$gte", startDate }, { "$lt", endDate } } } })
                .Lookup(foreignCollectionName: "LineitemR", localField: "_id",
                    foreignField: "l_orderkey", @as: "lineitems")
                .Project(new BsonDocument
                {
                    { "o_orderpriority", 1 },
                    { "lineitems", new BsonDocument { { "$filter", new BsonDocument
                        {
                            { "input", "$lineitems" }, { "as", "li" },
                            { "cond", new BsonDocument { { "$lt", new BsonArray
                                { "$$li.l_commitdate.DateTime", "$$li.l_receiptdate.DateTime" } } } }
                        }
                    } } }
                })
                .Match(new BsonDocument { { "lineitems.0", new BsonDocument { { "$exists", true } } } })
                .Group(new BsonDocument
                {
                    { "_id", "$o_orderpriority" },
                    { "order_count", new BsonDocument { { "$sum", 1 } } }
                })
                .Project(new BsonDocument { { "_id", 0 }, { "o_orderpriority", "$_id" }, { "order_count", 1 } })
                .Sort(Builders<BsonDocument>.Sort.Ascending("o_orderpriority"))
                .ToListAsync();
            return result.Count;
        }

        public async Task<int> Q5()
        {
            var startDate = new DateTime(1994, 1, 1);
            var endDate = startDate.AddYears(1);
            var result = await DB.Fluent<CustomerR>()
                .Lookup(foreignCollectionName: "OrdersR", localField: "_id",
                    foreignField: "o_custkey", @as: "orders")
                .Unwind("orders")
                .Match(new BsonDocument { { "orders.o_orderdate.DateTime", new BsonDocument
                    { { "$gte", startDate }, { "$lt", endDate } } } })
                .Lookup(foreignCollectionName: "LineitemR", localField: "orders._id",
                    foreignField: "l_orderkey", @as: "lineitems")
                .Unwind("lineitems")
                .Lookup(foreignCollectionName: "SupplierR", localField: "lineitems.l_suppkey",
                    foreignField: "_id", @as: "supplier")
                .Unwind("supplier")
                .Match(new BsonDocument { { "$expr", new BsonDocument
                    { { "$eq", new BsonArray { "$c_nationkey", "$supplier.s_nationkey" } } } } })
                .Lookup(foreignCollectionName: "NationR", localField: "supplier.s_nationkey",
                    foreignField: "_id", @as: "nation")
                .Unwind("nation")
                .Lookup(foreignCollectionName: "RegionR", localField: "nation.n_regionkey",
                    foreignField: "_id", @as: "region")
                .Unwind("region")
                .Match(new BsonDocument { { "region.r_name", "ASIA" } })
                .Group(new BsonDocument
                {
                    { "_id", "$nation.n_name" },
                    { "revenue", new BsonDocument { { "$sum", new BsonDocument { { "$multiply",
                        new BsonArray { "$lineitems.l_extendedprice",
                            new BsonDocument { { "$subtract", new BsonArray { 1, "$lineitems.l_discount" } } } } } } } } }
                })
                .Project(new BsonDocument { { "_id", 0 }, { "n_name", "$_id" }, { "revenue", 1 } })
                .Sort(Builders<BsonDocument>.Sort.Descending("revenue"))
                .ToListAsync();
            return result.Count;
        }
    }
}
