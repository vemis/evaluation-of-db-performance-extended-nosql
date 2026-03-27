package cz.cuni.mff.mongodb_java.morphia.benchmarks;

import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.*;
import dev.morphia.Datastore;
import dev.morphia.aggregation.expressions.*;
import dev.morphia.aggregation.stages.*;

import dev.morphia.aggregation.expressions.DateExpressions;

import dev.morphia.query.FindOptions;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.filters.Filters;
import org.bson.Document;

import javax.print.Doc;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static dev.morphia.query.filters.Filters.*;

import dev.morphia.aggregation.AggregationImpl;

public class QueriesMorphiaR {
    /**
     * A1) Non-Indexed Columns
     *
     * This query selects all records from the lineitem table
     * ```sql
     *         SELECT * FROM lineitem;
     * ```
     */
    public static List<LineitemR> A1(Datastore datastore) {
        List<LineitemR> a1 = datastore
                .find(LineitemR.class)
                .iterator()
                .toList();

        return a1;
    }

    /**
     * A2) Non-Indexed Columns — Range Query
     *
     * This query selects all records from the orders table where the order date is between '1996-01-01' and '1996-12-31'
     * ```sql
     * SELECT * FROM orders
     * WHERE o_orderdate
     *     BETWEEN '1996-01-01' AND '1996-12-31';
     * ```
     * @param datastore
     * @return
     */
    public static List<OrdersR> A2(Datastore datastore) {
        List<OrdersR> a2 = datastore
                .find(OrdersR.class)
                .filter(gte("o_orderdate", LocalDate.parse("1996-01-01")), lte("o_orderdate", LocalDate.parse("1996-12-31")))
                .iterator()
                .toList();

        return a2;
    }

    /**
     * ### A3) Indexed Columns
     *
     * This query selects all records from the customer table
     * ```sql
     * SELECT * FROM customer;
     * ```
     * */
    public static List<CustomerR> A3(Datastore datastore) {
        List<CustomerR> a3 = datastore
                .find(CustomerR.class)
                .iterator()
                .toList();

        return a3;
    }

    /**
     * ### A4) Indexed Columns — Range Query
     *
     * This query selects all records from the orders table where the order key is between 1000 and 50000
     * ```sql
     * SELECT * FROM orders
     * WHERE o_orderkey BETWEEN 1000 AND 50000;
     * ```
     * */
    public static List<OrdersR> A4(Datastore datastore) {
        List<OrdersR> a4 = datastore
                .find(OrdersR.class)
                .filter(gte("o_orderkey", 1000), lte("o_orderkey",50000 ))
                .iterator()
                .toList();

        return a4;
    }


    /**
     * ### B1) COUNT
     *
     * This query counts the number of orders grouped by order month
     * ```sql
     * SELECT COUNT(o.o_orderkey) AS order_count,
     *        DATE_FORMAT(o.o_orderdate, '%Y-%m') AS order_month
     * FROM orders o
     * GROUP BY order_month;
     * ```
     */
    public static List<Document> B1(Datastore datastore) {

         List<Document> aggregation = datastore.aggregate(OrdersR.class)
                .group(
                        Group.group(Group.id(

                                        DateExpressions.dateToString()
                                                .format("%Y-%m")
                                                .date(Expressions.field("o_orderdate"))

                                )
                        ).field("order_count", AccumulatorExpressions.sum(Expressions.value(1)))
                )
                 .project(
                         Projection.project()
                                 .suppressId()
                                .include("order_count")
                                .include("order_month", Expressions.field("_id"))
                )
                .execute(Document.class)
                .toList();

         return aggregation;
    }

    /**
     * ### B2) MAX
     *
     * This query finds the maximum extended price from the lineitem table grouped by ship month
     * ```sql
     * SELECT DATE_FORMAT(l.l_shipdate, '%Y-%m') AS ship_month,
     *        MAX(l.l_extendedprice) AS max_price
     * FROM lineitem l
     * GROUP BY ship_month;
     * ```
     * */
    public static List<Document> B2(Datastore datastore){
        List<Document> aggregation = datastore.aggregate(LineitemR.class)
                .group(
                        Group.group(Group.id(

                                        DateExpressions.dateToString()
                                                .format("%Y-%m")
                                                .date(Expressions.field("l_shipdate"))

                                )
                        ).field("max_price", AccumulatorExpressions.max(Expressions.field("l_extendedprice")))
                )
                .project(
                        Projection.project()
                                .suppressId()
                                .include("max_price")
                                .include("ship_month", Expressions.field("_id"))
                )
                .execute(Document.class)
                .toList();

        return aggregation;
    }


    /**
     *
     ### C1) Non-Indexed Columns

     This query gives customer names, order dates, and total prices for customers
     ```sql
     SELECT c.c_name, o.o_orderdate, o.o_totalprice
     FROM customer c, orders o;
     ```
     */
    public static List<Document> C1(Datastore datastore) {
        List<Document> c1 = datastore.aggregate(CustomerR.class)
                .lookup(
                        Lookup.lookup(OrdersR.class)
                                //.localField("_id")
                                //.foreignField("o_custkey")
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

        return c1;
    }


    /**
     * ### C2) Indexed Columns
     *
     * This query gives customer names, order dates, and total prices for all customers
     * ```sql
     * SELECT c.c_name, o.o_orderdate, o.o_totalprice
     * FROM customer c
     * JOIN orders o ON c.c_custkey = o.o_custkey;
     * ```
     */
    public static List<Document> C2(Datastore datastore) {
        List<Document> c2 = datastore.aggregate(CustomerR.class)
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

        return c2;
    }

    /**
     * ### C3) Complex Join 1
     *
     * This query gives customer names, nation names, order dates, and total prices for customers
     * ```sql
     * SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice
     * FROM customer c
     * JOIN nation n ON c.c_nationkey = n.n_nationkey
     * JOIN orders o ON c.c_custkey = o.o_custkey;
     * ```
     */
    public static List<Document> C3(Datastore datastore) {
        List<Document> c3 = datastore.aggregate(CustomerR.class)
                .lookup(
                        Lookup.lookup(NationR.class)
                                .localField("c_nationkey")
                                .foreignField("_id")
                                .as("nationR")

                )
                .unwind(Unwind.unwind("nationR"))

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
                        .include("nationR.n_name")
                        .include("ordersR.o_orderdate")
                        .include("ordersR.o_totalprice")
                )
                .execute(Document.class)
                .toList();

        return c3;
    }


    /**
     * ### C4) Complex Join 2
     *
     * This query gives customer names, nation names, region names, order dates, and total prices for customers
     * ```sql
     * SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice
     * FROM customer c
     * JOIN nation n ON c.c_nationkey = n.n_nationkey
     * JOIN region r ON n.n_regionkey = r.r_regionkey
     * JOIN orders o ON c.c_custkey = o.o_custkey;
     * ```
     */
    public static List<Document> C4(Datastore datastore) {
        List<Document> c4 = datastore.aggregate(CustomerR.class)
                .lookup(
                        Lookup.lookup(NationR.class)
                                .localField("c_nationkey")
                                .foreignField("_id")
                                .as("nationR")

                )
                .unwind(Unwind.unwind("nationR"))

                .lookup(
                        Lookup.lookup(RegionR.class)
                                .localField("nationR.n_regionkey")
                                .foreignField("_id")
                                .as("regionR")

                )
                .unwind(Unwind.unwind("nationR"))

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
                        .include("nationR.n_name")
                        .include("regionR.r_name")
                        .include("ordersR.o_orderdate")
                        .include("ordersR.o_totalprice")
                )
                .execute(Document.class)
                .toList();

        return c4;
    }


    /**
     * ### C5) Left Outer Join
     *
     * This query gives customer names and order dates for all customers, including those without orders
     * ```sql
     * SELECT c.c_custkey, c.c_name, o.o_orderkey, o.o_orderdate
     * FROM customer c
     * LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey;
     * ```
     */
    public static List<Document> C5(Datastore datastore) {
        List<Document> c5 = datastore.aggregate(CustomerR.class)
                .lookup(
                        Lookup.lookup(OrdersR.class)
                                .localField("_id")
                                .foreignField("o_custkey")
                                .as("ordersR")

                )
                .unwind(Unwind.unwind("ordersR")
                        .preserveNullAndEmptyArrays(true))

                .project(Projection.project()
                        .suppressId()
                        .include("c_name")
                        .include("ordersR.o_orderdate")
                        .include("ordersR.o_totalprice")
                )
                .execute(Document.class)
                .toList();

        return c5;
    }



    /**
     * ### D1) UNION
     *
     * This query combines customer and supplier nation keys
     * ```sql
     * (SELECT c_nationkey FROM customer)
     * UNION
     * (SELECT s_nationkey FROM supplier);
     * ```
     * @param datastore
     */
    public static List<Document> D1(Datastore datastore) {
        List<Document> result = datastore.aggregate(CustomerR.class)
                // SELECT c_nationkey AS nationkey
                .project(
                        Projection.project()
                                .include("nationkey", Expressions.field("c_nationkey"))
                                .exclude("_id")
                ) //size() 30_000

                // UNION supplier
                .unionWith(SupplierR.class,
                        Projection.project()
                                .include("nationkey", Expressions.field("s_nationkey"))
                                .exclude("_id")
                ) //size() 32_000

                // Remove duplicates (SQL UNION behavior)
                .group(
                        Group.group(
                                Group.id(
                                        Expressions.field("nationkey")
                                )

                        )
                ) //25

                // Final reshape
                .project(
                        Projection.project()
                                .include("nationkey", Expressions.field("_id"))
                                .exclude("_id")
                )

                .execute(Document.class)
                .toList();

                return result;
    }

    /**
     * ### D2) INTERSECT
     *
     * This query finds common customer and supplier keys
     * MySQL doesn't directly support INTERSECT, so I used IN
     * ```sql
     * SELECT DISTINCT c.c_custkey
     * FROM customer c
     * WHERE c.c_custkey IN (
     *     SELECT s.s_suppkey
     *     FROM supplier s
     * );
     * ```
     */
    public static List<Document> D2(Datastore datastore) {
        List<Document> d2 = datastore.aggregate(CustomerR.class)

                .group(
                        Group.group(
                                Group.id(
                                        Expressions.field("_id")
                                )

                        )
                )
                .lookup(
                        Lookup.lookup(SupplierR.class)
                                .localField("_id")
                                .foreignField("_id")
                                .as("matchesR")
                )
                .match(
                        ne("matchesR", new Object[0])
                )
                .project(
                        Projection.project()
                                .suppressId()
                                .include("key", Expressions.field("_id"))
                )


                .execute(Document.class)
                .toList();

        return d2;
    }


    /**
     * ### D3) DIFFERENCE
     *
     * This query finds customer keys that are not in the supplier table
     * MySQL doesn't directly support EXCEPT/MINUS, so I used NOT IN
     * ```sql
     * SELECT DISTINCT c.c_custkey
     * FROM customer c
     * WHERE c.c_custkey NOT IN (
     *     SELECT DISTINCT s.s_suppkey
     *     FROM supplier s
     * );
     * ```
     */
    public static List<Document> D3(Datastore datastore) {
        List<Document> d3 = datastore.aggregate(CustomerR.class)

                .lookup(
                        Lookup.lookup(SupplierR.class)
                                .localField("_id")
                                .foreignField("_id")
                                .as("matchesR")
                )
                .match(
                        eq("matchesR", new Object[0]) // keep only those NOT found in supplier
                )
                .group(
                        Group.group(
                                Group.id("_id")
                        )
                )
                .project(
                        Projection.project()
                                .suppressId()
                                .include("key",  Expressions.field("_id"))
                )


                .execute(Document.class)
                .toList();

        return d3;
    }


    /**
     * ### E1) Non-Indexed Columns Sorting
     *
     * This query sorts customer names, addresses, and account balances in descending order of account balance
     * ```sql
     * SELECT c_name, c_address, c_acctbal
     * FROM customer
     * ORDER BY c_acctbal DESC
     * ```
     */
    public static List<CustomerR> E1(Datastore datastore) {
        List<CustomerR> e1 = datastore.find(CustomerR.class, new FindOptions()
                        .sort(dev.morphia.query.Sort.descending ("c_acctbal"))
                        .projection()
                        .include("c_name", "c_address", "c_acctbal")
                )
                .iterator()
                .toList();

        return e1;
    }

    /**
     * ### E2) Indexed Columns Sorting
     *
     * This query sorts order keys, customer keys, order dates, and total prices in ascending order of order key
     * ```sql
     * SELECT o_orderkey, o_custkey, o_orderdate, o_totalprice
     * FROM orders
     * ORDER BY o_orderkey
     * ```
     */
    public static List<OrdersR> E2(Datastore datastore) {
        List<OrdersR> e2 = datastore.find(OrdersR.class, new FindOptions()
                        .sort(dev.morphia.query.Sort.ascending ("_id"))
                        .projection()
                        .include("_id", "o_custkey", "o_orderdate", "o_totalprice")
                )
                .iterator()
                .toList();

        return e2;
    }

    /**
     * ### E3) Distinct
     *
     * This query selects distinct nation keys and market segments from the customer table
     * ```sql
     * SELECT DISTINCT c_nationkey, c_mktsegment
     * FROM customer;
     * ```
     */
    public static List<Document> E3(Datastore datastore) {
        var e3 = datastore.aggregate(CustomerR.class)
                .group(
                        Group.group(
                                Group.id(
                                            Expressions.document()
                                                    .field("c_nationkey", Expressions.field("c_nationkey"))
                                                    .field("c_mktsegment", Expressions.field("c_mktsegment"))
                                        )
                        )
                )
                .project(
                        Projection.project()

                                .include("c_nationkey", Expressions.field("_id.c_nationkey"))
                                .include("c_mktsegment", Expressions.field("_id.c_mktsegment"))
                                .suppressId()
                );

        return e3
                .execute(Document.class)
                .toList();

        //return e3;
    }



     // ## Advanced Queries
     // ## TPC-H Benchmark Queries

    /**
     * ### Q1) Pricing Summary Report Query
     *
     * //This query reports the amount of business that was billed, shipped, and returned
     * ```sql
     * SELECT
     *   l_returnflag,
     *   l_linestatus,
     *   SUM(l_quantity) AS sum_qty,
     *   SUM(l_extendedprice) AS sum_base_price,
     *   SUM(l_extendedprice * (1 - l_discount)) AS sum_disc_price,
     *   SUM(l_extendedprice * (1 - l_discount) * (1 + l_tax)) AS sum_charge,
     *   AVG(l_quantity) AS avg_qty,
     *   AVG(l_extendedprice) AS avg_price,
     *   AVG(l_discount) AS avg_disc,
     *   COUNT(*) AS count_order
     * FROM lineitem
     * WHERE l_shipdate <= DATE_SUB('1998-12-01', INTERVAL 90 DAY)
     * GROUP BY l_returnflag, l_linestatus
     * ORDER BY l_returnflag, l_linestatus
     * ```
     */
    public static List<Document> Q1(Datastore datastore){
        var q1 = datastore.aggregate(LineitemR.class)
                .match(
                        expr(
                                ComparisonExpressions.lte(
                                        Expressions.field("l_shipdate"),
                                        DateExpressions.dateSubtract(
                                                Expressions.value(LocalDate.parse("1998-12-01")), // 1998-12-01
                                                90,
                                                TimeUnit.DAY
                                        )
                                )
                        )
                )
                .group(
                        Group.group(
                                Group.id(
                                        Expressions.document()
                                                .field("l_returnflag", Expressions.field("l_returnflag"))
                                                .field("l_linestatus", Expressions.field("l_linestatus"))
                                )

                        )
                                // SUM(l_quantity)
                                .field("sum_qty",
                                        AccumulatorExpressions.sum(Expressions.field("l_quantity"))
                                )
                                // SUM(l_extendedprice)
                                .field("sum_base_price",
                                        AccumulatorExpressions.sum(Expressions.field("l_extendedprice"))
                                )
                                // SUM(l_extendedprice * (1 - l_discount))
                                .field("sum_disc_price",
                                        AccumulatorExpressions.sum(
                                                MathExpressions.multiply(
                                                        Expressions.field("l_extendedprice"),
                                                        MathExpressions.subtract(
                                                                Expressions.value(1),
                                                                Expressions.field("l_discount")
                                                        )
                                                )
                                        )
                                )
                                // SUM(l_extendedprice * (1 - l_discount) * (1 + l_tax))
                                .field("sum_charge",
                                        AccumulatorExpressions.sum(
                                                MathExpressions.multiply(
                                                        Expressions.field("l_extendedprice"),
                                                        MathExpressions.subtract(
                                                                Expressions.value(1),
                                                                Expressions.field("l_discount")
                                                        ),
                                                        MathExpressions.add(
                                                                Expressions.value(1),
                                                                Expressions.field("l_tax")
                                                        )
                                                )
                                        )
                                )
                                // AVG(l_quantity)
                                .field("avg_qty",
                                        AccumulatorExpressions.avg(Expressions.field("l_quantity"))
                                )

                                // AVG(l_extendedprice)
                                .field("avg_price",
                                        AccumulatorExpressions.avg(Expressions.field("l_extendedprice"))
                                )

                                // AVG(l_discount)
                                .field("avg_disc",
                                        AccumulatorExpressions.avg(Expressions.field("l_discount"))
                                )

                                // COUNT(*)
                                .field("count_order",
                                        AccumulatorExpressions.sum(Expressions.value(1))
                                )
                )
                .project(
                        Projection.project()

                                //.include("_id", Expressions.field("_id"))
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
                                .suppressId()
                )
                .sort(
                        Sort.sort().ascending("l_returnflag", "l_linestatus")
                );


        //var p = ((AggregationImpl<LineitemR>)q1).pipeline();
        //System.out.println(p);

        return q1
                .execute(Document.class)
                .toList();
    }


    /**
     * ### Q2) Minimum Cost Supplier Query
     *
     * //This query finds which supplier should be selected to place an order for a given part in a given region
     * ```sql
     * SELECT
     *   s.s_acctbal,
     *   s.s_name,
     *   n.n_name,
     *   p.p_partkey,
     *   p.p_mfgr,
     *   s.s_address,
     *   s.s_phone,
     *   s.s_comment
     * FROM
     *   part p,
     *   supplier s,
     *   partsupp ps,
     *   nation n,
     *   region r
     * WHERE
     *   p.p_partkey = ps.ps_partkey
     *   AND s.s_suppkey = ps.ps_suppkey
     *   AND p.p_size = 15
     *   AND p.p_type LIKE '%BRASS'
     *   AND s.s_nationkey = n.n_nationkey
     *   AND n.n_regionkey = r.r_regionkey
     *   AND r.r_name = 'EUROPE'
     *   AND ps.ps_supplycost = (
     *     SELECT MIN(ps.ps_supplycost)
     *     FROM
     *       partsupp ps,
     *       supplier s,
     *       nation n,
     *       region r
     *     WHERE
     *       p.p_partkey = ps.ps_partkey
     *       AND s.s_suppkey = ps.ps_suppkey
     *       AND s.s_nationkey = n.n_nationkey
     *       AND n.n_regionkey = r.r_regionkey
     *       AND r.r_name = 'EUROPE'
     *   )
     * ORDER BY
     *   s.s_acctbal DESC,
     *   n.n_name,
     *   s.s_name,
     *   p.p_partkey
     * ```
     */
    public static List<Document> Q2(Datastore datastore){
        var q2 = datastore.aggregate(PartR.class)
                // Filter part conditions
                .match(
                        Filters.eq("p_size", 15),
                        regex("p_type","BRASS$")
                )
                // Join with partsupp
                .lookup(
                        Lookup.lookup(PartsuppR.class)
                                .localField("_id")
                                .foreignField("ps_partkey")
                                .as("ps")
                )
                .unwind(Unwind.unwind("ps"))
                // Join with supplier
                .lookup(
                        Lookup.lookup(SupplierR.class)
                                .localField("ps.ps_suppkey")
                                .foreignField("_id")
                                .as("s")
                )
                .unwind(Unwind.unwind("s"))
                // Join with nation
                .lookup(
                        Lookup.lookup(NationR.class)
                                .localField("s.s_nationkey")
                                .foreignField("_id")
                                .as("n")
                )
                .unwind(Unwind.unwind("n"))
                // Join with region
                .lookup(
                        Lookup.lookup(RegionR.class)
                                .localField("n.n_regionkey")
                                .foreignField("_id")
                                .as("r")
                )
                .unwind(Unwind.unwind("r"))
                // Filter region
                .match(
                        Filters.eq("r.r_name", "EUROPE")
                )
                // Find minimum supply cost per part
                .group(
                        Group.group(
                                        Group.id(
                                            Expressions.field("_id")
                                        )

                        )

                                .field("minSupplyCost",
                                        AccumulatorExpressions.min(Expressions.field("ps.ps_supplycost"))
                                )
                                .field("docs",
                                        AccumulatorExpressions.push(Expressions.field("$$ROOT")))
                )
                // Keep only docs with min supply cost
                .unwind(Unwind.unwind("docs"))
                .match(
                        expr(
                                ComparisonExpressions.eq(
                                        Expressions.field("docs.ps.ps_supplycost"),
                                        Expressions.field("minSupplyCost")
                                )
                        )
                )
                .replaceRoot(ReplaceRoot.replaceRoot(Expressions.field("docs")))

                .project(
                        Projection.project()

                                .include("s_acctbal", Expressions.field("s.s_acctbal"))
                                .include("s_name", Expressions.field("s.s_name"))
                                .include("n_name", Expressions.field("n.n_name"))
                                .include("p_partkey")
                                .include("p_mfgr")
                                .include("s_address", Expressions.field("s.s_address"))
                                .include("s_phone", Expressions.field("s.s_phone"))
                                .include("s_comment", Expressions.field("s.s_comment"))
                                .suppressId()
                )
                .sort(
                        Sort.sort()
                                .descending("s_acctbal")
                                .ascending("n_name", "s_name", "p_partkey")
                )


                .execute(Document.class)
                .toList();

        return q2;
    }


    /**
     *
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
    public static List<Document> Q3(Datastore datastore){
        var q3 = datastore.aggregate(CustomerR.class)
                // Filter part conditions
                .match(
                        Filters.eq("c_mktsegment", "BUILDING")
                )

                .lookup(
                        Lookup.lookup(OrdersR.class)
                                .localField("_id")
                                .foreignField("o_custkey")
                                .as("orders")
                )
                .unwind(Unwind.unwind("orders"))

                .match(
                        Filters.lt("orders.o_orderdate", LocalDate.parse("1995-03-15"))
                )

                .lookup(
                        Lookup.lookup(LineitemR.class)
                                .localField("orders._id")
                                .foreignField("l_orderkey")
                                .as("lineitems")
                )
                .unwind(Unwind.unwind("lineitems"))

                .match(
                        Filters.gt("lineitems.l_shipdate", LocalDate.parse("1995-03-15"))
                )

                .group(
                        Group.group(
                                        Group.id(
                                                Expressions.document()
                                                        .field("l_orderkey", Expressions.field("lineitems.l_orderkey"))
                                                        .field("o_orderdate", Expressions.field("orders.o_orderdate"))
                                                        .field("o_shippriority", Expressions.field("orders.o_shippriority"))
                                        )

                                )
                                // SUM(lineitems.l_extendedprice * (1 - lineitems.l_discount)) AS revenue,
                                .field("revenue",
                                        AccumulatorExpressions.sum(
                                                MathExpressions.multiply(
                                                        Expressions.field("lineitems.l_extendedprice"),
                                                        MathExpressions.subtract(
                                                                Expressions.value(1),
                                                                Expressions.field("lineitems.l_discount")
                                                        )
                                                )
                                        )
                                )
                )

                .sort(
                        Sort.sort()
                                .descending("revenue")
                                .ascending("_id.o_orderdate")
                )

                // Limiting to top 10
                .limit(10)

                .project(
                        Projection.project()

                                .include("l_orderkey", Expressions.field("_id.l_orderkey"))
                                .include("revenue")
                                .include("o_orderdate", Expressions.field("_id.o_orderdate"))
                                .include("o_shippriority", Expressions.field("_id.o_shippriority"))
                                .suppressId()
                )

                .execute(Document.class)
                .toList();

        return q3;
    }


    /**
     * ### Q4) Order Priority Checking Query
     *
     * //This query determines how well the order priority system is working and gives an assessment of customer satisfaction
     * ```sql
     * SELECT
     *   o_orderpriority,
     *   COUNT(*) AS order_count
     * FROM
     *   orders
     * WHERE
     *   o_orderdate >= '1993-07-01'
     *   AND o_orderdate < DATE_ADD('1993-07-01', INTERVAL 3 MONTH)
     *   AND EXISTS (
     *     SELECT *
     *     FROM
     *       lineitem
     *     WHERE
     *       l_orderkey = o_orderkey
     *       AND l_commitdate < l_receiptdate
     *   )
     * GROUP BY
     *   o_orderpriority
     * ORDER BY
     *   o_orderpriority
     * ```
     */
    public static List<Document> Q4(Datastore datastore){
        var q4 = datastore.aggregate(OrdersR.class)
                .match(
                        expr(
                                BooleanExpressions.and(
                                        ComparisonExpressions.gte(
                                                Expressions.field("o_orderdate"),
                                                Expressions.value(LocalDate.parse("1993-07-01"))
                                        ),
                                        ComparisonExpressions.lt(
                                                Expressions.field("o_orderdate"),
                                                DateExpressions.dateAdd(
                                                        Expressions.value(LocalDate.parse("1993-07-01")),
                                                        3,
                                                        TimeUnit.MONTH
                                                )
                                        )
                                )


                        )
                )
                .lookup(
                        Lookup.lookup(LineitemR.class)
                                .let("orderkey",  Expressions.field("_id"))
                                .pipeline(
                                        Match.match(
                                                expr(
                                                        BooleanExpressions.and(
                                                                ComparisonExpressions.eq(
                                                                        Expressions.field("l_orderkey"),
                                                                        Expressions.field("$$orderkey")
                                                                ),
                                                                ComparisonExpressions.lt(
                                                                        Expressions.field("l_commitdate"),
                                                                        Expressions.field("l_receiptdate")
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .as("matching_lineitems")
                )
                .match(
                        Filters.ne("matching_lineitems", new Object[0]) // EXISTS equivalent
                )
                .group(
                        Group.group(
                                Group.id(
                                        Expressions.field("o_orderpriority")
                                )
                        )
                                .field("order_count",
                                        AccumulatorExpressions.sum(Expressions.value(1)))
                )
                .sort(
                        Sort.sort()
                                .ascending("_id")
                )

                .execute(Document.class)
                .toList();

        return q4;
    }


    /**
     * ### Q5) Local Supplier Volume Query
     *
     * //This query lists the revenue volume done through local suppliers
     * ```sql
     * SELECT
     *   n.n_name,
     *   SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue
     * FROM
     *   customer c,
     *   orders o,
     *   lineitem l,
     *   supplier s,
     *   nation n,
     *   region r
     * WHERE
     *   c.c_custkey = o.o_custkey
     *   AND l.l_orderkey = o.o_orderkey
     *   AND l.l_suppkey = s.s_suppkey
     *   AND c.c_nationkey = s.s_nationkey
     *   AND s.s_nationkey = n.n_nationkey
     *   AND n.n_regionkey = r.r_regionkey
     *   AND r.r_name = 'ASIA'
     *   AND o.o_orderdate >= '1994-01-01'
     *   AND o.o_orderdate < DATE_ADD('1994-01-01', INTERVAL 1 YEAR)
     * GROUP BY
     *   n.n_name
     * ORDER BY
     *   revenue DESC
     * ```
     */
    public static List<Document> Q5(Datastore datastore){
        var q5 = datastore.aggregate(CustomerR.class)
                // Join orders
                .lookup(
                        Lookup.lookup(OrdersR.class)
                                .localField("_id")
                                .foreignField("o_custkey")
                                .as("orders")
                )
                .unwind(Unwind.unwind("orders"))

                // Filter orders by date
                .match(
                        expr(
                                BooleanExpressions.and(
                                        ComparisonExpressions.gte(
                                                Expressions.field("orders.o_orderdate"),
                                                Expressions.value(LocalDate.parse("1994-01-01"))
                                        ),
                                        ComparisonExpressions.lt(
                                                Expressions.field("orders.o_orderdate"),
                                                DateExpressions.dateAdd(
                                                        Expressions.value(LocalDate.parse("1994-01-01")),
                                                        1,
                                                        TimeUnit.YEAR
                                                )
                                        )
                                )


                        )
                )
                // Join lineitem
                .lookup(
                        Lookup.lookup(LineitemR.class)
                                .localField("orders._id")
                                .foreignField("l_orderkey")
                                .as("lineitems")
                )
                .unwind(Unwind.unwind("lineitems"))

                // Join supplier
                .lookup(
                        Lookup.lookup(SupplierR.class)
                                .localField("lineitems.l_suppkey")
                                .foreignField("_id")
                                .as("supplier")
                )
                .unwind(Unwind.unwind("supplier"))

                // Match local supplier (same nation)
                .match(
                        expr(
                                ComparisonExpressions.eq(
                                        Expressions.field("c_nationkey"),
                                        Expressions.field("supplier.s_nationkey")
                                )
                        )
                )

                // Join nation
                .lookup(
                        Lookup.lookup(NationR.class)
                                .localField("supplier.s_nationkey")
                                .foreignField("_id")
                                .as("nation")
                )
                .unwind(Unwind.unwind("nation"))

                // Join region
                .lookup(
                        Lookup.lookup(RegionR.class)
                                .localField("nation.n_regionkey")
                                .foreignField("_id")
                                .as("region")
                )
                .unwind(Unwind.unwind("region"))

                // Filter region = 'ASIA'
                .match(
                        Filters.eq("region.r_name", "ASIA")
                )

                // Group by nation name and compute revenue
                .group(
                        Group.group(
                                        Group.id(
                                                Expressions.field("nation.n_name")
                                        )

                                )
                                // SUM(lineitems.l_extendedprice * (1 - lineitems.l_discount)) AS revenue,
                                .field("revenue",
                                        AccumulatorExpressions.sum(
                                                MathExpressions.multiply(
                                                        Expressions.field("lineitems.l_extendedprice"),
                                                        MathExpressions.subtract(
                                                                Expressions.value(1),
                                                                Expressions.field("lineitems.l_discount")
                                                        )
                                                )
                                        )
                                )
                )

                // Sort descending
                .sort(
                        Sort.sort()
                                .descending("revenue")
                )

                .project(
                        Projection.project()

                                .include("n_name", Expressions.field("_id"))
                                .include("revenue")
                                .suppressId()
                )


                .execute(Document.class)
                .toList();
        return q5;
    }
}


