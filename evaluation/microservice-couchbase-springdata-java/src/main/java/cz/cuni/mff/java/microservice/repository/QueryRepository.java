package cz.cuni.mff.java.microservice.repository;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
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

    public QueryRepository(Cluster cluster) {
        this.cluster = cluster;
    }

    // ── A — Selection / Projection ────────────────────────────────────────────

    // A1) SELECT * FROM lineitem
    public List<JsonObject> a1() {
        return cluster.query("SELECT l.* FROM " + R + ".`LineitemR` AS l")
                .rowsAsObject();
    }

    // A2) SELECT * FROM orders WHERE o_orderdate BETWEEN startDate AND endDate
    public List<JsonObject> a2(LocalDate startDate, LocalDate endDate) {
        long start = toMillis(startDate);
        long end   = toMillis(endDate);
        return cluster.query(
                "SELECT o.* FROM " + R + ".`OrdersR` AS o" +
                " WHERE o.o_orderdate BETWEEN $start AND $end",
                QueryOptions.queryOptions().parameters(
                        JsonObject.create().put("start", start).put("end", end)))
                .rowsAsObject();
    }

    // A3) SELECT * FROM customer
    public List<JsonObject> a3() {
        return cluster.query("SELECT c.* FROM " + R + ".`CustomerR` AS c")
                .rowsAsObject();
    }

    // A4) SELECT * FROM orders WHERE o_orderkey BETWEEN minKey AND maxKey
    public List<JsonObject> a4(int minOrderKey, int maxOrderKey) {
        return cluster.query(
                "SELECT o.* FROM " + R + ".`OrdersR` AS o" +
                " WHERE o.o_orderkey_field BETWEEN $min AND $max",
                QueryOptions.queryOptions().parameters(
                        JsonObject.create().put("min", minOrderKey).put("max", maxOrderKey)))
                .rowsAsObject();
    }

    // ── B — Aggregation ───────────────────────────────────────────────────────

    // B1) COUNT(o_orderkey) grouped by order month
    public List<JsonObject> b1() {
        return cluster.query(
                "SELECT COUNT(META(o).id) AS order_count," +
                " MILLIS_TO_STR(o.o_orderdate, 'YYYY-MM') AS order_month" +
                " FROM " + R + ".`OrdersR` AS o" +
                " GROUP BY MILLIS_TO_STR(o.o_orderdate, 'YYYY-MM')")
                .rowsAsObject();
    }

    // B2) MAX(l_extendedprice) grouped by ship month
    public List<JsonObject> b2() {
        return cluster.query(
                "SELECT MILLIS_TO_STR(l.l_shipdate, 'YYYY-MM') AS ship_month," +
                " MAX(l.l_extendedprice) AS max_price" +
                " FROM " + R + ".`LineitemR` AS l" +
                " GROUP BY MILLIS_TO_STR(l.l_shipdate, 'YYYY-MM')")
                .rowsAsObject();
    }

    // ── C — Joins ─────────────────────────────────────────────────────────────

    // C1) Cartesian product — will DNF / OOM (consistent with all implementations)
    public List<JsonObject> c1() {
        return cluster.query(
                "SELECT c.c_name, o.o_orderdate, o.o_totalprice" +
                " FROM " + R + ".`CustomerR` AS c," +
                " " + R + ".`OrdersR` AS o" +
                " LIMIT 1500000")
                .rowsAsObject();
    }

    // C2) Inner join — customer + orders
    public List<JsonObject> c2() {
        return cluster.query(
                "SELECT c.c_name, o.o_orderdate, o.o_totalprice" +
                " FROM " + R + ".`CustomerR` AS c" +
                " JOIN " + R + ".`OrdersR` AS o ON META(c).id = o.o_custkey")
                .rowsAsObject();
    }

    // C3) 3-way join — customer + nation + orders
    public List<JsonObject> c3() {
        return cluster.query(
                "SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice" +
                " FROM " + R + ".`CustomerR` AS c" +
                " JOIN " + R + ".`NationR` AS n ON c.c_nationkey = META(n).id" +
                " JOIN " + R + ".`OrdersR` AS o ON META(c).id = o.o_custkey")
                .rowsAsObject();
    }

    // C4) 4-way join — customer + nation + region + orders
    public List<JsonObject> c4() {
        return cluster.query(
                "SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice" +
                " FROM " + R + ".`CustomerR` AS c" +
                " JOIN " + R + ".`NationR` AS n ON c.c_nationkey = META(n).id" +
                " JOIN " + R + ".`RegionR` AS r ON n.n_regionkey = META(r).id" +
                " JOIN " + R + ".`OrdersR` AS o ON META(c).id = o.o_custkey")
                .rowsAsObject();
    }

    // C5) LEFT OUTER JOIN — customer + orders
    public List<JsonObject> c5() {
        return cluster.query(
                "SELECT META(c).id AS c_custkey, c.c_name," +
                " META(o).id AS o_orderkey, o.o_orderdate" +
                " FROM " + R + ".`CustomerR` AS c" +
                " LEFT JOIN " + R + ".`OrdersR` AS o ON META(c).id = o.o_custkey")
                .rowsAsObject();
    }

    // ── D — Set Operations ────────────────────────────────────────────────────

    // D1) UNION — customer + supplier nation keys
    public List<JsonObject> d1() {
        return cluster.query(
                "SELECT c.c_nationkey AS nationkey FROM " + R + ".`CustomerR` AS c" +
                " UNION" +
                " SELECT s.s_nationkey AS nationkey FROM " + R + ".`SupplierR` AS s")
                .rowsAsObject();
    }

    // D2) INTERSECT simulation via IN
    public List<JsonObject> d2() {
        return cluster.query(
                "SELECT DISTINCT META(c).id AS c_custkey" +
                " FROM " + R + ".`CustomerR` AS c" +
                " WHERE META(c).id IN (" +
                "   SELECT RAW META(s).id FROM " + R + ".`SupplierR` AS s" +
                " )")
                .rowsAsObject();
    }

    // D3) EXCEPT simulation via NOT IN
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

    // E1) Sort by non-indexed column (c_acctbal DESC)
    public List<JsonObject> e1() {
        return cluster.query(
                "SELECT c.c_name, c.c_address, c.c_acctbal" +
                " FROM " + R + ".`CustomerR` AS c" +
                " ORDER BY c.c_acctbal DESC")
                .rowsAsObject();
    }

    // E2) Sort by indexed column (o_orderkey ASC via META id)
    public List<JsonObject> e2() {
        return cluster.query(
                "SELECT META(o).id AS o_orderkey, o.o_custkey, o.o_orderdate, o.o_totalprice" +
                " FROM " + R + ".`OrdersR` AS o" +
                " ORDER BY META(o).id ASC")
                .rowsAsObject();
    }

    // E3) DISTINCT nation key + market segment
    public List<JsonObject> e3() {
        return cluster.query(
                "SELECT DISTINCT c.c_nationkey, c.c_mktsegment" +
                " FROM " + R + ".`CustomerR` AS c")
                .rowsAsObject();
    }

    // ── Q — TPC-H Benchmark Queries ───────────────────────────────────────────

    // Q1) Pricing summary — items shipped before (1998-12-01 minus deltaDays)
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

    // Q2) Minimum cost supplier — parts of given size/type in given region
    // Uses a derived-table subquery to avoid the Couchbase 1000-doc correlated-subquery limit.
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

    // Q3) Shipping priority — top 10 unshipped orders by revenue
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

    // Q4) Order priority checking — count orders in a quarter that had late lineitems
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

    // Q5) Local supplier volume — revenue through local suppliers in a region/year
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
