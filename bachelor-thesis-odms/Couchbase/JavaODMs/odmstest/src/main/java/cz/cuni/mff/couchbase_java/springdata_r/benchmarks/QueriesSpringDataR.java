package cz.cuni.mff.couchbase_java.springdata_r.benchmarks;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import cz.cuni.mff.couchbase_java.springdata_r.models.CustomerR;
import cz.cuni.mff.couchbase_java.springdata_r.models.CustomerROrdersR;
import cz.cuni.mff.couchbase_java.springdata_r.models.LineitemR;
import cz.cuni.mff.couchbase_java.springdata_r.models.OrdersR;
import cz.cuni.mff.couchbase_java.springdata_r.repositories.CustomerROrdersRRepository;
import cz.cuni.mff.couchbase_java.springdata_r.repositories.OrdersRRepository;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.ExecutableFindByQueryOperation;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import org.springframework.data.couchbase.core.ReactiveFindByQueryOperation;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.QueryCriteria;


import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class QueriesSpringDataR {
    /**
     * A1) Non-Indexed Columns
     *
     * This query selects all records from the lineitem table
     * ```sql
     *         SELECT * FROM lineitem;
     * ```
     */
    public static List<LineitemR> A1(ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        //QueryOptions queryOptions = QueryOptions.queryOptions().timeout(Duration.ofMinutes(5));

        List<LineitemR> a1 = reactiveCouchbaseTemplate
                .findByQuery(LineitemR.class)
                //.withOptions(queryOptions) // Because it takes too long
                .all().toStream().toList();
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
    public static List<OrdersR> A2(ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        Query a2 = Query.query(
                QueryCriteria
                        .where("o_orderdate")
                        .between( LocalDate.parse("1996-01-01"), LocalDate.parse("1996-12-31"))
        );


        return reactiveCouchbaseTemplate
                .findByQuery(OrdersR.class)
                .matching(a2)
                .all()
                .toStream().toList();
    }


    /**
     *
     ### A3) Indexed Columns

     This query selects all records from the customer table
     ```sql
     SELECT * FROM customer;
     ```
     * @param reactiveCouchbaseTemplate
     * @return
     */
    public static List<CustomerR> A3(ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        //QueryOptions queryOptions = QueryOptions.queryOptions().timeout(Duration.ofMinutes(5));

        List<CustomerR> a3 = reactiveCouchbaseTemplate
                .findByQuery(CustomerR.class)
                //.withOptions(queryOptions) // Because it takes too long
                .all().toStream().toList();
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
     * @param reactiveCouchbaseTemplate
     * @return
     */
    public static List<OrdersR> A4(ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        Query a4 = Query.query(
                QueryCriteria
                        .where("o_orderkey_field")
                        .between( 1000, 50000)
        );


        return reactiveCouchbaseTemplate
                .findByQuery(OrdersR.class)
                .matching(a4)
                .all()
                .toStream().toList();
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
    public static List<JsonObject> B1(OrdersRRepository  ordersRRepository) {
        return ordersRRepository.countOrdersByMonthAsJson();
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
     * @param cluster
     * @return
     */
    public static List<JsonObject> B2(Cluster cluster){
        String query =
                """
                SELECT MILLIS_TO_STR(l.l_shipdate, 'YYYY-MM') AS ship_month,
                    MAX(l.l_extendedprice) AS max_price
                FROM `spring_bucket_r`.`spring_scope_r`.`LineitemR` AS l
                GROUP BY MILLIS_TO_STR(l.l_shipdate, 'YYYY-MM');""";

        return cluster
                .query(query)
                .rowsAsObject();
    }

    // ## C) Joins

    /**
     * ### C1) Non-Indexed Columns
     *
     * This query gives customer names, order dates, and total prices for customers
     * ```sql
     * SELECT c.c_name, o.o_orderdate, o.o_totalprice
     * FROM customer c, orders o;
     * ```
     */
    public static List<JsonObject> C1(Cluster cluster) throws Exception {
        throw new Exception("Not possible to implement in Couchbase");
        /*
        String query =
                """
                SELECT c.c_name, o.o_orderdate, o.o_totalprice
                FROM    `spring_bucket_r`.`spring_scope_r`.`CustomerR` AS c,
                        `spring_bucket_r`.`spring_scope_r`.`OrdersR` AS o
                LIMIT 1500000;""";

        return cluster
                .query(query)
                .rowsAsObject();
                */
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
    public static List<CustomerROrdersR> C2(CustomerROrdersRRepository customerROrdersRRepository) {
        return customerROrdersRRepository.findCustomerOrders();
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
     * @param cluster
     * @return
     */
    public static List<JsonObject> C3(Cluster cluster){
        String query =
                """
                SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice
                FROM `spring_bucket_r`.`spring_scope_r`.`CustomerR` AS c
                JOIN `spring_bucket_r`.`spring_scope_r`.`NationR` AS n
                  ON c.c_nationkey = META(n).id
                JOIN `spring_bucket_r`.`spring_scope_r`.`OrdersR` AS o
                  ON META(c).id = o.o_custkey;""";

        return cluster
                .query(query)
                .rowsAsObject();
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
     * @param cluster
     * @return
     */
    public static List<JsonObject> C4(Cluster cluster){
        String query =
                        """
                        SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice
                        FROM `spring_bucket_r`.`spring_scope_r`.`CustomerR` AS c
                        JOIN `spring_bucket_r`.`spring_scope_r`.`NationR` AS n
                          ON c.c_nationkey = META(n).id
                        JOIN `spring_bucket_r`.`spring_scope_r`.`RegionR` AS r
                          ON n.n_regionkey = META(r).id
                        JOIN `spring_bucket_r`.`spring_scope_r`.`OrdersR` AS o
                          ON META(c).id = o.o_custkey;""";

        return cluster
                .query(query)
                .rowsAsObject();
    }

    /**
     *
     ### C5) Left Outer Join

     This query gives customer names and order dates for all customers, including those without orders
     ```sql
     SELECT c.c_custkey, c.c_name, o.o_orderkey, o.o_orderdate
     FROM customer c
     LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey;
     ```
     * @param cluster
     * @return
     */
    public static List<JsonObject> C5(Cluster cluster){
        String query =
                        """
                        SELECT META(c).id AS c_custkey, c.c_name, META(o).id AS o_orderkey, o.o_orderdate
                        FROM `spring_bucket_r`.`spring_scope_r`.`CustomerR` AS c
                        LEFT JOIN `spring_bucket_r`.`spring_scope_r`.`OrdersR` AS o
                          ON META(c).id = o.o_custkey;""";

        return cluster
                .query(query)
                .rowsAsObject();
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
    public static List<JsonObject> D1(Cluster cluster){
        String query =
         "SELECT c.c_nationkey AS nationkey" +
        " FROM spring_bucket_r.spring_scope_r.CustomerR c" +
        " UNION" +
        " SELECT s.s_nationkey AS nationkey" +
        " FROM spring_bucket_r.spring_scope_r.SupplierR s";

        return cluster
                .query(query)
                .rowsAsObject();
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
     * @param cluster
     * @return
     */
    public static List<JsonObject> D2(Cluster cluster){
        String query =
                """
                        SELECT DISTINCT META(c).id AS c_custkey
                        FROM `spring_bucket_r`.`spring_scope_r`.`CustomerR` AS c
                        WHERE META(c).id IN (
                            SELECT RAW META(s).id
                            FROM `spring_bucket_r`.`spring_scope_r`.`SupplierR` AS s
                        );""";

        return cluster
                .query(query)
                .rowsAsObject();
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
     * @param cluster
     * @return
     */
    public static List<JsonObject> D3(Cluster cluster){
        String query =
                """
                        SELECT DISTINCT META(c).id AS c_custkey
                        FROM `spring_bucket_r`.`spring_scope_r`.`CustomerR` AS c
                        WHERE META(c).id NOT IN (
                            SELECT RAW META(s).id
                            FROM `spring_bucket_r`.`spring_scope_r`.`SupplierR` AS s
                        );""";

        return cluster
                .query(query)
                .rowsAsObject();
    }

    // ## E) Result Modification

    /**
     * ### E1) Non-Indexed Columns Sorting
     *
     * This query sorts customer names, addresses, and account balances in descending order of account balance
     * ```sql
     * SELECT c_name, c_address, c_acctbal
     * FROM customer
     * ORDER BY c_acctbal DESC
     * ```
     * @param cluster
     * @return
     */
    public static List<JsonObject> E1(Cluster cluster){
        String query =
                """
                        SELECT c.c_name, c.c_address, c.c_acctbal
                        FROM `spring_bucket_r`.`spring_scope_r`.`CustomerR` AS c
                        ORDER BY c.c_acctbal DESC;""";

        return cluster
                .query(query)
                .rowsAsObject();
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
    public static List<JsonObject> E2(Cluster cluster){
        String query =
                """
                        SELECT META(o).id AS o_orderkey, o.o_custkey, o.o_orderdate, o.o_totalprice
                        FROM `spring_bucket_r`.`spring_scope_r`.`OrdersR` AS o
                        ORDER BY META(o).id ASC;""";

        return cluster
                .query(query)
                .rowsAsObject();
    }

    /**
     * ### E3) Distinct
     *
     * This query selects distinct nation keys and market segments from the customer table
     * ```sql
     * SELECT DISTINCT c_nationkey, c_mktsegment
     * FROM customer;
     * ```
     * @param cluster
     * @return
     */
    public static List<JsonObject> E3(Cluster cluster){
        String query =
                """
                        SELECT DISTINCT c.c_nationkey, c.c_mktsegment
                        FROM `spring_bucket_r`.`spring_scope_r`.`CustomerR` AS c;""";

        return cluster
                .query(query)
                .rowsAsObject();
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
    public static List<JsonObject> Q1(Cluster cluster){
        String query =
                """
                        SELECT
                          l.l_returnflag,
                          l.l_linestatus,
                          SUM(l.l_quantity) AS sum_qty,
                          SUM(l.l_extendedprice) AS sum_base_price,
                          SUM(l.l_extendedprice * (1 - l.l_discount)) AS sum_disc_price,
                          SUM(l.l_extendedprice * (1 - l.l_discount) * (1 + l.l_tax)) AS sum_charge,
                          AVG(l.l_quantity) AS avg_qty,
                          AVG(l.l_extendedprice) AS avg_price,
                          AVG(l.l_discount) AS avg_disc,
                          COUNT(*) AS count_order
                        FROM `spring_bucket_r`.`spring_scope_r`.`LineitemR` AS l
                        WHERE l.l_shipdate <= MILLIS( DATE_ADD_STR('1998-12-01', -90, 'day') )
                        GROUP BY l.l_returnflag, l.l_linestatus
                        ORDER BY l.l_returnflag, l.l_linestatus;""";

        return cluster
                .query(query)
                .rowsAsObject();
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
     * @param cluster
     * @return
     */
    public static List<JsonObject> Q2(Cluster cluster){
        /**
         * True translation would be this query, but because of the Couchbase subquery 1000 documents limit
         * SELECT
         *      s.s_acctbal,
         *      s.s_name,
         *      n.n_name,
         *      p.p_partkey,
         *      p.p_mfgr,
         *      s.s_address,
         *      s.s_phone,
         *      s.s_comment FROM spring_bucket_r.spring_scope_r.PartR AS p
         * JOIN spring_bucket_r.spring_scope_r.PartsuppR AS ps
         * ON META(p).id = ps.ps_partkey
         * JOIN spring_bucket_r.spring_scope_r.SupplierR AS s
         * ON META(s).id = ps.ps_suppkey
         * JOIN spring_bucket_r.spring_scope_r.NationR AS n
         * ON s.s_nationkey = META(n).id
         * JOIN spring_bucket_r.spring_scope_r.RegionR AS r
         * ON n.n_regionkey = META(r).id WHERE p.p_size = 15
         * AND p.p_type LIKE '%BRASS'
         * AND r.r_name = 'EUROPE'
         * AND ps.ps_supplycost =
         * ( SELECT MIN(ps_inner.ps_supplycost) FROM spring_bucket_r.spring_scope_r.PartsuppR AS ps_inner
         * JOIN spring_bucket_r.spring_scope_r.SupplierR AS s_inner
         * ON META(s_inner).id = ps_inner.ps_suppkey JOIN spring_bucket_r.spring_scope_r.NationR AS n_inner
         * ON s_inner.s_nationkey = META(n_inner).id JOIN spring_bucket_r.spring_scope_r.RegionR AS r_inner
         * ON n_inner.n_regionkey = META(r_inner).id
         * WHERE ps_inner.ps_partkey = META(p).id
         * AND r_inner.r_name = 'EUROPE' )
         * ORDER BY s.s_acctbal DESC, n.n_name, s.s_name, META(p).id;
         */

        String query =
                """
                        SELECT
                          s.s_acctbal,
                          s.s_name,
                          n.n_name,
                          p.p_partkey,
                          p.p_mfgr,
                          s.s_address,
                          s.s_phone,
                          s.s_comment
                        FROM `spring_bucket_r`.`spring_scope_r`.`PartR` AS p
                        JOIN `spring_bucket_r`.`spring_scope_r`.`PartsuppR` AS ps
                          ON META(p).id = ps.ps_partkey
                        JOIN (
                            SELECT ps_inner.ps_partkey,
                                   MIN(ps_inner.ps_supplycost) AS min_supplycost
                            FROM `spring_bucket_r`.`spring_scope_r`.`PartsuppR` AS ps_inner
                            JOIN `spring_bucket_r`.`spring_scope_r`.`SupplierR` AS s_inner
                              ON META(s_inner).id = ps_inner.ps_suppkey
                            JOIN `spring_bucket_r`.`spring_scope_r`.`NationR` AS n_inner
                              ON s_inner.s_nationkey = META(n_inner).id
                            JOIN `spring_bucket_r`.`spring_scope_r`.`RegionR` AS r_inner
                              ON n_inner.n_regionkey = META(r_inner).id
                            WHERE r_inner.r_name = 'EUROPE'
                            GROUP BY ps_inner.ps_partkey
                        ) AS minps
                          ON ps.ps_partkey = minps.ps_partkey
                         AND ps.ps_supplycost = minps.min_supplycost
                        JOIN `spring_bucket_r`.`spring_scope_r`.`SupplierR` AS s
                          ON META(s).id = ps.ps_suppkey
                        JOIN `spring_bucket_r`.`spring_scope_r`.`NationR` AS n
                          ON s.s_nationkey = META(n).id
                        JOIN `spring_bucket_r`.`spring_scope_r`.`RegionR` AS r
                          ON n.n_regionkey = META(r).id
                        WHERE p.p_size = 15
                          AND p.p_type LIKE '%BRASS'
                          AND r.r_name = 'EUROPE'
                        ORDER BY s.s_acctbal DESC, n.n_name, s.s_name, META(p).id;""";

        return cluster
                .query(query)
                .rowsAsObject();
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
     * @param cluster
     * @return
     */
    public static List<JsonObject> Q3(Cluster cluster){
        String query =
                        """
                        SELECT
                          META(o).id AS l_orderkey,
                          SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue,
                          o.o_orderdate,
                          o.o_shippriority
                        FROM `spring_bucket_r`.`spring_scope_r`.`CustomerR` AS c
                        JOIN `spring_bucket_r`.`spring_scope_r`.`OrdersR` AS o
                          ON META(c).id = o.o_custkey
                        JOIN `spring_bucket_r`.`spring_scope_r`.`LineitemR` AS l
                          ON META(o).id = l.l_orderkey
                        WHERE c.c_mktsegment = 'BUILDING'
                          AND o.o_orderdate < MILLIS('1995-03-15')
                          AND l.l_shipdate > MILLIS('1995-03-15')
                        GROUP BY META(o).id, o.o_orderdate, o.o_shippriority
                        ORDER BY revenue DESC, o.o_orderdate
                        LIMIT 10;
                        """;

        return cluster
                .query(query)
                .rowsAsObject();
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
     * @param cluster
     * @return
     */
    public static List<JsonObject> Q4(Cluster cluster){
        String query =
                """
                        SELECT
                          o.o_orderpriority,
                          COUNT(DISTINCT META(o).id) AS order_count
                        FROM `spring_bucket_r`.`spring_scope_r`.`OrdersR` AS o
                        JOIN `spring_bucket_r`.`spring_scope_r`.`LineitemR` AS l
                          ON META(o).id = l.l_orderkey
                        LET start_date = MILLIS('1993-07-01'),
                            end_date = MILLIS(DATE_ADD_STR('1993-07-01', 3, 'month'))
                        WHERE o.o_orderdate >= start_date
                          AND o.o_orderdate < end_date
                          AND l.l_commitdate < l.l_receiptdate
                        GROUP BY o.o_orderpriority
                        ORDER BY o.o_orderpriority;
                        """;

        return cluster
                .query(query)
                .rowsAsObject();
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
     * @param cluster
     * @return
     */
    public static List<JsonObject> Q5(Cluster cluster){
        String query =
                """
                        SELECT
                          n.n_name,
                          SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue
                        FROM `spring_bucket_r`.`spring_scope_r`.`CustomerR` AS c
                        JOIN `spring_bucket_r`.`spring_scope_r`.`OrdersR` AS o
                          ON META(c).id = o.o_custkey
                        JOIN `spring_bucket_r`.`spring_scope_r`.`LineitemR` AS l
                          ON META(o).id = l.l_orderkey
                        JOIN `spring_bucket_r`.`spring_scope_r`.`SupplierR` AS s
                          ON META(s).id = l.l_suppkey
                        JOIN `spring_bucket_r`.`spring_scope_r`.`NationR` AS n
                          ON s.s_nationkey = META(n).id
                        JOIN `spring_bucket_r`.`spring_scope_r`.`RegionR` AS r
                          ON n.n_regionkey = META(r).id
                        LET start_date = MILLIS('1994-01-01'),
                            end_date = MILLIS(DATE_ADD_STR('1994-01-01', 1, 'year'))
                        WHERE c.c_nationkey = s.s_nationkey
                          AND r.r_name = 'ASIA'
                          AND o.o_orderdate >= start_date
                          AND o.o_orderdate < end_date
                        GROUP BY n.n_name
                        ORDER BY revenue DESC;
                        """;

        return cluster
                .query(query)
                .rowsAsObject();
    }
}
