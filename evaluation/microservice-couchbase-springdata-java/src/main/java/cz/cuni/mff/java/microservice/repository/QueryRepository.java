package cz.cuni.mff.java.microservice.repository;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import cz.cuni.mff.java.microservice.model.relational.CustomerR;
import cz.cuni.mff.java.microservice.model.relational.CustomerROrdersR;
import cz.cuni.mff.java.microservice.model.relational.LineitemR;
import cz.cuni.mff.java.microservice.model.relational.OrdersR;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.QueryCriteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * All relational TPC-H queries implemented via Couchbase N1QL against
 * bucket-main / spring_scope_r.  Dates are stored as epoch milliseconds
 * by Spring Data Couchbase; all date parameters are converted accordingly.
 */
@Repository
public class QueryRepository {

    private static final String R = "`bucket-main`.`spring_scope_r`";

    private final Cluster cluster;
    private final ReactiveCouchbaseTemplate reactiveCouchbaseTemplate;
    private final CustomerROrdersRRepository customerROrdersRRepository;

    public QueryRepository(Cluster cluster,
                           ReactiveCouchbaseTemplate reactiveCouchbaseTemplate,
                           CustomerROrdersRRepository customerROrdersRRepository) {
        this.cluster = cluster;
        this.reactiveCouchbaseTemplate = reactiveCouchbaseTemplate;
        this.customerROrdersRRepository = customerROrdersRRepository;
    }

    // ── A — Selection / Projection ────────────────────────────────────────────

    /**
     * A1) Non-Indexed Columns
     * <p>
     * Selects all records from the lineitem table.
     * <pre>SELECT * FROM lineitem;</pre>
     */
    public List<LineitemR> a1() {
        return reactiveCouchbaseTemplate
                .findByQuery(LineitemR.class)
                .all()
                .toStream().toList();
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
        Query a2 = Query.query(
                QueryCriteria
                        .where("o_orderdate")
                        .between(startDate, endDate)
        );
        return reactiveCouchbaseTemplate
                .findByQuery(OrdersR.class)
                .matching(a2)
                .all()
                .toStream().toList();
    }

    /**
     * A3) Indexed Columns
     * <p>
     * Selects all records from the customer table.
     * <pre>SELECT * FROM customer;</pre>
     */
    public List<CustomerR> a3() {
        return reactiveCouchbaseTemplate
                .findByQuery(CustomerR.class)
                .all()
                .toStream().toList();
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
        Query a4 = Query.query(
                QueryCriteria
                        .where("o_orderkey_field")
                        .between(minOrderKey, maxOrderKey)
        );
        return reactiveCouchbaseTemplate
                .findByQuery(OrdersR.class)
                .matching(a4)
                .all()
                .toStream().toList();
    }

    // ── B — Aggregation ───────────────────────────────────────────────────────

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
    public List<JsonObject> b1() {
        return cluster.query(
                "SELECT COUNT(META(o).id) AS order_count," +
                " MILLIS_TO_STR(o.o_orderdate, 'YYYY-MM') AS order_month" +
                " FROM " + R + ".`OrdersR` AS o" +
                " GROUP BY MILLIS_TO_STR(o.o_orderdate, 'YYYY-MM')")
                .rowsAsObject();
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
    public List<JsonObject> b2() {
        return cluster.query(
                "SELECT MILLIS_TO_STR(l.l_shipdate, 'YYYY-MM') AS ship_month," +
                " MAX(l.l_extendedprice) AS max_price" +
                " FROM " + R + ".`LineitemR` AS l" +
                " GROUP BY MILLIS_TO_STR(l.l_shipdate, 'YYYY-MM')")
                .rowsAsObject();
    }

    // ── C — Joins ─────────────────────────────────────────────────────────────

    /**
     * C1) Non-Indexed Columns
     * <p>
     * Cartesian product of customer and orders — always DNF, limited to 1,500,000 rows.
     * <pre>
     * SELECT c.c_name, o.o_orderdate, o.o_totalprice
     * FROM customer c, orders o;
     * </pre>
     */
    public List<JsonObject> c1() {
        return cluster.query(
                "SELECT c.c_name, o.o_orderdate, o.o_totalprice" +
                " FROM " + R + ".`CustomerR` AS c," +
                " " + R + ".`OrdersR` AS o" +
                " LIMIT 1500000")
                .rowsAsObject();
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
    public List<CustomerROrdersR> c2() {
        return customerROrdersRRepository.findCustomerOrders();
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
    public List<JsonObject> c3() {
        return cluster.query(
                "SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice" +
                " FROM " + R + ".`CustomerR` AS c" +
                " JOIN " + R + ".`NationR` AS n ON c.c_nationkey = META(n).id" +
                " JOIN " + R + ".`OrdersR` AS o ON META(c).id = o.o_custkey")
                .rowsAsObject();
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
    public List<JsonObject> c4() {
        return cluster.query(
                "SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice" +
                " FROM " + R + ".`CustomerR` AS c" +
                " JOIN " + R + ".`NationR` AS n ON c.c_nationkey = META(n).id" +
                " JOIN " + R + ".`RegionR` AS r ON n.n_regionkey = META(r).id" +
                " JOIN " + R + ".`OrdersR` AS o ON META(c).id = o.o_custkey")
                .rowsAsObject();
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
    public List<JsonObject> c5() {
        return cluster.query(
                "SELECT META(c).id AS c_custkey, c.c_name," +
                " META(o).id AS o_orderkey, o.o_orderdate" +
                " FROM " + R + ".`CustomerR` AS c" +
                " LEFT JOIN " + R + ".`OrdersR` AS o ON META(c).id = o.o_custkey")
                .rowsAsObject();
    }

    // ── D — Set Operations ────────────────────────────────────────────────────

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
    public List<JsonObject> d1() {
        return cluster.query(
                "SELECT c.c_nationkey AS nationkey FROM " + R + ".`CustomerR` AS c" +
                " UNION" +
                " SELECT s.s_nationkey AS nationkey FROM " + R + ".`SupplierR` AS s")
                .rowsAsObject();
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
    public List<JsonObject> d2() {
        return cluster.query(
                "SELECT DISTINCT META(c).id AS c_custkey" +
                " FROM " + R + ".`CustomerR` AS c" +
                " WHERE META(c).id IN (" +
                "   SELECT RAW META(s).id FROM " + R + ".`SupplierR` AS s" +
                " )")
                .rowsAsObject();
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
    public List<JsonObject> d3() {
        return cluster.query(
                "SELECT DISTINCT META(c).id AS c_custkey" +
                " FROM " + R + ".`CustomerR` AS c" +
                " WHERE META(c).id NOT IN (" +
                "   SELECT RAW META(s).id FROM " + R + ".`SupplierR` AS s" +
                " )")
                .rowsAsObject();
    }

    // ── E — Result Modification ───────────────────────────────────────────────

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
    public List<JsonObject> e1() {
        return cluster.query(
                "SELECT c.c_name, c.c_address, c.c_acctbal" +
                " FROM " + R + ".`CustomerR` AS c" +
                " ORDER BY c.c_acctbal DESC")
                .rowsAsObject();
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
    public List<JsonObject> e2() {
        return cluster.query(
                "SELECT META(o).id AS o_orderkey, o.o_custkey, o.o_orderdate, o.o_totalprice" +
                " FROM " + R + ".`OrdersR` AS o" +
                " ORDER BY META(o).id ASC")
                .rowsAsObject();
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
    public List<JsonObject> e3() {
        return cluster.query(
                "SELECT DISTINCT c.c_nationkey, c.c_mktsegment" +
                " FROM " + R + ".`CustomerR` AS c")
                .rowsAsObject();
    }

    // ── Q — TPC-H Benchmark Queries ───────────────────────────────────────────

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
    public List<JsonObject> q1(int deltaDays) {
        long cutoff = toMillis(LocalDate.of(1998, 12, 1)) - (long) deltaDays * 86_400_000L;
        return cluster.query(
                "SELECT l.l_returnflag, l.l_linestatus," +
                " SUM(l.l_quantity) AS sum_qty," +
                " SUM(l.l_extendedprice) AS sum_base_price," +
                " SUM(l.l_extendedprice * (1 - l.l_discount)) AS sum_disc_price," +
                " SUM(l.l_extendedprice * (1 - l.l_discount) * (1 + l.l_tax)) AS sum_charge," +
                " AVG(l.l_quantity) AS avg_qty," +
                " AVG(l.l_extendedprice) AS avg_price," +
                " AVG(l.l_discount) AS avg_disc," +
                " COUNT(*) AS count_order" +
                " FROM " + R + ".`LineitemR` AS l" +
                " WHERE l.l_shipdate <= $cutoff" +
                " GROUP BY l.l_returnflag, l.l_linestatus" +
                " ORDER BY l.l_returnflag, l.l_linestatus",
                QueryOptions.queryOptions().parameters(JsonObject.create().put("cutoff", cutoff)))
                .rowsAsObject();
    }

    /**
     * Q2) Minimum Cost Supplier Query
     * <p>
     * Finds which supplier should be selected to place an order for a given part in a given region.
     * Selects the supplier with the minimum supply cost per part matching the given size, type, and region.
     * Uses a derived-table subquery to avoid the Couchbase 1000-doc correlated-subquery limit.
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
    public List<JsonObject> q2(int size, String type, String region) {
        return cluster.query(
                "SELECT s.s_acctbal, s.s_name, n.n_name, META(p).id AS p_partkey," +
                " p.p_mfgr, s.s_address, s.s_phone, s.s_comment" +
                " FROM " + R + ".`PartR` AS p" +
                " JOIN " + R + ".`PartsuppR` AS ps ON META(p).id = ps.ps_partkey" +
                " JOIN (" +
                "   SELECT ps_inner.ps_partkey, MIN(ps_inner.ps_supplycost) AS min_supplycost" +
                "   FROM " + R + ".`PartsuppR` AS ps_inner" +
                "   JOIN " + R + ".`SupplierR` AS s_inner ON META(s_inner).id = ps_inner.ps_suppkey" +
                "   JOIN " + R + ".`NationR` AS n_inner ON s_inner.s_nationkey = META(n_inner).id" +
                "   JOIN " + R + ".`RegionR` AS r_inner ON n_inner.n_regionkey = META(r_inner).id" +
                "   WHERE r_inner.r_name = $region" +
                "   GROUP BY ps_inner.ps_partkey" +
                " ) AS minps ON ps.ps_partkey = minps.ps_partkey AND ps.ps_supplycost = minps.min_supplycost" +
                " JOIN " + R + ".`SupplierR` AS s ON META(s).id = ps.ps_suppkey" +
                " JOIN " + R + ".`NationR` AS n ON s.s_nationkey = META(n).id" +
                " JOIN " + R + ".`RegionR` AS r ON n.n_regionkey = META(r).id" +
                " WHERE p.p_size = $size AND p.p_type LIKE $typeSuffix AND r.r_name = $region" +
                " ORDER BY s.s_acctbal DESC, n.n_name, s.s_name, META(p).id",
                QueryOptions.queryOptions().parameters(JsonObject.create()
                        .put("size", size)
                        .put("typeSuffix", "%" + type)
                        .put("region", region)))
                .rowsAsObject();
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
    public List<JsonObject> q3(String segment, LocalDate orderDate, LocalDate shipDate) {
        long orderMillis = toMillis(orderDate);
        long shipMillis  = toMillis(shipDate);
        return cluster.query(
                "SELECT META(o).id AS l_orderkey," +
                " SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue," +
                " o.o_orderdate, o.o_shippriority" +
                " FROM " + R + ".`CustomerR` AS c" +
                " JOIN " + R + ".`OrdersR` AS o ON META(c).id = o.o_custkey" +
                " JOIN " + R + ".`LineitemR` AS l ON META(o).id = l.l_orderkey" +
                " WHERE c.c_mktsegment = $segment" +
                " AND o.o_orderdate < $orderMillis" +
                " AND l.l_shipdate > $shipMillis" +
                " GROUP BY META(o).id, o.o_orderdate, o.o_shippriority" +
                " ORDER BY revenue DESC, o.o_orderdate" +
                " LIMIT 10",
                QueryOptions.queryOptions().parameters(JsonObject.create()
                        .put("segment", segment)
                        .put("orderMillis", orderMillis)
                        .put("shipMillis", shipMillis)))
                .rowsAsObject();
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
    public List<JsonObject> q4(LocalDate orderDate) {
        long startMillis = toMillis(orderDate);
        long endMillis   = toMillis(orderDate.plusMonths(3));
        return cluster.query(
                "SELECT o.o_orderpriority, COUNT(DISTINCT META(o).id) AS order_count" +
                " FROM " + R + ".`OrdersR` AS o" +
                " JOIN " + R + ".`LineitemR` AS l ON META(o).id = l.l_orderkey" +
                " WHERE o.o_orderdate >= $start AND o.o_orderdate < $end" +
                " AND l.l_commitdate < l.l_receiptdate" +
                " GROUP BY o.o_orderpriority" +
                " ORDER BY o.o_orderpriority",
                QueryOptions.queryOptions().parameters(JsonObject.create()
                        .put("start", startMillis)
                        .put("end", endMillis)))
                .rowsAsObject();
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
    public List<JsonObject> q5(String region, LocalDate orderDate) {
        long startMillis = toMillis(orderDate);
        long endMillis   = toMillis(orderDate.plusYears(1));
        return cluster.query(
                "SELECT n.n_name, SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue" +
                " FROM " + R + ".`CustomerR` AS c" +
                " JOIN " + R + ".`OrdersR` AS o ON META(c).id = o.o_custkey" +
                " JOIN " + R + ".`LineitemR` AS l ON META(o).id = l.l_orderkey" +
                " JOIN " + R + ".`SupplierR` AS s ON META(s).id = l.l_suppkey" +
                " JOIN " + R + ".`NationR` AS n ON s.s_nationkey = META(n).id" +
                " JOIN " + R + ".`RegionR` AS r ON n.n_regionkey = META(r).id" +
                " WHERE c.c_nationkey = s.s_nationkey" +
                " AND r.r_name = $region" +
                " AND o.o_orderdate >= $start AND o.o_orderdate < $end" +
                " GROUP BY n.n_name" +
                " ORDER BY revenue DESC",
                QueryOptions.queryOptions().parameters(JsonObject.create()
                        .put("region", region)
                        .put("start", startMillis)
                        .put("end", endMillis)))
                .rowsAsObject();
    }

    private static long toMillis(LocalDate date) {
        return date.toEpochDay() * 86_400_000L;
    }
}
