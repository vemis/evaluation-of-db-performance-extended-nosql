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

    /**
     * A1) Non-Indexed Columns
     * <p>
     * Selects all records from the lineitem table.
     * <pre>SELECT * FROM lineitem;</pre>
     */
    public List<LineitemR> a1() {
        return datastore.find(LineitemR.class).iterator().toList();
    }

    /**
     * A2) Non-Indexed Columns — Range Query
     * <p>
     * Selects all records from the orders table where the order date is within the given range.
     * <pre>
     * SELECT * FROM orders
     * WHERE o_orderdate BETWEEN startDate AND endDate;
     * </pre>
     */
    public List<OrdersR> a2(LocalDate startDate, LocalDate endDate) {
        return datastore.find(OrdersR.class)
                .filter(gte("o_orderdate", startDate), lte("o_orderdate", endDate))
                .iterator()
                .toList();
    }

    /**
     * A3) Indexed Columns
     * <p>
     * Selects all records from the customer table.
     * <pre>SELECT * FROM customer;</pre>
     */
    public List<CustomerR> a3() {
        return datastore.find(CustomerR.class).iterator().toList();
    }

    /**
     * A4) Indexed Columns — Range Query
     * <p>
     * Selects all records from the orders table where the order key is within the given range.
     * <pre>
     * SELECT * FROM orders
     * WHERE o_orderkey BETWEEN minOrderKey AND maxOrderKey;
     * </pre>
     */
    public List<OrdersR> a4(int minOrderKey, int maxOrderKey) {
        return datastore.find(OrdersR.class)
                .filter(gte("o_orderkey", minOrderKey), lte("o_orderkey", maxOrderKey))
                .iterator()
                .toList();
    }

    /**
     * B1) COUNT
     * <p>
     * Counts the number of orders grouped by order month.
     * <pre>
     * SELECT COUNT(o.o_orderkey) AS order_count,
     *        DATE_FORMAT(o.o_orderdate, '%Y-%m') AS order_month
     * FROM orders o
     * GROUP BY order_month;
     * </pre>
     */
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

    /**
     * B2) MAX
     * <p>
     * Finds the maximum extended price from the lineitem table grouped by ship month.
     * <pre>
     * SELECT DATE_FORMAT(l.l_shipdate, '%Y-%m') AS ship_month,
     *        MAX(l.l_extendedprice) AS max_price
     * FROM lineitem l
     * GROUP BY ship_month;
     * </pre>
     */
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

    /**
     * C1) Non-Indexed Columns
     * <p>
     * Cartesian product of customer and orders — always DNF, limited to 1,500,000 rows.
     * <pre>
     * SELECT c.c_name, o.o_orderdate, o.o_totalprice
     * FROM customer c, orders o;
     * </pre>
     */
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

    /**
     * C2) Indexed Columns
     * <p>
     * Inner join of customer and orders on {@code c_custkey = o_custkey}.
     * <pre>
     * SELECT c.c_name, o.o_orderdate, o.o_totalprice
     * FROM customer c
     * JOIN orders o ON c.c_custkey = o.o_custkey;
     * </pre>
     */
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

    /**
     * C3) Complex Join 1
     * <p>
     * 3-way join: customer + nation + orders.
     * <pre>
     * SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice
     * FROM customer c
     * JOIN nation n ON c.c_nationkey = n.n_nationkey
     * JOIN orders o ON c.c_custkey = o.o_custkey;
     * </pre>
     */
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

    /**
     * C4) Complex Join 2
     * <p>
     * 4-way join: customer + nation + region + orders.
     * <pre>
     * SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice
     * FROM customer c
     * JOIN nation n ON c.c_nationkey = n.n_nationkey
     * JOIN region r ON n.n_regionkey = r.r_regionkey
     * JOIN orders o ON c.c_custkey = o.o_custkey;
     * </pre>
     */
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

    /**
     * C5) Left Outer Join
     * <p>
     * Returns all customers and their orders, including customers without orders.
     * <pre>
     * SELECT c.c_custkey, c.c_name, o.o_orderkey, o.o_orderdate
     * FROM customer c
     * LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey;
     * </pre>
     */
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

    /**
     * D1) UNION
     * <p>
     * Combines distinct nation keys from customer and supplier tables.
     * <pre>
     * (SELECT c_nationkey FROM customer)
     * UNION
     * (SELECT s_nationkey FROM supplier);
     * </pre>
     */
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

    /**
     * D2) INTERSECT
     * <p>
     * Finds customer keys that also appear as supplier keys.
     * MySQL does not support INTERSECT directly — simulated via {@code IN}.
     * <pre>
     * SELECT DISTINCT c.c_custkey
     * FROM customer c
     * WHERE c.c_custkey IN (SELECT s.s_suppkey FROM supplier s);
     * </pre>
     */
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

    /**
     * D3) DIFFERENCE
     * <p>
     * Finds customer keys that do not appear in the supplier table.
     * MySQL does not support EXCEPT/MINUS directly — simulated via {@code NOT IN}.
     * <pre>
     * SELECT DISTINCT c.c_custkey
     * FROM customer c
     * WHERE c.c_custkey NOT IN (SELECT DISTINCT s.s_suppkey FROM supplier s);
     * </pre>
     */
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

    /**
     * E1) Non-Indexed Columns Sorting
     * <p>
     * Sorts customer names, addresses, and account balances in descending order of account balance.
     * <pre>
     * SELECT c_name, c_address, c_acctbal
     * FROM customer
     * ORDER BY c_acctbal DESC;
     * </pre>
     */
    public List<CustomerR> e1() {
        return datastore.find(CustomerR.class, new FindOptions()
                        .sort(dev.morphia.query.Sort.descending("c_acctbal"))
                        .projection()
                        .include("c_name", "c_address", "c_acctbal"))
                .iterator()
                .toList();
    }

    /**
     * E2) Indexed Columns Sorting
     * <p>
     * Sorts order keys, customer keys, order dates, and total prices in ascending order of order key.
     * <pre>
     * SELECT o_orderkey, o_custkey, o_orderdate, o_totalprice
     * FROM orders
     * ORDER BY o_orderkey;
     * </pre>
     */
    public List<OrdersR> e2() {
        return datastore.find(OrdersR.class, new FindOptions()
                        .sort(dev.morphia.query.Sort.ascending("_id"))
                        .projection()
                        .include("_id", "o_custkey", "o_orderdate", "o_totalprice"))
                .iterator()
                .toList();
    }

    /**
     * E3) Distinct
     * <p>
     * Selects distinct nation keys and market segments from the customer table.
     * <pre>
     * SELECT DISTINCT c_nationkey, c_mktsegment
     * FROM customer;
     * </pre>
     */
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

    /**
     * Q1) Pricing Summary Report Query
     * <p>
     * Reports the amount of business that was billed, shipped, and returned.
     * <pre>
     * SELECT l_returnflag, l_linestatus,
     *   SUM(l_quantity) AS sum_qty,
     *   SUM(l_extendedprice) AS sum_base_price,
     *   SUM(l_extendedprice * (1 - l_discount)) AS sum_disc_price,
     *   SUM(l_extendedprice * (1 - l_discount) * (1 + l_tax)) AS sum_charge,
     *   AVG(l_quantity) AS avg_qty, AVG(l_extendedprice) AS avg_price,
     *   AVG(l_discount) AS avg_disc, COUNT(*) AS count_order
     * FROM lineitem
     * WHERE l_shipdate &lt;= DATE_SUB('1998-12-01', INTERVAL deltaDays DAY)
     * GROUP BY l_returnflag, l_linestatus
     * ORDER BY l_returnflag, l_linestatus;
     * </pre>
     */
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

    /**
     * Q2) Minimum Cost Supplier Query
     * <p>
     * Finds which supplier should be selected to place an order for a given part in a given region.
     * Selects the supplier with the minimum supply cost per part matching the given size, type, and region.
     * <pre>
     * SELECT s.s_acctbal, s.s_name, n.n_name, p.p_partkey, p.p_mfgr,
     *        s.s_address, s.s_phone, s.s_comment
     * FROM part p, supplier s, partsupp ps, nation n, region r
     * WHERE p.p_partkey = ps.ps_partkey AND s.s_suppkey = ps.ps_suppkey
     *   AND p.p_size = size AND p.p_type LIKE '%type'
     *   AND s.s_nationkey = n.n_nationkey AND n.n_regionkey = r.r_regionkey
     *   AND r.r_name = region
     *   AND ps.ps_supplycost = (SELECT MIN(ps.ps_supplycost) ...)
     * ORDER BY s.s_acctbal DESC, n.n_name, s.s_name, p.p_partkey;
     * </pre>
     */
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

    /**
     * Q3) Shipping Priority Query
     * <p>
     * Retrieves the 10 unshipped orders with the highest value.
     * <pre>
     * SELECT l.l_orderkey, SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue,
     *        o.o_orderdate, o.o_shippriority
     * FROM customer c, orders o, lineitem l
     * WHERE c.c_mktsegment = segment
     *   AND c.c_custkey = o.o_custkey AND l.l_orderkey = o.o_orderkey
     *   AND o.o_orderdate &lt; orderDate AND l.l_shipdate &gt; shipDate
     * GROUP BY l.l_orderkey, o.o_orderdate, o.o_shippriority
     * ORDER BY revenue DESC, o.o_orderdate
     * LIMIT 10;
     * </pre>
     */
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

    /**
     * Q4) Order Priority Checking Query
     * <p>
     * Determines how well the order priority system is working and gives an assessment of customer satisfaction.
     * <pre>
     * SELECT o_orderpriority, COUNT(*) AS order_count
     * FROM orders
     * WHERE o_orderdate &gt;= orderDate
     *   AND o_orderdate &lt; DATE_ADD(orderDate, INTERVAL 3 MONTH)
     *   AND EXISTS (
     *     SELECT * FROM lineitem
     *     WHERE l_orderkey = o_orderkey AND l_commitdate &lt; l_receiptdate
     *   )
     * GROUP BY o_orderpriority
     * ORDER BY o_orderpriority;
     * </pre>
     */
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

    /**
     * Q5) Local Supplier Volume Query
     * <p>
     * Lists the revenue volume done through local suppliers (customer and supplier in same nation).
     * <pre>
     * SELECT n.n_name, SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue
     * FROM customer c, orders o, lineitem l, supplier s, nation n, region r
     * WHERE c.c_custkey = o.o_custkey AND l.l_orderkey = o.o_orderkey
     *   AND l.l_suppkey = s.s_suppkey AND c.c_nationkey = s.s_nationkey
     *   AND s.s_nationkey = n.n_nationkey AND n.n_regionkey = r.r_regionkey
     *   AND r.r_name = region
     *   AND o.o_orderdate &gt;= orderDate
     *   AND o.o_orderdate &lt; DATE_ADD(orderDate, INTERVAL 1 YEAR)
     * GROUP BY n.n_name
     * ORDER BY revenue DESC;
     * </pre>
     */
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
