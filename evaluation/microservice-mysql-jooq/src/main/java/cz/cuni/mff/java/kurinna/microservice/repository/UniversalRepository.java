package cz.cuni.mff.java.kurinna.microservice.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Repository class for executing SQL queries using jOOQ.
 * This class provides methods for various types of database operations and queries,
 * including simple selects, joins, aggregations, and TPC-H benchmark queries.
 * jOOQ provides a type-safe SQL building API that allows for fluent query construction.
 */
@Repository
public class UniversalRepository {
    private final DSLContext dslContext;

    /**
     * Constructs a new UniversalRepository with the specified DSLContext.
     *
     * @param dslContext The jOOQ DSLContext used for building and executing queries
     */
    public UniversalRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    /**
     * A1) Executes a query on non-indexed columns.
     * Retrieves all records from the lineitem table.
     *
     * @return List of maps containing all lineitem records
     */
    public List<Map<String, Object>> a1() {
        String sql = "SELECT * FROM lineitem";
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * A2) Executes a range query on non-indexed columns.
     * Retrieves orders within a specified date range.
     *
     * @param startDate The start date of the range (inclusive)
     * @param endDate The end date of the range (inclusive)
     * @return List of maps containing orders within the date range
     */
    public List<Map<String, Object>> a2(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM orders WHERE o_orderdate BETWEEN ? AND ?";
        return dslContext.fetch(sql, startDate, endDate).intoMaps();
    }

    /**
     * A3) Executes a query on indexed columns.
     * Retrieves all records from the customer table.
     *
     * @return List of maps containing all customer records
     */
    public List<Map<String, Object>> a3() {
        String sql = "SELECT * FROM customer";
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * A4) Executes a range query on indexed columns.
     * Retrieves orders within a specified order key range.
     *
     * @param minOrderKey The minimum order key (inclusive)
     * @param maxOrderKey The maximum order key (inclusive)
     * @return List of maps containing orders within the order key range
     */
    public List<Map<String, Object>> a4(int minOrderKey, int maxOrderKey) {
        String sql = "SELECT * FROM orders WHERE o_orderkey BETWEEN ? AND ?";
        return dslContext.fetch(sql, (long) minOrderKey, (long) maxOrderKey).intoMaps();
    }

    /**
     * B1) Executes a COUNT aggregation query.
     * Counts orders grouped by month and returns the count for each month.
     *
     * @return List of maps containing order counts by month
     */
    public List<Map<String, Object>> b1() {
        String sql = """
            SELECT COUNT(o_orderkey) AS order_count,
                   DATE_FORMAT(o_orderdate, '%Y-%m') AS order_month
            FROM orders
            GROUP BY order_month
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * B2) Executes a MAX aggregation query.
     * Finds the maximum extended price for line items grouped by ship month.
     *
     * @return List of maps containing maximum prices by ship month
     */
    public List<Map<String, Object>> b2() {
        String sql = """
            SELECT DATE_FORMAT(l_shipdate, '%Y-%m') AS ship_month,
                   MAX(l_extendedprice) AS max_price
            FROM lineitem
            GROUP BY ship_month
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * C1) Executes a join query on non-indexed columns.
     * Performs a Cartesian product between customer and orders tables.
     *
     * @return List of maps containing customer names and order details
     */
    public List<Map<String, Object>> c1() {
        String sql = """
            SELECT c_name, o_orderdate, o_totalprice
            FROM customer, orders
            LIMIT 1500000
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * C2) Executes a join query on indexed columns.
     * Joins customer and orders tables on customer key.
     *
     * @return List of maps containing customer names and order details
     */
    public List<Map<String, Object>> c2() {
        String sql = """
            SELECT c_name, o_orderdate, o_totalprice
            FROM customer
            JOIN orders ON c_custkey = o_custkey
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * C3) Executes a complex join query (first level).
     * Joins customer, nation, and orders tables.
     *
     * @return List of maps containing customer names, nation names, and order details
     */
    public List<Map<String, Object>> c3() {
        String sql = """
            SELECT c_name, n_name, o_orderdate, o_totalprice
            FROM customer
            JOIN nation ON c_nationkey = n_nationkey
            JOIN orders ON c_custkey = o_custkey
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * C4) Executes a complex join query (second level).
     * Joins customer, nation, region, and orders tables.
     *
     * @return List of maps containing customer names, nation names, region names, and order details
     */
    public List<Map<String, Object>> c4() {
        String sql = """
            SELECT c_name, n_name, r_name, o_orderdate, o_totalprice
            FROM customer
            JOIN nation ON c_nationkey = n_nationkey
            JOIN region ON n_regionkey = r_regionkey
            JOIN orders ON c_custkey = o_custkey
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * C5) Executes a left outer join query.
     * Performs a left outer join between customer and orders tables.
     *
     * @return List of maps containing customer details and their orders (if any)
     */
    public List<Map<String, Object>> c5() {
        String sql = """
            SELECT c_custkey, c_name, o_orderkey, o_orderdate
            FROM customer
            LEFT OUTER JOIN orders ON c_custkey = o_custkey
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * D1) Executes a UNION set operation query.
     * Combines nation keys from both customer and supplier tables.
     *
     * @return List of maps containing unique nation keys from both tables
     */
    public List<Map<String, Object>> d1() {
        String sql = """
            (SELECT c_nationkey AS nationkey FROM customer)
            UNION
            (SELECT s_nationkey AS nationkey FROM supplier)
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * D2) Executes an INTERSECT-like set operation query.
     * Finds customer keys that also exist as supplier keys.
     *
     * @return List of maps containing customer keys that are also supplier keys
     */
    public List<Map<String, Object>> d2() {
        String sql = """
            SELECT DISTINCT c_custkey AS custkey
            FROM customer
            WHERE c_custkey IN (
                SELECT s_suppkey
                FROM supplier
            )
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * D3) Executes a DIFFERENCE-like set operation query.
     * Finds customer keys that do not exist as supplier keys.
     *
     * @return List of maps containing customer keys that are not supplier keys
     */
    public List<Map<String, Object>> d3() {
        String sql = """
            SELECT DISTINCT c_custkey AS custkey
            FROM customer
            WHERE c_custkey NOT IN (
                SELECT DISTINCT s_suppkey
                FROM supplier
            )
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * E1) Executes a sorting query on non-indexed columns.
     * Retrieves customer information sorted by account balance in descending order.
     *
     * @return List of maps containing customer data sorted by account balance
     */
    public List<Map<String, Object>> e1() {
        String sql = """
            SELECT c_name, c_address, c_acctbal
            FROM customer
            ORDER BY c_acctbal DESC
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * E2) Executes a sorting query on indexed columns.
     * Retrieves order information sorted by order key.
     *
     * @return List of maps containing order data sorted by order key
     */
    public List<Map<String, Object>> e2() {
        String sql = """
            SELECT o_orderkey, o_custkey, o_orderdate, o_totalprice
            FROM orders
            ORDER BY o_orderkey ASC
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * E3) Executes a query with DISTINCT operator.
     * Retrieves unique combinations of nation key and market segment from customers.
     *
     * @return List of maps containing unique nation key and market segment combinations
     */
    public List<Map<String, Object>> e3() {
        String sql = """
            SELECT DISTINCT c_nationkey, c_mktsegment
            FROM customer
            """;
        return dslContext.fetch(sql).intoMaps();
    }

    /**
     * Executes TPC-H Query 1: Pricing Summary Report.
     * This query reports the amount of business that was billed, shipped, and returned.
     * Note: This implementation uses a static value of 90 days instead of the parameterized days.
     *
     * @param days Number of days to subtract from the cutoff date (1998-12-01)
     * @return List of maps containing pricing summary information
     */
    public List<Map<String, Object>> q1(int days) {
        // Using static value 90 instead of parameterized days
        LocalDate cutoff = LocalDate.of(1998, 12, 1).minusDays(90);

        String sql = """
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
            WHERE l_shipdate <= ?
            GROUP BY l_returnflag, l_linestatus
            ORDER BY l_returnflag, l_linestatus
            """;

        return dslContext.fetch(sql, cutoff).intoMaps();
    }

    /**
     * Executes TPC-H Query 2: Minimum Cost Supplier.
     * This query finds which supplier should be selected to place an order for a given part in a given region.
     *
     * @param size The size of the part
     * @param type The type of the part (used in LIKE pattern)
     * @param region The name of the region
     * @return List of maps containing supplier information
     */
    public List<Map<String, Object>> q2(int size, String type, String region) {
        String sql = """
            SELECT
              s.s_acctbal AS acctbal,
              s.s_name AS name,
              n.n_name AS nationName,
              p.p_partkey AS partKey,
              p.p_mfgr AS mfgr,
              s.s_address AS address,
              s.s_phone AS phone,
              s.s_comment AS comment
            FROM
              part p
              JOIN partsupp ps ON p.p_partkey = ps.ps_partkey
              JOIN supplier s ON s.s_suppkey = ps.ps_suppkey
              JOIN nation n ON s.s_nationkey = n.n_nationkey
              JOIN region r ON n.n_regionkey = r.r_regionkey
            WHERE
              p.p_size = ?
              AND p.p_type LIKE ?
              AND r.r_name = ?
              AND ps.ps_supplycost = (
                SELECT MIN(ps.ps_supplycost)
                FROM
                  partsupp ps
                  JOIN supplier s ON s.s_suppkey = ps.ps_suppkey
                  JOIN nation n ON s.s_nationkey = n.n_nationkey
                  JOIN region r ON n.n_regionkey = r.r_regionkey
                WHERE
                  p.p_partkey = ps.ps_partkey
                  AND r.r_name = ?
              )
            ORDER BY
              s.s_acctbal DESC,
              n.n_name ASC,
              s.s_name ASC,
              p.p_partkey ASC
            LIMIT 100
            """;

        return dslContext.fetch(sql, size, type, region, region).intoMaps();
    }

    /**
     * Executes TPC-H Query 3: Shipping Priority.
     * This query retrieves the 10 unshipped orders with the highest value.
     *
     * @param segment The market segment to consider
     * @param orderDate The cutoff date for orders
     * @param shipDate The cutoff date for shipments
     * @return List of maps containing order information sorted by revenue
     */
    public List<Map<String, Object>> q3(String segment, LocalDate orderDate, LocalDate shipDate) {
        String sql = """
            SELECT
              l.l_orderkey AS orderKey,
              SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue,
              o.o_orderdate AS orderDate,
              o.o_shippriority AS shipPriority
            FROM
              customer c
              JOIN orders o ON c.c_custkey = o.o_custkey
              JOIN lineitem l ON l.l_orderkey = o.o_orderkey
            WHERE
              c.c_mktsegment = ?
              AND o.o_orderdate < ?
              AND l.l_shipdate > ?
            GROUP BY
              l.l_orderkey, o.o_orderdate, o.o_shippriority
            ORDER BY
              revenue DESC,
              o.o_orderdate ASC
            LIMIT 10
            """;

        return dslContext.fetch(sql, segment, orderDate, shipDate).intoMaps();
    }

    /**
     * Executes TPC-H Query 4: Order Priority Checking.
     * This query determines how well the order priority system is working.
     *
     * @param orderDate The start date for the three-month period
     * @return List of maps containing order counts by priority
     */
    public List<Map<String, Object>> q4(LocalDate orderDate) {
        LocalDate endDate = orderDate.plusMonths(3);

        String sql = """
            SELECT
              o.o_orderpriority AS orderPriority,
              COUNT(*) AS orderCount
            FROM
              orders o
            WHERE
              o.o_orderdate >= ?
              AND o.o_orderdate < ?
              AND EXISTS (
                SELECT 1
                FROM
                  lineitem l
                WHERE
                  l.l_orderkey = o.o_orderkey
                  AND l.l_commitdate < l.l_receiptdate
              )
            GROUP BY
              o.o_orderpriority
            ORDER BY
              o.o_orderpriority ASC
            """;

        return dslContext.fetch(sql, orderDate, endDate).intoMaps();
    }

    /**
     * Executes TPC-H Query 5: Local Supplier Volume.
     * This query lists the revenue volume done through local suppliers.
     *
     * @param region The name of the region
     * @param orderDate The start date for the one-year period
     * @return List of maps containing revenue by nation
     */
    public List<Map<String, Object>> q5(String region, LocalDate orderDate) {
        LocalDate endDate = orderDate.plusYears(1);

        String sql = """
            SELECT
              n.n_name AS nationName,
              SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue
            FROM
              customer c
              JOIN orders o ON c.c_custkey = o.o_custkey
              JOIN lineitem l ON l.l_orderkey = o.o_orderkey
              JOIN supplier s ON l.l_suppkey = s.s_suppkey
              JOIN nation n ON s.s_nationkey = n.n_nationkey
              JOIN region r ON n.n_regionkey = r.r_regionkey
            WHERE
              r.r_name = ?
              AND o.o_orderdate >= ?
              AND o.o_orderdate < ?
              AND c.c_nationkey = s.s_nationkey
            GROUP BY
              n.n_name
            ORDER BY
              revenue DESC
            """;

        return dslContext.fetch(sql, region, orderDate, endDate).intoMaps();
    }
}
