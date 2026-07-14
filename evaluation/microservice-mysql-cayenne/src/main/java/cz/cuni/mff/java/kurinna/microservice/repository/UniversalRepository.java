package cz.cuni.mff.java.kurinna.microservice.repository;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SQLSelect;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository class for executing SQL queries using Apache Cayenne framework.
 * This class provides methods for various types of database operations and queries,
 * including simple selects, joins, aggregations, and TPC-H benchmark queries.
 * Each method requires an ObjectContext to execute the query.
 */
@Repository
public class UniversalRepository {
    /**
     * A1) Executes a query on non-indexed columns.
     * Retrieves all records from the lineitem table.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing all lineitem records
     */
    public List<DataRow> a1(ObjectContext context) {
        String sql = "SELECT * FROM lineitem";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * A2) Executes a range query on non-indexed columns.
     * Retrieves orders within a specified date range.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @param startDate The start date of the range (inclusive)
     * @param endDate The end date of the range (inclusive)
     * @return List of DataRow objects containing orders within the date range
     */
    public List<DataRow> a2(ObjectContext context, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM orders WHERE o_orderdate BETWEEN #bind($startDate) AND #bind($endDate)";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", Date.valueOf(startDate));
        parameters.put("endDate", Date.valueOf(endDate));
        query.params(parameters);

        return query.select(context);
    }

    /**
     * A3) Executes a query on indexed columns.
     * Retrieves all records from the customer table.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing all customer records
     */
    public List<DataRow> a3(ObjectContext context) {
        String sql = "SELECT * FROM customer";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * A4) Executes a range query on indexed columns.
     * Retrieves orders within a specified order key range.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @param minOrderKey The minimum order key (inclusive)
     * @param maxOrderKey The maximum order key (inclusive)
     * @return List of DataRow objects containing orders within the order key range
     */
    public List<DataRow> a4(ObjectContext context, int minOrderKey, int maxOrderKey) {
        String sql = "SELECT * FROM orders WHERE o_orderkey BETWEEN #bind($minOrderKey) AND #bind($maxOrderKey)";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("minOrderKey", minOrderKey);
        parameters.put("maxOrderKey", maxOrderKey);
        query.params(parameters);

        return query.select(context);
    }

    /**
     * B1) Executes a COUNT aggregation query.
     * Counts orders grouped by month and returns the count for each month.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing order counts by month
     */
    public List<DataRow> b1(ObjectContext context) {
        String sql = "SELECT COUNT(o.o_orderkey) AS order_count, " +
                "DATE_FORMAT(o.o_orderdate, '%Y-%m') AS order_month " +
                "FROM orders o " +
                "GROUP BY order_month";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * B2) Executes a MAX aggregation query.
     * Finds the maximum extended price for line items grouped by ship month.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing maximum prices by ship month
     */
    public List<DataRow> b2(ObjectContext context) {
        String sql = "SELECT DATE_FORMAT(l.l_shipdate, '%Y-%m') AS ship_month, " +
                "MAX(l.l_extendedprice) AS max_price " +
                "FROM lineitem l " +
                "GROUP BY ship_month";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * C1) Executes a join query on non-indexed columns.
     * Performs a Cartesian product between customer and orders tables.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing customer names and order details
     */
    public List<DataRow> c1(ObjectContext context) {
        String sql = "SELECT c.c_name, o.o_orderdate, o.o_totalprice " +
                "FROM customer c, orders o " +
                "LIMIT 1500000";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * C2) Executes a join query on indexed columns.
     * Joins customer and orders tables on customer key.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing customer names and order details
     */
    public List<DataRow> c2(ObjectContext context) {
        String sql = "SELECT c.c_name, o.o_orderdate, o.o_totalprice " +
                "FROM customer c " +
                "JOIN orders o ON c.c_custkey = o.o_custkey";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * C3) Executes a complex join query (first level).
     * Joins customer, nation, and orders tables.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing customer names, nation names, and order details
     */
    public List<DataRow> c3(ObjectContext context) {
        String sql = "SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice " +
                "FROM customer c " +
                "JOIN nation n ON c.c_nationkey = n.n_nationkey " +
                "JOIN orders o ON c.c_custkey = o.o_custkey";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * C4) Executes a complex join query (second level).
     * Joins customer, nation, region, and orders tables.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing customer names, nation names, region names, and order details
     */
    public List<DataRow> c4(ObjectContext context) {
        String sql = "SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice " +
                "FROM customer c " +
                "JOIN nation n ON c.c_nationkey = n.n_nationkey " +
                "JOIN region r ON n.n_regionkey = r.r_regionkey " +
                "JOIN orders o ON c.c_custkey = o.o_custkey";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * C5) Executes a left outer join query.
     * Performs a left outer join between customer and orders tables.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing customer details and their orders (if any)
     */
    public List<DataRow> c5(ObjectContext context) {
        String sql = "SELECT c.c_custkey, c.c_name, o.o_orderkey, o.o_orderdate " +
                "FROM customer c " +
                "LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * D1) Executes a UNION set operation query.
     * Combines nation keys from both customer and supplier tables.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing unique nation keys from both tables
     */
    public List<DataRow> d1(ObjectContext context) {
        String sql = "(SELECT c_nationkey AS nationkey FROM customer) " +
                "UNION " +
                "(SELECT s_nationkey AS nationkey FROM supplier)";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * D2) Executes an INTERSECT-like set operation query.
     * Finds customer keys that also exist as supplier keys.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing customer keys that are also supplier keys
     */
    public List<DataRow> d2(ObjectContext context) {
        String sql = "SELECT DISTINCT c.c_custkey AS custkey " +
                "FROM customer c " +
                "WHERE c.c_custkey IN (SELECT s.s_suppkey FROM supplier s)";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * D3) Executes a DIFFERENCE-like set operation query.
     * Finds customer keys that do not exist as supplier keys.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing customer keys that are not supplier keys
     */
    public List<DataRow> d3(ObjectContext context) {
        String sql = "SELECT DISTINCT c.c_custkey AS custkey " +
                "FROM customer c " +
                "WHERE c.c_custkey NOT IN (SELECT DISTINCT s.s_suppkey FROM supplier s)";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * E1) Executes a sorting query on non-indexed columns.
     * Retrieves customer information sorted by account balance in descending order.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing customer data sorted by account balance
     */
    public List<DataRow> e1(ObjectContext context) {
        String sql = "SELECT c_name, c_address, c_acctbal " +
                "FROM customer " +
                "ORDER BY c_acctbal DESC";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * E2) Executes a sorting query on indexed columns.
     * Retrieves order information sorted by order key.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing order data sorted by order key
     */
    public List<DataRow> e2(ObjectContext context) {
        String sql = "SELECT o_orderkey, o_custkey, o_orderdate, o_totalprice " +
                "FROM orders " +
                "ORDER BY o_orderkey";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * E3) Executes a query with DISTINCT operator.
     * Retrieves unique combinations of nation key and market segment from customers.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @return List of DataRow objects containing unique nation key and market segment combinations
     */
    public List<DataRow> e3(ObjectContext context) {
        String sql = "SELECT DISTINCT c_nationkey, c_mktsegment " +
                "FROM customer";
        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);
        return query.select(context);
    }

    /**
     * Executes TPC-H Query 1: Pricing Summary Report.
     * This query reports the amount of business that was billed, shipped, and returned.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @param deltaDays Number of days to subtract from the cutoff date (1998-12-01)
     * @return List of DataRow objects containing pricing summary information
     */
    public List<DataRow> q1(ObjectContext context, int deltaDays) {
        LocalDate baseDate = LocalDate.of(1998, 12, 1);
        LocalDate shipDateThreshold = baseDate.minusDays(deltaDays);

        String sql = "SELECT " +
                "    l_returnflag, " +
                "    l_linestatus, " +
                "    SUM(l_quantity) AS sum_qty, " +
                "    SUM(l_extendedprice) AS sum_base_price, " +
                "    SUM(l_extendedprice * (1 - l_discount)) AS sum_disc_price, " +
                "    SUM(l_extendedprice * (1 - l_discount) * (1 + l_tax)) AS sum_charge, " +
                "    AVG(l_quantity) AS avg_qty, " +
                "    AVG(l_extendedprice) AS avg_price, " +
                "    AVG(l_discount) AS avg_disc, " +
                "    COUNT(*) AS count_order " +
                "FROM " +
                "    lineitem " +
                "WHERE " +
                "    l_shipdate <= #bind($ship_date_threshold) " +
                "GROUP BY " +
                "    l_returnflag, " +
                "    l_linestatus " +
                "ORDER BY " +
                "    l_returnflag, " +
                "    l_linestatus";

        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ship_date_threshold", Date.valueOf(shipDateThreshold));
        query.params(parameters);

        return query.select(context);
    }

    /**
     * Executes TPC-H Query 2: Minimum Cost Supplier.
     * This query finds which supplier should be selected to place an order for a given part in a given region.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @param size The size of the part
     * @param type The type of the part (used in LIKE pattern)
     * @param region The name of the region
     * @return List of DataRow objects containing supplier information
     */
    public List<DataRow> q2(ObjectContext context, int size, String type, String region) {
        String sql = "SELECT " +
                "    s.s_acctbal, " +
                "    s.s_name, " +
                "    n.n_name, " +
                "    p.p_partkey, " +
                "    p.p_mfgr, " +
                "    s.s_address, " +
                "    s.s_phone, " +
                "    s.s_comment " +
                "FROM " +
                "    part p, " +
                "    supplier s, " +
                "    partsupp ps, " +
                "    nation n, " +
                "    region r " +
                "WHERE " +
                "    p.p_partkey = ps.ps_partkey " +
                "    AND s.s_suppkey = ps.ps_suppkey " +
                "    AND p.p_size = #bind($size) " +
                "    AND p.p_type LIKE #bind($type) " +
                "    AND s.s_nationkey = n.n_nationkey " +
                "    AND n.n_regionkey = r.r_regionkey " +
                "    AND r.r_name = #bind($region) " +
                "    AND ps.ps_supplycost = ( " +
                "        SELECT MIN(ps.ps_supplycost) " +
                "        FROM " +
                "            partsupp ps, " +
                "            supplier s, " +
                "            nation n, " +
                "            region r " +
                "        WHERE " +
                "            p.p_partkey = ps.ps_partkey " +
                "            AND s.s_suppkey = ps.ps_suppkey " +
                "            AND s.s_nationkey = n.n_nationkey " +
                "            AND n.n_regionkey = r.r_regionkey " +
                "            AND r.r_name = #bind($region) " +
                "    ) " +
                "ORDER BY " +
                "    s.s_acctbal DESC, " +
                "    n.n_name, " +
                "    s.s_name, " +
                "    p.p_partkey ";

        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("size", size);
        parameters.put("type", type);
        parameters.put("region", region);
        query.params(parameters);

        return query.select(context);
    }

    /**
     * Executes TPC-H Query 3: Shipping Priority.
     * This query retrieves the 10 unshipped orders with the highest value.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @param segment The market segment to consider
     * @param orderDate The cutoff date for orders
     * @param shipDate The cutoff date for shipments
     * @return List of DataRow objects containing order information sorted by revenue
     */
    public List<DataRow> q3(ObjectContext context, String segment, LocalDate orderDate, LocalDate shipDate) {
        String sql = "SELECT " +
                "    l.l_orderkey, " +
                "    SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue, " +
                "    o.o_orderdate, " +
                "    o.o_shippriority " +
                "FROM " +
                "    customer c, " +
                "    orders o, " +
                "    lineitem l " +
                "WHERE " +
                "    c.c_mktsegment = #bind($segment) " +
                "    AND c.c_custkey = o.o_custkey " +
                "    AND l.l_orderkey = o.o_orderkey " +
                "    AND o.o_orderdate < #bind($order_date) " +
                "    AND l.l_shipdate > #bind($ship_date) " +
                "GROUP BY " +
                "    l.l_orderkey, " +
                "    o.o_orderdate, " +
                "    o.o_shippriority " +
                "ORDER BY " +
                "    revenue DESC, " +
                "    o.o_orderdate ";

        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("segment", segment);
        parameters.put("order_date", Date.valueOf(orderDate));
        parameters.put("ship_date", Date.valueOf(shipDate));
        query.params(parameters);

        return query.select(context);
    }

    /**
     * Executes TPC-H Query 4: Order Priority Checking.
     * This query determines how well the order priority system is working.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @param orderDate The start date for the three-month period
     * @return List of DataRow objects containing order counts by priority
     */
    public List<DataRow> q4(ObjectContext context, LocalDate orderDate) {
        LocalDate endDate = orderDate.plusMonths(3);

        String sql = "SELECT " +
                "    o_orderpriority, " +
                "    COUNT(*) AS order_count " +
                "FROM " +
                "    orders " +
                "WHERE " +
                "    o_orderdate >= #bind($order_date) " +
                "    AND o_orderdate < #bind($end_date) " +
                "    AND EXISTS ( " +
                "        SELECT * " +
                "        FROM " +
                "            lineitem " +
                "        WHERE " +
                "            l_orderkey = o_orderkey " +
                "            AND l_commitdate < l_receiptdate " +
                "    ) " +
                "GROUP BY " +
                "    o_orderpriority " +
                "ORDER BY " +
                "    o_orderpriority";

        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("order_date", Date.valueOf(orderDate));
        parameters.put("end_date", Date.valueOf(endDate));
        query.params(parameters);

        return query.select(context);
    }

    /**
     * Executes TPC-H Query 5: Local Supplier Volume.
     * This query lists the revenue volume done through local suppliers.
     *
     * @param context The Cayenne ObjectContext to execute the query
     * @param region The name of the region
     * @param orderDate The start date for the one-year period
     * @return List of DataRow objects containing revenue by nation
     */
    public List<DataRow> q5(ObjectContext context, String region, LocalDate orderDate) {
        LocalDate endDate = orderDate.plusYears(1);

        String sql = "SELECT " +
                "    n.n_name, " +
                "    SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue " +
                "FROM " +
                "    customer c, " +
                "    orders o, " +
                "    lineitem l, " +
                "    supplier s, " +
                "    nation n, " +
                "    region r " +
                "WHERE " +
                "    c.c_custkey = o.o_custkey " +
                "    AND l.l_orderkey = o.o_orderkey " +
                "    AND l.l_suppkey = s.s_suppkey " +
                "    AND c.c_nationkey = s.s_nationkey " +
                "    AND s.s_nationkey = n.n_nationkey " +
                "    AND n.n_regionkey = r.r_regionkey " +
                "    AND r.r_name = #bind($region) " +
                "    AND o.o_orderdate >= #bind($order_date) " +
                "    AND o.o_orderdate < #bind($end_date) " +
                "GROUP BY " +
                "    n.n_name " +
                "ORDER BY " +
                "    revenue DESC";

        SQLSelect<DataRow> query = SQLSelect.dataRowQuery(sql);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("region", region);
        parameters.put("order_date", Date.valueOf(orderDate));
        parameters.put("end_date", Date.valueOf(endDate));
        query.params(parameters);

        return query.select(context);
    }
}
