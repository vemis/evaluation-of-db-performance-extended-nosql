package cz.cuni.mff.java.microservice.repository;

import cz.cuni.mff.java.microservice.model.relational.*;
import dev.morphia.Datastore;
import dev.morphia.aggregation.expressions.*;
import dev.morphia.aggregation.stages.*;
import dev.morphia.query.FindOptions;
import dev.morphia.query.filters.Filters;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static dev.morphia.query.filters.Filters.*;

@Repository
public class QueryRepository {

    private final Datastore datastore;

    public QueryRepository(Datastore datastore) {
        this.datastore = datastore;
    }

    // A1) SELECT * FROM lineitem
    public List<LineitemR> a1() {
        return datastore.find(LineitemR.class).iterator().toList();
    }

    // A2) SELECT * FROM orders WHERE o_orderdate BETWEEN startDate AND endDate
    public List<OrdersR> a2(LocalDate startDate, LocalDate endDate) {
        return datastore.find(OrdersR.class)
                .filter(gte("o_orderdate", startDate), lte("o_orderdate", endDate))
                .iterator()
                .toList();
    }

    // A3) SELECT * FROM customer
    public List<CustomerR> a3() {
        return datastore.find(CustomerR.class).iterator().toList();
    }

    // A4) SELECT * FROM orders WHERE o_orderkey BETWEEN minKey AND maxKey
    public List<OrdersR> a4(int minOrderKey, int maxOrderKey) {
        return datastore.find(OrdersR.class)
                .filter(gte("o_orderkey", minOrderKey), lte("o_orderkey", maxOrderKey))
                .iterator()
                .toList();
    }

    // B1) COUNT(o_orderkey) grouped by month
    public List<Document> b1() {
        return datastore.aggregate(OrdersR.class)
                .group(
                        Group.group(Group.id(
                                DateExpressions.dateToString()
                                        .format("%Y-%m")
                                        .date(Expressions.field("o_orderdate"))
                        )).field("order_count", AccumulatorExpressions.sum(Expressions.value(1)))
                )
                .project(
                        Projection.project()
                                .suppressId()
                                .include("order_count")
                                .include("order_month", Expressions.field("_id"))
                )
                .execute(Document.class)
                .toList();
    }

    // B2) MAX(l_extendedprice) grouped by ship month
    public List<Document> b2() {
        return datastore.aggregate(LineitemR.class)
                .group(
                        Group.group(Group.id(
                                DateExpressions.dateToString()
                                        .format("%Y-%m")
                                        .date(Expressions.field("l_shipdate"))
                        )).field("max_price", AccumulatorExpressions.max(Expressions.field("l_extendedprice")))
                )
                .project(
                        Projection.project()
                                .suppressId()
                                .include("max_price")
                                .include("ship_month", Expressions.field("_id"))
                )
                .execute(Document.class)
                .toList();
    }

    // C1) Cartesian product customer x orders (DNF — limited to 1_500_000 rows)
    public List<Document> c1() {
        return datastore.aggregate(CustomerR.class)
                .lookup(
                        Lookup.lookup(OrdersR.class)
                                .pipeline()
                                .as("ordersR")
                )
                .unwind(Unwind.unwind("ordersR"))
                .project(Projection.project()
                        .suppressId()
                        .include("c_name")
                        .include("ordersR.o_orderdate")
                        .include("ordersR.o_totalprice")
                )
                .limit(1_500_000)
                .execute(Document.class)
                .toList();
    }

    // C2) Inner join customer -> orders on c_custkey = o_custkey
    public List<Document> c2() {
        return datastore.aggregate(CustomerR.class)
                .lookup(
                        Lookup.lookup(OrdersR.class)
                                .localField("_id")
                                .foreignField("o_custkey")
                                .as("ordersR")
                )
                .unwind(Unwind.unwind("ordersR"))
                .project(Projection.project()
                        .suppressId()
                        .include("c_name")
                        .include("ordersR.o_orderdate")
                        .include("ordersR.o_totalprice")
                )
                .execute(Document.class)
                .toList();
    }

    // C3) 3-way join: customer + nation + orders
    public List<Document> c3() {
        return datastore.aggregate(CustomerR.class)
                .lookup(Lookup.lookup(NationR.class)
                        .localField("c_nationkey")
                        .foreignField("_id")
                        .as("nationR"))
                .unwind(Unwind.unwind("nationR"))
                .lookup(Lookup.lookup(OrdersR.class)
                        .localField("_id")
                        .foreignField("o_custkey")
                        .as("ordersR"))
                .unwind(Unwind.unwind("ordersR"))
                .project(Projection.project()
                        .suppressId()
                        .include("c_name")
                        .include("nationR.n_name")
                        .include("ordersR.o_orderdate")
                        .include("ordersR.o_totalprice")
                )
                .execute(Document.class)
                .toList();
    }

    // C4) 4-way join: customer + nation + region + orders
    public List<Document> c4() {
        return datastore.aggregate(CustomerR.class)
                .lookup(Lookup.lookup(NationR.class)
                        .localField("c_nationkey")
                        .foreignField("_id")
                        .as("nationR"))
                .unwind(Unwind.unwind("nationR"))
                .lookup(Lookup.lookup(RegionR.class)
                        .localField("nationR.n_regionkey")
                        .foreignField("_id")
                        .as("regionR"))
                .unwind(Unwind.unwind("regionR"))
                .lookup(Lookup.lookup(OrdersR.class)
                        .localField("_id")
                        .foreignField("o_custkey")
                        .as("ordersR"))
                .unwind(Unwind.unwind("ordersR"))
                .project(Projection.project()
                        .suppressId()
                        .include("c_name")
                        .include("nationR.n_name")
                        .include("regionR.r_name")
                        .include("ordersR.o_orderdate")
                        .include("ordersR.o_totalprice")
                )
                .execute(Document.class)
                .toList();
    }

    // C5) LEFT OUTER JOIN customer -> orders
    public List<Document> c5() {
        return datastore.aggregate(CustomerR.class)
                .lookup(Lookup.lookup(OrdersR.class)
                        .localField("_id")
                        .foreignField("o_custkey")
                        .as("ordersR"))
                .unwind(Unwind.unwind("ordersR").preserveNullAndEmptyArrays(true))
                .project(Projection.project()
                        .suppressId()
                        .include("c_name")
                        .include("ordersR.o_orderdate")
                        .include("ordersR.o_totalprice")
                )
                .execute(Document.class)
                .toList();
    }

    // D1) UNION customer.c_nationkey and supplier.s_nationkey
    public List<Document> d1() {
        return datastore.aggregate(CustomerR.class)
                .project(Projection.project()
                        .include("nationkey", Expressions.field("c_nationkey"))
                        .exclude("_id"))
                .unionWith(SupplierR.class,
                        Projection.project()
                                .include("nationkey", Expressions.field("s_nationkey"))
                                .exclude("_id"))
                .group(Group.group(Group.id(Expressions.field("nationkey"))))
                .project(Projection.project()
                        .include("nationkey", Expressions.field("_id"))
                        .exclude("_id"))
                .execute(Document.class)
                .toList();
    }

    // D2) INTERSECT: customer keys that appear in supplier keys
    public List<Document> d2() {
        return datastore.aggregate(CustomerR.class)
                .group(Group.group(Group.id(Expressions.field("_id"))))
                .lookup(Lookup.lookup(SupplierR.class)
                        .localField("_id")
                        .foreignField("_id")
                        .as("matchesR"))
                .match(ne("matchesR", new Object[0]))
                .project(Projection.project()
                        .suppressId()
                        .include("key", Expressions.field("_id")))
                .execute(Document.class)
                .toList();
    }

    // D3) DIFFERENCE: customer keys NOT in supplier keys
    public List<Document> d3() {
        return datastore.aggregate(CustomerR.class)
                .lookup(Lookup.lookup(SupplierR.class)
                        .localField("_id")
                        .foreignField("_id")
                        .as("matchesR"))
                .match(eq("matchesR", new Object[0]))
                .group(Group.group(Group.id("_id")))
                .project(Projection.project()
                        .suppressId()
                        .include("key", Expressions.field("_id")))
                .execute(Document.class)
                .toList();
    }

    // E1) ORDER BY c_acctbal DESC
    public List<CustomerR> e1() {
        return datastore.find(CustomerR.class, new FindOptions()
                        .sort(dev.morphia.query.Sort.descending("c_acctbal"))
                        .projection()
                        .include("c_name", "c_address", "c_acctbal"))
                .iterator()
                .toList();
    }

    // E2) ORDER BY o_orderkey ASC
    public List<OrdersR> e2() {
        return datastore.find(OrdersR.class, new FindOptions()
                        .sort(dev.morphia.query.Sort.ascending("_id"))
                        .projection()
                        .include("_id", "o_custkey", "o_orderdate", "o_totalprice"))
                .iterator()
                .toList();
    }

    // E3) SELECT DISTINCT c_nationkey, c_mktsegment FROM customer
    public List<Document> e3() {
        return datastore.aggregate(CustomerR.class)
                .group(Group.group(Group.id(
                        Expressions.document()
                                .field("c_nationkey", Expressions.field("c_nationkey"))
                                .field("c_mktsegment", Expressions.field("c_mktsegment"))
                )))
                .project(Projection.project()
                        .include("c_nationkey", Expressions.field("_id.c_nationkey"))
                        .include("c_mktsegment", Expressions.field("_id.c_mktsegment"))
                        .suppressId())
                .execute(Document.class)
                .toList();
    }

    // Q1) Pricing Summary Report
    public List<Document> q1(int deltaDays) {
        LocalDate cutoff = LocalDate.of(1998, 12, 1).minusDays(deltaDays);
        return datastore.aggregate(LineitemR.class)
                .match(lte("l_shipdate", cutoff))
                .group(
                        Group.group(Group.id(
                                        Expressions.document()
                                                .field("l_returnflag", Expressions.field("l_returnflag"))
                                                .field("l_linestatus", Expressions.field("l_linestatus"))
                                ))
                                .field("sum_qty", AccumulatorExpressions.sum(Expressions.field("l_quantity")))
                                .field("sum_base_price", AccumulatorExpressions.sum(Expressions.field("l_extendedprice")))
                                .field("sum_disc_price", AccumulatorExpressions.sum(
                                        MathExpressions.multiply(
                                                Expressions.field("l_extendedprice"),
                                                MathExpressions.subtract(Expressions.value(1), Expressions.field("l_discount"))
                                        )
                                ))
                                .field("sum_charge", AccumulatorExpressions.sum(
                                        MathExpressions.multiply(
                                                Expressions.field("l_extendedprice"),
                                                MathExpressions.subtract(Expressions.value(1), Expressions.field("l_discount")),
                                                MathExpressions.add(Expressions.value(1), Expressions.field("l_tax"))
                                        )
                                ))
                                .field("avg_qty", AccumulatorExpressions.avg(Expressions.field("l_quantity")))
                                .field("avg_price", AccumulatorExpressions.avg(Expressions.field("l_extendedprice")))
                                .field("avg_disc", AccumulatorExpressions.avg(Expressions.field("l_discount")))
                                .field("count_order", AccumulatorExpressions.sum(Expressions.value(1)))
                )
                .project(Projection.project()
                        .include("l_returnflag", Expressions.field("_id.l_returnflag"))
                        .include("l_linestatus", Expressions.field("_id.l_linestatus"))
                        .include("sum_qty")
                        .include("sum_base_price")
                        .include("sum_disc_price")
                        .include("sum_charge")
                        .include("avg_qty")
                        .include("avg_price")
                        .include("avg_disc")
                        .include("count_order")
                        .suppressId())
                .sort(Sort.sort().ascending("l_returnflag", "l_linestatus"))
                .execute(Document.class)
                .toList();
    }

    // Q2) Minimum Cost Supplier
    public List<Document> q2(int size, String type, String region) {
        return datastore.aggregate(PartR.class)
                .match(Filters.eq("p_size", size), regex("p_type", type.replace("%", "") + "$"))
                .lookup(Lookup.lookup(PartsuppR.class)
                        .localField("_id").foreignField("ps_partkey").as("ps"))
                .unwind(Unwind.unwind("ps"))
                .lookup(Lookup.lookup(SupplierR.class)
                        .localField("ps.ps_suppkey").foreignField("_id").as("s"))
                .unwind(Unwind.unwind("s"))
                .lookup(Lookup.lookup(NationR.class)
                        .localField("s.s_nationkey").foreignField("_id").as("n"))
                .unwind(Unwind.unwind("n"))
                .lookup(Lookup.lookup(RegionR.class)
                        .localField("n.n_regionkey").foreignField("_id").as("r"))
                .unwind(Unwind.unwind("r"))
                .match(Filters.eq("r.r_name", region))
                .group(
                        Group.group(Group.id(Expressions.field("_id")))
                                .field("minSupplyCost", AccumulatorExpressions.min(Expressions.field("ps.ps_supplycost")))
                                .field("docs", AccumulatorExpressions.push(Expressions.field("$$ROOT")))
                )
                .unwind(Unwind.unwind("docs"))
                .match(expr(ComparisonExpressions.eq(
                        Expressions.field("docs.ps.ps_supplycost"),
                        Expressions.field("minSupplyCost")
                )))
                .replaceRoot(ReplaceRoot.replaceRoot(Expressions.field("docs")))
                .project(Projection.project()
                        .include("s_acctbal", Expressions.field("s.s_acctbal"))
                        .include("s_name", Expressions.field("s.s_name"))
                        .include("n_name", Expressions.field("n.n_name"))
                        .include("p_partkey")
                        .include("p_mfgr")
                        .include("s_address", Expressions.field("s.s_address"))
                        .include("s_phone", Expressions.field("s.s_phone"))
                        .include("s_comment", Expressions.field("s.s_comment"))
                        .suppressId())
                .sort(Sort.sort()
                        .descending("s_acctbal")
                        .ascending("n_name", "s_name", "p_partkey"))
                .execute(Document.class)
                .toList();
    }

    // Q3) Shipping Priority
    public List<Document> q3(String segment, LocalDate orderDate, LocalDate shipDate) {
        return datastore.aggregate(CustomerR.class)
                .match(Filters.eq("c_mktsegment", segment))
                .lookup(Lookup.lookup(OrdersR.class)
                        .localField("_id").foreignField("o_custkey").as("orders"))
                .unwind(Unwind.unwind("orders"))
                .match(Filters.lt("orders.o_orderdate", orderDate))
                .lookup(Lookup.lookup(LineitemR.class)
                        .localField("orders._id").foreignField("l_orderkey").as("lineitems"))
                .unwind(Unwind.unwind("lineitems"))
                .match(Filters.gt("lineitems.l_shipdate", shipDate))
                .group(
                        Group.group(Group.id(
                                        Expressions.document()
                                                .field("l_orderkey", Expressions.field("lineitems.l_orderkey"))
                                                .field("o_orderdate", Expressions.field("orders.o_orderdate"))
                                                .field("o_shippriority", Expressions.field("orders.o_shippriority"))
                                ))
                                .field("revenue", AccumulatorExpressions.sum(
                                        MathExpressions.multiply(
                                                Expressions.field("lineitems.l_extendedprice"),
                                                MathExpressions.subtract(Expressions.value(1), Expressions.field("lineitems.l_discount"))
                                        )
                                ))
                )
                .sort(Sort.sort().descending("revenue").ascending("_id.o_orderdate"))
                .limit(10)
                .project(Projection.project()
                        .include("l_orderkey", Expressions.field("_id.l_orderkey"))
                        .include("revenue")
                        .include("o_orderdate", Expressions.field("_id.o_orderdate"))
                        .include("o_shippriority", Expressions.field("_id.o_shippriority"))
                        .suppressId())
                .execute(Document.class)
                .toList();
    }

    // Q4) Order Priority Checking
    public List<Document> q4(LocalDate orderDate) {
        LocalDate orderDateEnd = orderDate.plusMonths(3);
        return datastore.aggregate(OrdersR.class)
                .match(expr(BooleanExpressions.and(
                        ComparisonExpressions.gte(Expressions.field("o_orderdate"), Expressions.value(orderDate)),
                        ComparisonExpressions.lt(Expressions.field("o_orderdate"), Expressions.value(orderDateEnd))
                )))
                .lookup(
                        Lookup.lookup(LineitemR.class)
                                .let("orderkey", Expressions.field("_id"))
                                .pipeline(
                                        Match.match(expr(BooleanExpressions.and(
                                                ComparisonExpressions.eq(
                                                        Expressions.field("l_orderkey"),
                                                        Expressions.field("$$orderkey")
                                                ),
                                                ComparisonExpressions.lt(
                                                        Expressions.field("l_commitdate"),
                                                        Expressions.field("l_receiptdate")
                                                )
                                        )))
                                )
                                .as("matching_lineitems")
                )
                .match(Filters.ne("matching_lineitems", new Object[0]))
                .group(
                        Group.group(Group.id(Expressions.field("o_orderpriority")))
                                .field("order_count", AccumulatorExpressions.sum(Expressions.value(1)))
                )
                .sort(Sort.sort().ascending("_id"))
                .execute(Document.class)
                .toList();
    }

    // Q5) Local Supplier Volume
    public List<Document> q5(String region, LocalDate orderDate) {
        LocalDate orderDateEnd = orderDate.plusYears(1);
        return datastore.aggregate(CustomerR.class)
                .lookup(Lookup.lookup(OrdersR.class)
                        .localField("_id").foreignField("o_custkey").as("orders"))
                .unwind(Unwind.unwind("orders"))
                .match(expr(BooleanExpressions.and(
                        ComparisonExpressions.gte(Expressions.field("orders.o_orderdate"), Expressions.value(orderDate)),
                        ComparisonExpressions.lt(Expressions.field("orders.o_orderdate"), Expressions.value(orderDateEnd))
                )))
                .lookup(Lookup.lookup(LineitemR.class)
                        .localField("orders._id").foreignField("l_orderkey").as("lineitems"))
                .unwind(Unwind.unwind("lineitems"))
                .lookup(Lookup.lookup(SupplierR.class)
                        .localField("lineitems.l_suppkey").foreignField("_id").as("supplier"))
                .unwind(Unwind.unwind("supplier"))
                .match(expr(ComparisonExpressions.eq(
                        Expressions.field("c_nationkey"),
                        Expressions.field("supplier.s_nationkey")
                )))
                .lookup(Lookup.lookup(NationR.class)
                        .localField("supplier.s_nationkey").foreignField("_id").as("nation"))
                .unwind(Unwind.unwind("nation"))
                .lookup(Lookup.lookup(RegionR.class)
                        .localField("nation.n_regionkey").foreignField("_id").as("region"))
                .unwind(Unwind.unwind("region"))
                .match(Filters.eq("region.r_name", region))
                .group(
                        Group.group(Group.id(Expressions.field("nation.n_name")))
                                .field("revenue", AccumulatorExpressions.sum(
                                        MathExpressions.multiply(
                                                Expressions.field("lineitems.l_extendedprice"),
                                                MathExpressions.subtract(Expressions.value(1), Expressions.field("lineitems.l_discount"))
                                        )
                                ))
                )
                .sort(Sort.sort().descending("revenue"))
                .project(Projection.project()
                        .include("n_name", Expressions.field("_id"))
                        .include("revenue")
                        .suppressId())
                .execute(Document.class)
                .toList();
    }
}
