package cz.cuni.mff.java.microservice.repository;

import cz.cuni.mff.java.microservice.model.relational.*;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class QueryRepository {

    private final MongoTemplate mongoTemplate;

    public QueryRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * A1) Non-Indexed Columns
     * <p>
     * Selects all records from the lineitem table.
     * <pre>SELECT * FROM lineitem;</pre>
     */
    public List<LineitemR> a1() {
        return mongoTemplate.findAll(LineitemR.class);
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
        Query query = new Query().addCriteria(
                Criteria.where("o_orderdate").gte(startDate).lte(endDate)
        );
        return mongoTemplate.find(query, OrdersR.class);
    }

    /**
     * A3) Indexed Columns
     * <p>
     * Selects all records from the customer table.
     * <pre>SELECT * FROM customer;</pre>
     */
    public List<CustomerR> a3() {
        return mongoTemplate.findAll(CustomerR.class);
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
        Query query = new Query().addCriteria(
                Criteria.where("_id").gte(minOrderKey).lte(maxOrderKey)
        );
        return mongoTemplate.find(query, OrdersR.class);
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
        GroupOperation groupOperation = group().and("_id",
                        DateOperators.dateOf("o_orderdate").toString("%Y-%m"))
                .count().as("order_count");

        ProjectionOperation projectionOperation = project()
                .and("_id").as("orderMonth")
                .and("order_count").as("orderCount")
                .andExclude("_id");

        Aggregation aggregation = newAggregation(groupOperation, projectionOperation);

        return mongoTemplate.aggregate(aggregation, OrdersR.class, Document.class).getMappedResults();
    }

    /**
     * B2) MAX
     * <p>
     * Finds the maximum extended price from the lineitem table grouped by ship month.
     * <pre>
     * SELECT DATE_FORMAT(l.l_shipdate, '%Y-%m') AS ship_month,
     *        MAX(l.l_extendedprice) AS max_price
     * FROM lineitem l GROUP BY ship_month;
     * </pre>
     */
    public List<Document> b2() {
        GroupOperation groupOperation = group().and("_id",
                        DateOperators.dateOf("l_shipdate").toString("%Y-%m"))
                .max("l_extendedprice").as("max_price");

        ProjectionOperation projectionOperation = project()
                .and("_id").as("shipMonth")
                .and("max_price").as("maxPrice")
                .andExclude("_id");

        Aggregation aggregation = newAggregation(groupOperation, projectionOperation);

        return mongoTemplate.aggregate(aggregation, LineitemR.class, Document.class).getMappedResults();
    }

    /**
     * C1) Non-Indexed Columns (Cartesian product, always DNF, limited to 1,500,000 rows)
     * <pre>
     * SELECT c.c_name, o.o_orderdate, o.o_totalprice
     * FROM customer c, orders o;
     * </pre>
     */
    public List<Document> c1() {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("ordersR")
                .pipeline()
                .as("ordersR");
        UnwindOperation unwindOperation = unwind("ordersR");

        ProjectionOperation projectionOperation = project()
                .and("c_name").as("c_name")
                .and("ordersR.o_orderdate").as("o_orderdate")
                .and("ordersR.o_totalprice").as("o_totalprice");

        LimitOperation limitOperation = limit(1_500_000);

        Aggregation aggregation = newAggregation(
                lookupOperation, unwindOperation, projectionOperation, limitOperation);

        return mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class).getMappedResults();
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
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("ordersR")
                .localField("_id")
                .foreignField("o_custkey")
                .as("ordersR");
        UnwindOperation unwindOperation = unwind("ordersR");

        ProjectionOperation projectionOperation = project()
                .and("c_name").as("c_name")
                .and("ordersR.o_orderdate").as("o_orderdate")
                .and("ordersR.o_totalprice").as("o_totalprice");

        Aggregation aggregation = newAggregation(lookupOperation, unwindOperation, projectionOperation);

        return mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class).getMappedResults();
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
        LookupOperation lookupOrders = LookupOperation.newLookup()
                .from("ordersR").localField("_id").foreignField("o_custkey").as("ordersR");
        UnwindOperation unwindOrders = unwind("ordersR");

        LookupOperation lookupNation = LookupOperation.newLookup()
                .from("nationR").localField("c_nationkey").foreignField("_id").as("nationR");
        UnwindOperation unwindNation = unwind("nationR");

        ProjectionOperation projection = project()
                .and("c_name").as("c_name")
                .and("nationR.n_name").as("n_name")
                .and("ordersR.o_orderdate").as("o_orderdate")
                .and("ordersR.o_totalprice").as("o_totalprice");

        Aggregation aggregation = newAggregation(
                lookupOrders, unwindOrders, lookupNation, unwindNation, projection);

        return mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class).getMappedResults();
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
        LookupOperation lookupOrders = LookupOperation.newLookup()
                .from("ordersR").localField("_id").foreignField("o_custkey").as("ordersR");
        UnwindOperation unwindOrders = unwind("ordersR");

        LookupOperation lookupNation = LookupOperation.newLookup()
                .from("nationR").localField("c_nationkey").foreignField("_id").as("nationR");
        UnwindOperation unwindNation = unwind("nationR");

        LookupOperation lookupRegion = LookupOperation.newLookup()
                .from("regionR").localField("nationR.n_regionkey").foreignField("_id").as("regionR");
        UnwindOperation unwindRegion = unwind("regionR");

        ProjectionOperation projection = project()
                .and("c_name").as("c_name")
                .and("nationR.n_name").as("n_name")
                .and("regionR.r_name").as("r_name")
                .and("ordersR.o_orderdate").as("o_orderdate")
                .and("ordersR.o_totalprice").as("o_totalprice");

        Aggregation aggregation = newAggregation(
                lookupOrders, unwindOrders, lookupNation, unwindNation,
                lookupRegion, unwindRegion, projection);

        return mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class).getMappedResults();
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
        LookupOperation lookupOrders = LookupOperation.newLookup()
                .from("ordersR").localField("_id").foreignField("o_custkey").as("ordersR");
        // preserveNullAndEmptyArrays = true keeps customers without orders
        UnwindOperation unwindOrders = unwind("ordersR", true);

        ProjectionOperation projection = project()
                .and("_id").as("c_custkey")
                .and("c_name").as("c_name")
                .and("ordersR.o_orderkey").as("o_orderkey")
                .and("ordersR.o_orderdate").as("o_orderdate");

        Aggregation aggregation = newAggregation(lookupOrders, unwindOrders, projection);

        return mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class).getMappedResults();
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
        ProjectionOperation projectCustomer = project()
                .and("c_nationkey").as("nationkey");

        UnionWithOperation unionWithSupplier = UnionWithOperation.unionWith("supplierR")
                .pipeline(
                        project("s_nationkey").and("s_nationkey").as("nationkey").andExclude("_id")
                );

        GroupOperation groupByNationKey = group("nationkey");

        ProjectionOperation finalProjection = project()
                .and("_id").as("nationkey")
                .andExclude("_id");

        Aggregation aggregation = newAggregation(
                projectCustomer, unionWithSupplier, groupByNationKey, finalProjection);

        return mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class).getMappedResults();
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
        LookupOperation lookupSupplier = LookupOperation.newLookup()
                .from("supplierR").localField("_id").foreignField("_id").as("supplierR");

        MatchOperation matchOperation = match(Criteria.where("supplierR").ne(List.of()));

        ProjectionOperation projection = project().and("_id").as("c_custkey");

        Aggregation aggregation = newAggregation(lookupSupplier, matchOperation, projection);

        return mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class).getMappedResults();
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
        LookupOperation lookupSupplier = LookupOperation.newLookup()
                .from("supplierR").localField("_id").foreignField("_id").as("supplierR");

        MatchOperation matchOperation = match(Criteria.where("supplierR").is(List.of()));

        ProjectionOperation projection = project().and("_id").as("c_custkey");

        Aggregation aggregation = newAggregation(lookupSupplier, matchOperation, projection);

        return mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class).getMappedResults();
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
    public List<Document> e1() {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "c_acctbal"));
        query.fields().include("c_name").include("c_address").include("c_acctbal");
        return mongoTemplate.find(query, Document.class, "customerR");
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
        Query query = new Query().with(Sort.by(Sort.Direction.ASC, "_id"));
        query.fields().include("_id").include("o_custkey").include("o_orderdate").include("o_totalprice");
        return mongoTemplate.find(query, OrdersR.class);
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
        GroupOperation groupOperation = group("c_nationkey", "c_mktsegment");

        ProjectionOperation projection = project()
                .and("_id.c_nationkey").as("c_nationkey")
                .and("_id.c_mktsegment").as("c_mktsegment")
                .andExclude("_id");

        Aggregation aggregation = newAggregation(groupOperation, projection);

        return mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class).getMappedResults();
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

        MatchOperation matchOperation = match(Criteria.where("l_shipdate").lte(cutoff));

        AggregationExpression discPrice = ArithmeticOperators.Multiply.valueOf("l_extendedprice")
                .multiplyBy(ArithmeticOperators.Subtract.valueOf(1).subtract("l_discount"));

        AggregationExpression charge = ArithmeticOperators.Multiply.valueOf(discPrice)
                .multiplyBy(ArithmeticOperators.Add.valueOf(1).add("l_tax"));

        GroupOperation groupOperation = group("l_returnflag", "l_linestatus")
                .sum("l_quantity").as("sum_qty")
                .sum("l_extendedprice").as("sum_base_price")
                .sum(discPrice).as("sum_disc_price")
                .sum(charge).as("sum_charge")
                .avg("l_quantity").as("avg_qty")
                .avg("l_extendedprice").as("avg_price")
                .avg("l_discount").as("avg_disc")
                .count().as("count_order");

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

        SortOperation sortOperation = sort(Sort.Direction.ASC, "l_returnflag", "l_linestatus");

        Aggregation aggregation = newAggregation(
                matchOperation, groupOperation, projection, sortOperation);

        return mongoTemplate.aggregate(aggregation, LineitemR.class, Document.class).getMappedResults();
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
        MatchOperation matchPart = match(
                Criteria.where("p_size").is(size)
                        .and("p_type").regex(type.replace("%", "") + "$", "i")
        );

        LookupOperation lookupPartSupp = LookupOperation.newLookup()
                .from("partsuppR").localField("_id").foreignField("ps_partkey").as("ps");
        UnwindOperation unwindPs = unwind("ps");

        LookupOperation lookupSupplier = LookupOperation.newLookup()
                .from("supplierR").localField("ps.ps_suppkey").foreignField("_id").as("s");
        UnwindOperation unwindS = unwind("s");

        LookupOperation lookupNation = LookupOperation.newLookup()
                .from("nationR").localField("s.s_nationkey").foreignField("_id").as("n");
        UnwindOperation unwindN = unwind("n");

        LookupOperation lookupRegion = LookupOperation.newLookup()
                .from("regionR").localField("n.n_regionkey").foreignField("_id").as("r");
        UnwindOperation unwindR = unwind("r");

        MatchOperation matchRegion = match(Criteria.where("r.r_name").is(region));

        // group by part, compute min supply cost and collect full docs via $$ROOT
        AggregationOperation groupMinCost = ctx -> new Document("$group",
                new Document("_id", "$_id")
                        .append("minSupplyCost", new Document("$min", "$ps.ps_supplycost"))
                        .append("docs", new Document("$push", "$$ROOT"))
        );

        UnwindOperation unwindDocs = unwind("docs");

        MatchOperation matchMinCost = match(
                Criteria.expr(
                        ComparisonOperators.Eq.valueOf("docs.ps.ps_supplycost")
                                .equalTo("minSupplyCost")
                )
        );

        ProjectionOperation projection = project()
                .and("docs.s.s_acctbal").as("s_acctbal")
                .and("docs.s.s_name").as("s_name")
                .and("docs.n.n_name").as("n_name")
                .and("docs._id").as("p_partkey")
                .and("docs.p_mfgr").as("p_mfgr")
                .and("docs.s.s_address").as("s_address")
                .and("docs.s.s_phone").as("s_phone")
                .and("docs.s.s_comment").as("s_comment")
                .andExclude("_id");

        SortOperation sort = sort(Sort.by(
                Sort.Order.desc("s_acctbal"),
                Sort.Order.asc("n_name"),
                Sort.Order.asc("s_name"),
                Sort.Order.asc("p_partkey")
        ));

        Aggregation aggregation = newAggregation(
                matchPart,
                lookupPartSupp, unwindPs,
                lookupSupplier, unwindS,
                lookupNation, unwindN,
                lookupRegion, unwindR,
                matchRegion,
                groupMinCost,
                unwindDocs,
                matchMinCost,
                projection,
                sort
        );

        return mongoTemplate.aggregate(aggregation, PartR.class, Document.class).getMappedResults();
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
        MatchOperation matchCustomer = match(Criteria.where("c_mktsegment").is(segment));

        LookupOperation lookupOrders = LookupOperation.newLookup()
                .from("ordersR").localField("_id").foreignField("o_custkey").as("ordersR");
        UnwindOperation unwindOrders = unwind("ordersR");

        MatchOperation matchOrderDate = match(Criteria.where("ordersR.o_orderdate").lt(orderDate));

        LookupOperation lookupLineitem = LookupOperation.newLookup()
                .from("lineitemR").localField("ordersR._id").foreignField("l_orderkey").as("lineitemR");
        UnwindOperation unwindLineitem = unwind("lineitemR");

        MatchOperation matchShipDate = match(Criteria.where("lineitemR.l_shipdate").gt(shipDate));

        GroupOperation group = group("lineitemR.l_orderkey", "ordersR.o_orderdate", "ordersR.o_shippriority")
                .sum(ArithmeticOperators.Multiply.valueOf("lineitemR.l_extendedprice")
                        .multiplyBy(
                                ArithmeticOperators.Subtract.valueOf(1).subtract("lineitemR.l_discount")
                        )).as("revenue");

        ProjectionOperation project = project()
                .and("_id.l_orderkey").as("l_orderkey")
                .and("_id.o_orderdate").as("o_orderdate")
                .and("_id.o_shippriority").as("o_shippriority")
                .and("revenue").as("revenue")
                .andExclude("_id");

        SortOperation sort = sort(Sort.by(
                Sort.Order.desc("revenue"),
                Sort.Order.asc("o_orderdate")
        ));

        LimitOperation limit = limit(10);

        Aggregation aggregation = newAggregation(
                matchCustomer,
                lookupOrders, unwindOrders, matchOrderDate,
                lookupLineitem, unwindLineitem, matchShipDate,
                group, project, sort, limit
        );

        return mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class).getMappedResults();
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

        MatchOperation matchOrders = match(
                Criteria.where("o_orderdate").gte(orderDate).lt(orderDateEnd)
        );

        // pipeline lookup: only return lineitems where l_commitdate < l_receiptdate
        AggregationOperation lookupLineitems = ctx -> new Document("$lookup",
                new Document("from", "lineitemR")
                        .append("let", new Document("order_id", "$_id"))
                        .append("pipeline", List.of(
                                new Document("$match", new Document("$expr", new Document("$and", List.of(
                                        new Document("$eq", List.of("$l_orderkey", "$$order_id")),
                                        new Document("$lt", List.of("$l_commitdate", "$l_receiptdate"))
                                ))))
                        ))
                        .append("as", "matching_lineitems")
        );

        MatchOperation matchNonEmpty = match(
                Criteria.where("matching_lineitems").ne(List.of())
        );

        GroupOperation group = group("o_orderpriority").count().as("order_count");

        ProjectionOperation project = project()
                .and("_id").as("o_orderpriority")
                .and("order_count").as("order_count")
                .andExclude("_id");

        SortOperation sort = sort(Sort.by("o_orderpriority"));

        Aggregation aggregation = newAggregation(
                matchOrders, lookupLineitems, matchNonEmpty, group, project, sort);

        return mongoTemplate.aggregate(aggregation, OrdersR.class, Document.class).getMappedResults();
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

        LookupOperation lookupOrders = LookupOperation.newLookup()
                .from("ordersR").localField("_id").foreignField("o_custkey").as("ordersR");
        UnwindOperation unwindOrders = unwind("ordersR");

        MatchOperation matchOrderDate = match(
                Criteria.where("ordersR.o_orderdate").gte(orderDate).lt(orderDateEnd)
        );

        LookupOperation lookupLineitem = LookupOperation.newLookup()
                .from("lineitemR").localField("ordersR._id").foreignField("l_orderkey").as("lineitemR");
        UnwindOperation unwindLineitem = unwind("lineitemR");

        LookupOperation lookupSupplier = LookupOperation.newLookup()
                .from("supplierR").localField("lineitemR.l_suppkey").foreignField("_id").as("supplierR");
        UnwindOperation unwindSupplier = unwind("supplierR");

        // local supplier: c_nationkey == s_nationkey
        MatchOperation matchLocalSupplier = match(
                Criteria.expr(
                        ComparisonOperators.Eq.valueOf("c_nationkey")
                                .equalTo("supplierR.s_nationkey")
                )
        );

        LookupOperation lookupNation = LookupOperation.newLookup()
                .from("nationR").localField("supplierR.s_nationkey").foreignField("_id").as("nationR");
        UnwindOperation unwindNation = unwind("nationR");

        LookupOperation lookupRegion = LookupOperation.newLookup()
                .from("regionR").localField("nationR.n_regionkey").foreignField("_id").as("regionR");
        UnwindOperation unwindRegion = unwind("regionR");

        MatchOperation matchRegion = match(Criteria.where("regionR.r_name").is(region));

        GroupOperation groupByNation = group("nationR.n_name")
                .sum(ArithmeticOperators.Multiply.valueOf("lineitemR.l_extendedprice")
                        .multiplyBy(
                                ArithmeticOperators.Subtract.valueOf(1).subtract("lineitemR.l_discount")
                        )).as("revenue");

        ProjectionOperation project = project()
                .and("_id").as("n_name")
                .and("revenue").as("revenue")
                .andExclude("_id");

        SortOperation sort = sort(Sort.by(Sort.Order.desc("revenue")));

        Aggregation aggregation = newAggregation(
                lookupOrders, unwindOrders, matchOrderDate,
                lookupLineitem, unwindLineitem,
                lookupSupplier, unwindSupplier,
                matchLocalSupplier,
                lookupNation, unwindNation,
                lookupRegion, unwindRegion,
                matchRegion,
                groupByNation,
                project,
                sort
        );

        return mongoTemplate.aggregate(aggregation, CustomerR.class, Document.class).getMappedResults();
    }
}
