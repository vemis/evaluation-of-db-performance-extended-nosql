package cz.cuni.mff.java.kurinna.microservice.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Repository interface for executing SQL queries using MyBatis.
 * This interface defines methods for various types of database operations and queries,
 * including simple selects, joins, aggregations, and TPC-H benchmark queries.
 * Each method is annotated with @Select to specify the SQL query to execute.
 */
@Mapper
public interface UniversalMapper {
    /**
     * A1) Executes a query on non-indexed columns.
     * Retrieves all records from the lineitem table.
     *
     * @return List of maps containing all lineitem records
     */
    @Select("SELECT * FROM lineitem")
    List<Map<String, Object>> a1();

    /**
     * A2) Executes a range query on non-indexed columns.
     * Retrieves orders within a specified date range.
     *
     * @param startDate The start date of the range (inclusive)
     * @param endDate The end date of the range (inclusive)
     * @return List of maps containing orders within the date range
     */
    @Select("SELECT * FROM orders WHERE o_orderdate BETWEEN #{startDate} AND #{endDate}")
    List<Map<String, Object>> a2(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * A3) Executes a query on indexed columns.
     * Retrieves all records from the customer table.
     *
     * @return List of maps containing all customer records
     */
    @Select("SELECT * FROM customer")
    List<Map<String, Object>> a3();

    /**
     * A4) Executes a range query on indexed columns.
     * Retrieves orders within a specified order key range.
     *
     * @param minOrderKey The minimum order key (inclusive)
     * @param maxOrderKey The maximum order key (inclusive)
     * @return List of maps containing orders within the order key range
     */
    @Select("SELECT * FROM orders WHERE o_orderkey BETWEEN #{minOrderKey} AND #{maxOrderKey}")
    List<Map<String, Object>> a4(@Param("minOrderKey") int minOrderKey, @Param("maxOrderKey") int maxOrderKey);

    /**
     * B1) Executes a COUNT aggregation query.
     * Counts orders grouped by month and returns the count for each month.
     *
     * @return List of maps containing order counts by month
     */
    @Select("""
            SELECT COUNT(o.o_orderkey) AS order_count, 
                   DATE_FORMAT(o.o_orderdate, '%Y-%m') AS order_month
            FROM orders o
            GROUP BY order_month
            """)
    List<Map<String, Object>> b1();

    /**
     * B2) Executes a MAX aggregation query.
     * Finds the maximum extended price for line items grouped by ship month.
     *
     * @return List of maps containing maximum prices by ship month
     */
    @Select("""
            SELECT DATE_FORMAT(l.l_shipdate, '%Y-%m') AS ship_month,
                   MAX(l.l_extendedprice) AS max_price
            FROM lineitem l
            GROUP BY ship_month
            """)
    List<Map<String, Object>> b2();

    /**
     * C1) Executes a join query on non-indexed columns.
     * Performs a Cartesian product between customer and orders tables.
     *
     * @return List of maps containing customer names and order details
     */
    @Select("""
            SELECT c.c_name, o.o_orderdate, o.o_totalprice
            FROM customer c, orders o
            LIMIT 1500000
            """)
    List<Map<String, Object>> c1();

    /**
     * C2) Executes a join query on indexed columns.
     * Joins customer and orders tables on customer key.
     *
     * @return List of maps containing customer names and order details
     */
    @Select("""
            SELECT c.c_name, o.o_orderdate, o.o_totalprice
            FROM customer c
            JOIN orders o ON c.c_custkey = o.o_custkey
            """)
    List<Map<String, Object>> c2();

    /**
     * C3) Executes a complex join query (first level).
     * Joins customer, nation, and orders tables.
     *
     * @return List of maps containing customer names, nation names, and order details
     */
    @Select("""
            SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice
            FROM customer c
            JOIN nation n ON c.c_nationkey = n.n_nationkey
            JOIN orders o ON c.c_custkey = o.o_custkey
            """)
    List<Map<String, Object>> c3();

    /**
     * C4) Executes a complex join query (second level).
     * Joins customer, nation, region, and orders tables.
     *
     * @return List of maps containing customer names, nation names, region names, and order details
     */
    @Select("""
            SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice
            FROM customer c
            JOIN nation n ON c.c_nationkey = n.n_nationkey
            JOIN region r ON n.n_regionkey = r.r_regionkey
            JOIN orders o ON c.c_custkey = o.o_custkey
            """)
    List<Map<String, Object>> c4();

    /**
     * C5) Executes a left outer join query.
     * Performs a left outer join between customer and orders tables.
     *
     * @return List of maps containing customer details and their orders (if any)
     */
    @Select("""
            SELECT c.c_custkey, c.c_name, o.o_orderkey, o.o_orderdate
            FROM customer c
            LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey
            """)
    List<Map<String, Object>> c5();

    /**
     * D1) Executes a UNION set operation query.
     * Combines nation keys from both customer and supplier tables.
     *
     * @return List of maps containing unique nation keys from both tables
     */
    @Select("""
            (SELECT c_nationkey AS nationkey FROM customer)
            UNION
            (SELECT s_nationkey AS nationkey FROM supplier)
            """)
    List<Map<String, Object>> d1();

    /**
     * D2) Executes an INTERSECT-like set operation query.
     * Finds customer keys that also exist as supplier keys.
     *
     * @return List of maps containing customer keys that are also supplier keys
     */
    @Select("""
            SELECT DISTINCT c.c_custkey AS custkey
            FROM customer c
            WHERE c.c_custkey IN (
                SELECT s.s_suppkey
                FROM supplier s
            )
            """)
    List<Map<String, Object>> d2();

    /**
     * D3) Executes a DIFFERENCE-like set operation query.
     * Finds customer keys that do not exist as supplier keys.
     *
     * @return List of maps containing customer keys that are not supplier keys
     */
    @Select("""
            SELECT DISTINCT c.c_custkey AS custkey
            FROM customer c
            WHERE c.c_custkey NOT IN (
                SELECT DISTINCT s.s_suppkey
                FROM supplier s
            )
            """)
    List<Map<String, Object>> d3();

    /**
     * E1) Executes a sorting query on non-indexed columns.
     * Retrieves customer information sorted by account balance in descending order.
     *
     * @return List of maps containing customer data sorted by account balance
     */
    @Select("""
            SELECT c_name, c_address, c_acctbal
            FROM customer
            ORDER BY c_acctbal DESC
            """)
    List<Map<String, Object>> e1();

    /**
     * E2) Executes a sorting query on indexed columns.
     * Retrieves order information sorted by order key.
     *
     * @return List of maps containing order data sorted by order key
     */
    @Select("""
            SELECT o_orderkey, o_custkey, o_orderdate, o_totalprice
            FROM orders
            ORDER BY o_orderkey
            """)
    List<Map<String, Object>> e2();

    /**
     * E3) Executes a query with DISTINCT operator.
     * Retrieves unique combinations of nation key and market segment from customers.
     *
     * @return List of maps containing unique nation key and market segment combinations
     */
    @Select("""
            SELECT DISTINCT c_nationkey, c_mktsegment
            FROM customer
            """)
    List<Map<String, Object>> e3();

    /**
     * Executes TPC-H Query 1: Pricing Summary Report.
     * This query reports the amount of business that was billed, shipped, and returned.
     *
     * @param days Number of days to subtract from the cutoff date (1998-12-01)
     * @return List of maps containing pricing summary information
     */
    @Select("""
            SELECT
              l_returnflag,
              l_linestatus,
              SUM(l_quantity)                     AS sum_qty,
              SUM(l_extendedprice)                AS sum_base_price,
              SUM(l_extendedprice * (1 - l_discount))             AS sum_disc_price,
              SUM(l_extendedprice * (1 - l_discount) * (1 + l_tax)) AS sum_charge,
              AVG(l_quantity)                     AS avg_qty,
              AVG(l_extendedprice)                AS avg_price,
              AVG(l_discount)                     AS avg_disc,
              COUNT(*)                            AS count_order
            FROM lineitem
            WHERE l_shipdate <=
              DATE_SUB('1998-12-01', INTERVAL #{days} DAY)
            GROUP BY l_returnflag, l_linestatus
            ORDER BY l_returnflag, l_linestatus
            """)
    List<Map<String, Object>> q1(int days);

    /**
     * Executes TPC-H Query 2: Minimum Cost Supplier.
     * This query finds which supplier should be selected to place an order for a given part in a given region.
     *
     * @param size The size of the part
     * @param type The type of the part (used in LIKE pattern)
     * @param region The name of the region
     * @return List of maps containing supplier information
     */
    @Select("""
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
              AND p.p_size = #{size}
              AND p.p_type LIKE #{type}
              AND s.s_nationkey = n.n_nationkey
              AND n.n_regionkey = r.r_regionkey
              AND r.r_name = #{region}
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
                  AND r.r_name = #{region}
              )
            ORDER BY
              s.s_acctbal DESC,
              n.n_name,
              s.s_name,
              p.p_partkey
            LIMIT 100
            """)
    List<Map<String, Object>> q2(@Param("size") int size, @Param("type") String type, @Param("region") String region);

    /**
     * Executes TPC-H Query 3: Shipping Priority.
     * This query retrieves the 10 unshipped orders with the highest value.
     *
     * @param segment The market segment to consider
     * @param orderDate The cutoff date for orders
     * @param shipDate The cutoff date for shipments
     * @return List of maps containing order information sorted by revenue
     */
    @Select("""
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
              c.c_mktsegment = #{segment}
              AND c.c_custkey = o.o_custkey
              AND l.l_orderkey = o.o_orderkey
              AND o.o_orderdate < #{orderDate}
              AND l.l_shipdate > #{shipDate}
            GROUP BY
              l.l_orderkey,
              o.o_orderdate,
              o.o_shippriority
            ORDER BY
              revenue DESC,
              o.o_orderdate
            LIMIT 10
            """)
    List<Map<String, Object>> q3(@Param("segment") String segment, @Param("orderDate") LocalDate orderDate, @Param("shipDate") LocalDate shipDate);

    /**
     * Executes TPC-H Query 4: Order Priority Checking.
     * This query determines how well the order priority system is working.
     *
     * @param orderDate The start date for the three-month period
     * @return List of maps containing order counts by priority
     */
    @Select("""
            SELECT
              o_orderpriority,
              COUNT(*) AS order_count
            FROM
              orders
            WHERE
              o_orderdate >= #{orderDate}
              AND o_orderdate < DATE_ADD(#{orderDate}, INTERVAL 3 MONTH)
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
            """)
    List<Map<String, Object>> q4(@Param("orderDate") LocalDate orderDate);

    /**
     * Executes TPC-H Query 5: Local Supplier Volume.
     * This query lists the revenue volume done through local suppliers.
     *
     * @param region The name of the region
     * @param orderDate The start date for the one-year period
     * @return List of maps containing revenue by nation
     */
    @Select("""
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
              AND r.r_name = #{region}
              AND o.o_orderdate >= #{orderDate}
              AND o.o_orderdate < DATE_ADD(#{orderDate}, INTERVAL 1 YEAR)
            GROUP BY
              n.n_name
            ORDER BY
              revenue DESC
            """)
    List<Map<String, Object>> q5(@Param("region") String region, @Param("orderDate") LocalDate orderDate);
}
