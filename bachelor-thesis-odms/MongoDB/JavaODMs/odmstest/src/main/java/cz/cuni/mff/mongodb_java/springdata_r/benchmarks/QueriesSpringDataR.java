package cz.cuni.mff.mongodb_java.springdata_r.benchmarks;


import cz.cuni.mff.mongodb_java.springdata_r.model.*;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class QueriesSpringDataR {
    /**
     * A1) Non-Indexed Columns
     *
     * This query selects all records from the lineitem table
     * ```sql
     *         SELECT * FROM lineitem;
     * ```
     */
    public static List<LineitemR> A1(MongoTemplate mongoTemplate) {
        List<LineitemR> a1 = mongoTemplate.findAll(LineitemR.class);

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
     */
    public static List<OrdersR> A2(MongoTemplate mongoTemplate) {
        /*List<OrdersR> a2 = datastore
                .find(OrdersR.class)
                .filter(gte("o_orderdate", LocalDate.parse("1996-01-01")), lte("o_orderdate", LocalDate.parse("1996-12-31")))
                .iterator()
                .toList();*/
        Query query = new Query().addCriteria(
                Criteria.where("o_orderdate")
                        .gte(LocalDate.parse("1996-01-01"))
                        .lte(LocalDate.parse("1996-12-31"))
        );
        var a2 = mongoTemplate.find(query, OrdersR.class);

        return a2;
    }

    /**
     * ### A3) Indexed Columns
     *
     * This query selects all records from the customer table
     * ```sql
     * SELECT * FROM customer;
     * ```
     */
    public static List<CustomerR> A3(MongoTemplate mongoTemplate){
        List<CustomerR> a3 = mongoTemplate.findAll(CustomerR.class);

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
     */
    public static List<OrdersR> A4(MongoTemplate mongoTemplate) {
        Query query = new Query().addCriteria(
                Criteria.where("_id")
                        .gte(1000)
                        .lte(50000)
        );
        var a4 = mongoTemplate.find(query, OrdersR.class);

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
    public static List<Document> B1(MongoTemplate mongoTemplate) {

        GroupOperation groupOperation = group().and("_id",
                        DateOperators.dateOf("o_orderdate")
                        .toString("%Y-%m")
                )
                .count().as("order_count");

        ProjectionOperation projectionOperation = project()
                .and("_id").as("orderMonth")
                .and("order_count").as("orderCount")
                .andExclude("_id");

        Aggregation aggregation = newAggregation(
                groupOperation,
                projectionOperation
        );

        return mongoTemplate.aggregate(
                aggregation,
                OrdersR.class,
                Document.class
        ).getMappedResults();
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
     */
    public static List<Document> B2(MongoTemplate mongoTemplate) {

        GroupOperation groupOperation = group().and("_id",
                        DateOperators.dateOf("l_shipdate")
                                .toString("%Y-%m")
                )
                .max("l_extendedprice").as("max_price");

        ProjectionOperation projectionOperation = project()
                .and("_id").as("shipMonth")
                .and("max_price").as("maxPrice")
                .andExclude("_id");

        Aggregation aggregation = newAggregation(
                groupOperation,
                projectionOperation
        );

        return mongoTemplate.aggregate(
                aggregation,
                LineitemR.class,
                Document.class
        ).getMappedResults();
    }

    /**
     * ## C) Joins
     *
     * ### C1) Non-Indexed Columns
     *
     * This query gives customer names, order dates, and total prices for customers
     * ```sql
     * SELECT c.c_name, o.o_orderdate, o.o_totalprice
     * FROM customer c, orders o;
     * ```
     */
    public static List<Document> C1(MongoTemplate mongoTemplate){
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("ordersR")
                //.localField("_id")
                //.foreignField("o_custkey")
                .pipeline()
                .as("ordersR");
        UnwindOperation unwindOperation = Aggregation.unwind("ordersR");

        ProjectionOperation projectionOperation = project()
                //.andExclude("_id")
                .and("c_name").as("c_name")
                .and("ordersR.o_orderdate").as("o_orderdate")
                .and("ordersR.o_totalprice").as("o_totalprice");

        LimitOperation limitOperation = Aggregation.limit(1_500_000);

        Aggregation aggregation = newAggregation(
                lookupOperation,
                unwindOperation,
                projectionOperation,
                limitOperation
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(
                        aggregation,
                        CustomerR.class,
                        Document.class);

        return results.getMappedResults();
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
    public static List<Document> C2(MongoTemplate mongoTemplate) {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("ordersR")
                .localField("_id")
                .foreignField("o_custkey")
                .as("ordersR");

        UnwindOperation unwindOperation = Aggregation.unwind("ordersR");

        ProjectionOperation projectionOperation = project()
                //.andExclude("_id")
                .and("c_name").as("c_name")
                .and("ordersR.o_orderdate").as("o_orderdate")
                .and("ordersR.o_totalprice").as("o_totalprice");

        Aggregation aggregation = newAggregation(
                lookupOperation,
                unwindOperation,
                projectionOperation
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(
                        aggregation,
                        CustomerR.class,
                        Document.class);

        return results.getMappedResults();
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
     * @param mongoTemplate
     * @return
     */
    public static List<Document> C3(MongoTemplate mongoTemplate) {

        // Join customer -> orders
        LookupOperation lookupOrders = LookupOperation.newLookup()
                .from("ordersR")
                .localField("_id")              // c_custkey
                .foreignField("o_custkey")
                .as("ordersR");
        UnwindOperation unwindOrders = Aggregation.unwind("ordersR");

        // Join customer -> nation
        LookupOperation lookupNation = LookupOperation.newLookup()
                .from("nationR")
                .localField("c_nationkey")
                .foreignField("_id")            // n_nationkey
                .as("nationR");
        UnwindOperation unwindNation = Aggregation.unwind("nationR");

        // Final projection
        ProjectionOperation projection = project()
                .and("c_name").as("c_name")
                .and("nationR.n_name").as("n_name")
                .and("ordersR.o_orderdate").as("o_orderdate")
                .and("ordersR.o_totalprice").as("o_totalprice");

        Aggregation aggregation = newAggregation(
                lookupOrders,
                unwindOrders,
                lookupNation,
                unwindNation,
                projection
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(
                        aggregation,
                        CustomerR.class,
                        Document.class
                );

        return results.getMappedResults();
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
     * @param mongoTemplate
     * @return
     */
    public static List<Document> C4(MongoTemplate mongoTemplate) {

        // customer -> orders
        LookupOperation lookupOrders = LookupOperation.newLookup()
                .from("ordersR")
                .localField("_id")              // c_custkey
                .foreignField("o_custkey")
                .as("ordersR");
        UnwindOperation unwindOrders = Aggregation.unwind("ordersR");

        // customer -> nation
        LookupOperation lookupNation = LookupOperation.newLookup()
                .from("nationR")
                .localField("c_nationkey")
                .foreignField("_id")            // n_nationkey
                .as("nationR");
        UnwindOperation unwindNation = Aggregation.unwind("nationR");

        // nation -> region
        LookupOperation lookupRegion = LookupOperation.newLookup()
                .from("regionR")
                .localField("nationR.n_regionkey")
                .foreignField("_id")            // r_regionkey
                .as("regionR");
        UnwindOperation unwindRegion = Aggregation.unwind("regionR");

        // projection
        ProjectionOperation projection = project()
                .and("c_name").as("c_name")
                .and("nationR.n_name").as("n_name")
                .and("regionR.r_name").as("r_name")
                .and("ordersR.o_orderdate").as("o_orderdate")
                .and("ordersR.o_totalprice").as("o_totalprice");

        Aggregation aggregation = newAggregation(
                lookupOrders,
                unwindOrders,
                lookupNation,
                unwindNation,
                lookupRegion,
                unwindRegion,
                projection
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(
                        aggregation,
                        CustomerR.class,
                        Document.class
                );

        return results.getMappedResults();
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
     * @param mongoTemplate
     * @return
     */
    public static List<Document> C5(MongoTemplate mongoTemplate) {

        // LEFT JOIN customer -> orders
        LookupOperation lookupOrders = LookupOperation.newLookup()
                .from("ordersR")
                .localField("_id")              // c_custkey
                .foreignField("o_custkey")
                .as("ordersR");
        // IMPORTANT: preserve customers without orders
        UnwindOperation unwindOrders = Aggregation.unwind("ordersR", true);

        // projection
        ProjectionOperation projection = project()
                .and("_id").as("c_custkey")
                .and("c_name").as("c_name")
                .and("ordersR.o_orderkey").as("o_orderkey")
                .and("ordersR.o_orderdate").as("o_orderdate");

        Aggregation aggregation = newAggregation(
                lookupOrders,
                unwindOrders,
                projection
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(
                        aggregation,
                        CustomerR.class,
                        Document.class
                );

        return results.getMappedResults();
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
     */
    public static List<Document> D1(MongoTemplate mongoTemplate) {
        // Stage 1: project from customerR
        ProjectionOperation projectCustomer =
                Aggregation.project()
                        .and("c_nationkey").as("nationkey");

        // Stage 2: unionWith supplierR
        UnionWithOperation unionWithSupplier =
                UnionWithOperation.unionWith("supplierR")
                        .pipeline(
                                project("s_nationkey")
                                        .and("s_nationkey").as("nationkey")
                                        .andExclude("_id")
                        );

        // Stage 3: remove duplicates via group
        GroupOperation groupByNationKey =
                group("nationkey");

        // Stage 4: reshape output
        ProjectionOperation finalProjection =
                project()
                        .and("_id").as("nationkey")
                        .andExclude("_id");

        Aggregation aggregation = newAggregation(
                projectCustomer,
                unionWithSupplier,
                groupByNationKey,
                finalProjection
        );

        return mongoTemplate.aggregate(
                aggregation,
                CustomerR.class,
                Document.class
        ).getMappedResults();
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
     * @param mongoTemplate
     * @return
     */
    public static List<Document> D2(MongoTemplate mongoTemplate) {

        // customer -> supplier (simulate IN)
        LookupOperation lookupSupplier = LookupOperation.newLookup()
                .from("supplierR")
                .localField("_id")          // c_custkey
                .foreignField("_id")        // s_suppkey
                .as("supplierR");

        // keep only matches (i.e., intersection)
        MatchOperation matchOperation = match(
                Criteria.where("supplierR").ne(List.of())
        );

        // projection (DISTINCT already ensured by _id)
        ProjectionOperation projection = project()
                .and("_id").as("c_custkey");

        Aggregation aggregation = newAggregation(
                lookupSupplier,
                matchOperation,
                projection
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(
                        aggregation,
                        CustomerR.class,
                        Document.class
                );

        return results.getMappedResults();
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
     * @param mongoTemplate
     * @return
     */
    public static List<Document> D3(MongoTemplate mongoTemplate) {

        // customer -> supplier
        LookupOperation lookupSupplier = LookupOperation.newLookup()
                .from("supplierR")
                .localField("_id")          // c_custkey
                .foreignField("_id")        // s_suppkey
                .as("supplierR");

        // keep only NON-matching (difference)
        MatchOperation matchOperation = match(
                Criteria.where("supplierR").is(List.of())
        );

        // projection
        ProjectionOperation projection = project()
                .and("_id").as("c_custkey");

        Aggregation aggregation = newAggregation(
                lookupSupplier,
                matchOperation,
                projection
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(
                        aggregation,
                        CustomerR.class,
                        Document.class
                );

        return results.getMappedResults();
    }

    /*
    * ## E) Result Modification
    */

    /**
     * ### E1) Non-Indexed Columns Sorting
     *
     * This query sorts customer names, addresses, and account balances in descending order of account balance
     * ```sql
     * SELECT c_name, c_address, c_acctbal
     * FROM customer
     * ORDER BY c_acctbal DESC
     * ```
     * @param mongoTemplate
     * @return
     */
    public static List<Document> E1(MongoTemplate mongoTemplate) {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "c_acctbal"));

        query.fields()
                .include("c_name")
                .include("c_address")
                .include("c_acctbal");

        List<Document> result = mongoTemplate.find(query, Document.class, "customerR");

        return result;
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
     * @param mongoTemplate
     * @return
     */
    public static List<OrdersR> E2(MongoTemplate mongoTemplate) {

        Query query = new Query()
                .with(Sort.by(Sort.Direction.ASC, "_id"));

        query.fields()
                .include("_id")
                .include("o_custkey")
                .include("o_orderdate")
                .include("o_totalprice");

        return mongoTemplate.find(query, OrdersR.class);
    }

    /**
     * ### E3) Distinct
     *
     * This query selects distinct nation keys and market segments from the customer table
     * ```sql
     * SELECT DISTINCT c_nationkey, c_mktsegment
     * FROM customer;
     * ```
     * @param mongoTemplate
     * @return
     */
    public static List<Document> E3(MongoTemplate mongoTemplate) {

        // group by both fields (DISTINCT)
        GroupOperation groupOperation = group("c_nationkey", "c_mktsegment");

        // reshape output
        ProjectionOperation projection = project()
                .and("_id.c_nationkey").as("c_nationkey")
                .and("_id.c_mktsegment").as("c_mktsegment")
                .andExclude("_id");

        Aggregation aggregation = newAggregation(
                groupOperation,
                projection
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(
                        aggregation,
                        CustomerR.class,
                        Document.class
                );

        return results.getMappedResults();
    }


    /**
     * ## Advanced Queries
     * ## TPC-H Benchmark Queries
     *
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
     * @param mongoTemplate
     * @return
     */
    public static List<Document> Q1(MongoTemplate mongoTemplate) {

        // WHERE l_shipdate <= '1998-09-02'
        /*MatchOperation matchOperation = match(
                Criteria.where("l_shipdate")
                        .lte(LocalDate.parse("1998-09-02"))
        );*/
        AggregationExpression cutoffDate =
                DateOperators.DateSubtract
                        .subtractValue(90, "day")
                        .fromDate(DateOperators.DateFromString.fromString("1998-12-01"));

        MatchOperation matchOperation = match(
                Criteria.expr(
                        ComparisonOperators.Lte.valueOf("l_shipdate")
                                .lessThanEqualTo(cutoffDate)
                )
        );

        // computed: l_extendedprice * (1 - l_discount)
        AggregationExpression discPrice =
                ArithmeticOperators.Multiply.valueOf("l_extendedprice")
                        .multiplyBy(
                                ArithmeticOperators.Subtract.valueOf(1)
                                        .subtract("l_discount")
                        );

        // computed: l_extendedprice * (1 - l_discount) * (1 + l_tax)
        AggregationExpression charge =
                ArithmeticOperators.Multiply.valueOf(discPrice)
                        .multiplyBy(
                                ArithmeticOperators.Add.valueOf(1)
                                        .add("l_tax")
                        );

        // GROUP BY l_returnflag, l_linestatus
        GroupOperation groupOperation = group("l_returnflag", "l_linestatus")
                .sum("l_quantity").as("sum_qty")
                .sum("l_extendedprice").as("sum_base_price")
                .sum(discPrice).as("sum_disc_price")
                .sum(charge).as("sum_charge")
                .avg("l_quantity").as("avg_qty")
                .avg("l_extendedprice").as("avg_price")
                .avg("l_discount").as("avg_disc")
                .count().as("count_order");

        // reshape output
        ProjectionOperation projection = project()
                .and("_id.l_returnflag").as("l_returnflag")
                .and("_id.l_linestatus").as("l_linestatus")
                .and("sum_qty").as("sum_qty")
                .and("sum_base_price").as("sum_base_price")
                .and("sum_disc_price").as("sum_disc_price")
                .and("sum_charge").as("sum_charge")
                .and("avg_qty").as("avg_qty")
                .and("avg_price").as("avg_price")
                .and("avg_disc").as("avg_disc")
                .and("count_order").as("count_order")
                .andExclude("_id");

        // ORDER BY l_returnflag, l_linestatus
        SortOperation sortOperation = sort(
                Sort.Direction.ASC, "l_returnflag", "l_linestatus"
        );

        Aggregation aggregation = newAggregation(
                matchOperation,
                groupOperation,
                projection,
                sortOperation
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(
                        aggregation,
                        LineitemR.class,
                        Document.class
                );

        return results.getMappedResults();
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
     * @param mongoTemplate
     * @return
     */
    public static List<Document> Q2(MongoTemplate mongoTemplate) {

        // Step 1: filter parts
        MatchOperation matchPart = match(
                Criteria.where("p_size").is(15)
                        .and("p_type").regex("BRASS$", "i") // ends with BRASS, case-insensitive
        );

        // Step 2: join partsupp
        LookupOperation lookupPartSupp = LookupOperation.newLookup()
                .from("partsuppR")
                .localField("_id")        // p_partkey
                .foreignField("ps_partkey")
                .as("partsuppR");
        UnwindOperation unwindPartSupp = unwind("partsuppR");

        // Step 3: join supplier
        LookupOperation lookupSupplier = LookupOperation.newLookup()
                .from("supplierR")
                .localField("partsuppR.ps_suppkey")
                .foreignField("_id")
                .as("supplierR");
        UnwindOperation unwindSupplier = unwind("supplierR");

        // Step 4: join nation
        LookupOperation lookupNation = LookupOperation.newLookup()
                .from("nationR")
                .localField("supplierR.s_nationkey")
                .foreignField("_id")
                .as("nationR");
        UnwindOperation unwindNation = unwind("nationR");

        // Step 5: join region
        LookupOperation lookupRegion = LookupOperation.newLookup()
                .from("regionR")
                .localField("nationR.n_regionkey")
                .foreignField("_id")
                .as("regionR");
        UnwindOperation unwindRegion = unwind("regionR");

        // Step 6: filter region
        MatchOperation matchRegion = match(Criteria.where("regionR.r_name").is("EUROPE"));

        // Step 7: compute min supply cost per part
        GroupOperation groupMinSupplyCost = group("_id") // _id = part _id
                .min("partsuppR.ps_supplycost").as("min_supplycost");

        // Step 8: join back min cost to keep only suppliers with min cost
        LookupOperation lookupMinCost = LookupOperation.newLookup()
                .from("partsuppR")
                .localField("_id")
                .foreignField("ps_partkey")
                .as("allPartSupp");

        UnwindOperation unwindAllPartSupp = unwind("allPartSupp");

        MatchOperation matchMinCost = match(
                Criteria.where("allPartSupp.ps_supplycost").is("$$min_supplycost")
        );

        // Step 9: project required fields
        ProjectionOperation projection = project()
                .and("supplierR.s_acctbal").as("s_acctbal")
                .and("supplierR.s_name").as("s_name")
                .and("nationR.n_name").as("n_name")
                .and("_id").as("p_partkey")
                .and("p_mfgr").as("p_mfgr")
                .and("supplierR.s_address").as("s_address")
                .and("supplierR.s_phone").as("s_phone")
                .and("supplierR.s_comment").as("s_comment");

        // Step 10: sort
        SortOperation sort = sort(
                Sort.by(
                        Sort.Order.desc("s_acctbal"),
                        Sort.Order.asc("n_name"),
                        Sort.Order.asc("s_name"),
                        Sort.Order.asc("p_partkey")
                )
        );

        Aggregation aggregation = newAggregation(
                matchPart,
                lookupPartSupp,
                unwindPartSupp,
                lookupSupplier,
                unwindSupplier,
                lookupNation,
                unwindNation,
                lookupRegion,
                unwindRegion,
                matchRegion,
                // min supply cost aggregation can also be done via $group/$setWindowFields for performance
                projection,
                sort
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, PartR.class, Document.class);

        return results.getMappedResults();
    }


    /**
     * ### Q3) Shipping Priority Query
     *
     * //This query retrieves the 10 unshipped orders with the highest value
     * ```sql
     * SELECT
     *   l.l_orderkey,
     *   SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue,
     *   o.o_orderdate,
     *   o.o_shippriority
     * FROM
     *   customer c,
     *   orders o,
     *   lineitem l
     * WHERE
     *   c.c_mktsegment = 'BUILDING'
     *   AND c.c_custkey = o.o_custkey
     *   AND l.l_orderkey = o.o_orderkey
     *   AND o.o_orderdate < '1995-03-15'
     *   AND l.l_shipdate > '1995-03-15'
     * GROUP BY
     *   l.l_orderkey,
     *   o.o_orderdate,
     *   o.o_shippriority
     * ORDER BY
     *   revenue DESC,
     *   o.o_orderdate
     * ```
     */
    public static List<Document> Q3(MongoTemplate mongoTemplate) {

        // Step 1: filter customers
        MatchOperation matchCustomer = match(Criteria.where("c_mktsegment").is("BUILDING"));

        // Step 2: join orders
        LookupOperation lookupOrders = LookupOperation.newLookup()
                .from("ordersR")
                .localField("_id")       // c_custkey
                .foreignField("o_custkey")
                .as("ordersR");

        UnwindOperation unwindOrders = unwind("ordersR");

        // Step 3: filter order date
        MatchOperation matchOrderDate = match(Criteria.where("ordersR.o_orderdate")
                .lt(LocalDate.parse("1995-03-15")));

        // Step 4: join lineitem
        LookupOperation lookupLineitem = LookupOperation.newLookup()
                .from("lineitemR")
                .localField("ordersR._id")  // o_orderkey
                .foreignField("l_orderkey")
                .as("lineitemR");

        UnwindOperation unwindLineitem = unwind("lineitemR");

        // Step 5: filter lineitem ship date
        MatchOperation matchShipDate = match(Criteria.where("lineitemR.l_shipdate")
                .gt(LocalDate.parse("1995-03-15")));

        // Step 6: compute revenue and group
        GroupOperation group = group("lineitemR.l_orderkey", "ordersR.o_orderdate", "ordersR.o_shippriority")
                .sum(ArithmeticOperators.Multiply.valueOf("lineitemR.l_extendedprice")
                        .multiplyBy(
                                ArithmeticOperators.Subtract.valueOf(1)
                                        .subtract("lineitemR.l_discount")
                        )).as("revenue");

        // Step 7: project grouped fields
        ProjectionOperation project = project()
                .and("_id").as("l_orderkey")
                .and("ordersR.o_orderdate").as("o_orderdate")
                .and("ordersR.o_shippriority").as("o_shippriority")
                .and("revenue").as("revenue")
                .andExclude("_id");

        // Step 8: sort and limit
        SortOperation sort = sort(Sort.by(
                Sort.Order.desc("revenue"),
                Sort.Order.asc("o_orderdate")
        ));

        LimitOperation limit = limit(10);

        // Step 9: build aggregation
        Aggregation aggregation = newAggregation(
                matchCustomer,
                lookupOrders,
                unwindOrders,
                matchOrderDate,
                lookupLineitem,
                unwindLineitem,
                matchShipDate,
                group,
                project,
                sort,
                limit
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class);

        return results.getMappedResults();
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
     * @param mongoTemplate
     * @return
     */
    public static List<Document> Q4(MongoTemplate mongoTemplate) {

        // Step 1: filter orders by date
        /////////////////////
        AggregationExpression addDate =
                DateOperators.DateAdd
                        .addValue(3, "month")
                        .toDate(DateOperators.DateFromString.fromString("1993-07-01"));

        MatchOperation matchOrders = match(
                Criteria.expr(
                        BooleanOperators.And.and(
                                ComparisonOperators.Gte.valueOf("o_orderdate")
                                        .greaterThanEqualToValue(DateOperators.DateFromString.fromString("1993-07-01")),
                                ComparisonOperators.Lt.valueOf("o_orderdate")
                                        .lessThan(addDate)
                        )
                )
        );
        /////////////////////



        // Step 2: lookup lineitems
        LookupOperation lookupLineitem = LookupOperation.newLookup()
                .from("lineitemR")
                .localField("_id")          // o_orderkey
                .foreignField("l_orderkey")
                .as("lineitems");

        // Step 3: filter orders with at least one matching lineitem
        MatchOperation matchExists = match(
                Criteria.expr(
                        ComparisonOperators.Lt.valueOf("lineitems.l_commitdate")
                                        .lessThan("lineitems.l_receiptdate")
                )
        );

        // Step 4: group by o_orderpriority and count
        GroupOperation group = group("o_orderpriority")
                .count().as("order_count");

        // Step 5: project for clean output
        ProjectionOperation project = project()
                .and("_id").as("o_orderpriority")
                .and("order_count").as("order_count")
                .andExclude("_id");

        // Step 6: sort by o_orderpriority
        SortOperation sort = sort(Sort.by("o_orderpriority"));

        // Build aggregation
        Aggregation aggregation = newAggregation(
                matchOrders,
                lookupLineitem,
                matchExists,
                group,
                project,
                sort
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, OrdersR.class, Document.class);

        return results.getMappedResults();
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
     * @param mongoTemplate
     * @return
     */
    public static List<Document> Q5(MongoTemplate mongoTemplate) {

        // Step 1: join orders
        LookupOperation lookupOrders = LookupOperation.newLookup()
                .from("ordersR")
                .localField("_id")      // c_custkey
                .foreignField("o_custkey")
                .as("ordersR");

        UnwindOperation unwindOrders = unwind("ordersR");

        // Step 2: join lineitem
        LookupOperation lookupLineitem = LookupOperation.newLookup()
                .from("lineitemR")
                .localField("ordersR._id")  // o_orderkey
                .foreignField("l_orderkey")
                .as("lineitemR");

        UnwindOperation unwindLineitem = unwind("lineitemR");

        // Step 3: join supplier
        LookupOperation lookupSupplier = LookupOperation.newLookup()
                .from("supplierR")
                .localField("lineitemR.l_suppkey")
                .foreignField("_id")
                .as("supplierR");

        UnwindOperation unwindSupplier = unwind("supplierR");

        // Step 4: local suppliers (c_nationkey == s_nationkey)
        MatchOperation matchLocalSupplier = match(
                //Criteria.where("c_nationkey").exists(true)//is("$$supplierR.s_nationkey")

                Criteria.expr(
                        ComparisonOperators.Eq.valueOf("c_nationkey")
                                .equalTo("supplierR.s_nationkey")
                )
        );

        // Step 5: join nation
        LookupOperation lookupNation = LookupOperation.newLookup()
                .from("nationR")
                .localField("supplierR.s_nationkey")
                .foreignField("_id")
                .as("nationR");

        UnwindOperation unwindNation = unwind("nationR");

        // Step 6: join region
        LookupOperation lookupRegion = LookupOperation.newLookup()
                .from("regionR")
                .localField("nationR.n_regionkey")
                .foreignField("_id")
                .as("regionR");
        UnwindOperation unwindRegion = unwind("regionR");

        // Step 7: filter region and order date
        /*MatchOperation matchRegionDate = match(
                Criteria.where("regionR.r_name").is("ASIA")
                        .and("ordersR.o_orderdate")
                        .gte(LocalDate.parse("1994-01-01"))
                        .lt(LocalDate.parse("1995-01-01"))
        );*/
        /////////////////////
        AggregationExpression addDate =
                DateOperators.DateAdd
                        .addValue(1, "year")
                        .toDate(DateOperators.DateFromString.fromString("1994-01-01"));

        MatchOperation matchRegionDate = match(
                Criteria.expr(
                        BooleanOperators.And.and(
                                ComparisonOperators.Gte.valueOf("ordersR.o_orderdate")
                                        .greaterThanEqualToValue(DateOperators.DateFromString.fromString("1994-01-01")),
                                ComparisonOperators.Lt.valueOf("ordersR.o_orderdate")
                                        .lessThan(addDate)
                        )
                )
        );
        /// ///////////////////

        // Step 8: compute revenue and group by nation name
        GroupOperation groupByNation = group("nationR.n_name")
                .sum(ArithmeticOperators.Multiply.valueOf("lineitemR.l_extendedprice")
                        .multiplyBy(
                                ArithmeticOperators.Subtract.valueOf(1)
                                        .subtract("lineitemR.l_discount")
                        )
                ).as("revenue");

        // Step 9: project final fields
        ProjectionOperation project = project()
                .and("_id").as("n_name")
                .and("revenue").as("revenue")
                .andExclude("_id");

        // Step 10: sort by revenue descending
        SortOperation sort = sort(Sort.by(Sort.Order.desc("revenue")));

        Aggregation aggregation = newAggregation(
                lookupOrders,
                unwindOrders,
                lookupLineitem,
                unwindLineitem,
                lookupSupplier,
                unwindSupplier,
                matchLocalSupplier,
                lookupNation,
                unwindNation,
                lookupRegion,
                unwindRegion,
                matchRegionDate,
                groupByNation,
                project,
                sort
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class);

        return results.getMappedResults();
    }

}
