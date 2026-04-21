package cz.cuni.mff.java.kurinna.microservice.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final String[] ALL_SERVICES = new String[] {
            "ebean", "cayenne", "jdbc", "jooq", "myBatis", "springDataJpa", "morphia", "couchbaseSpringData", "springDataMongoDB", "mongooseMongoDBJavascript", "ottomanJSCouchbaseJavascript", "mongoDBEntitiesCSharp"
    };

    /** Services that implement the embedded document model (r1–r9). */
    public static final String[] EMBEDDED_SERVICES = new String[] { "morphia", "couchbaseSpringData", "springDataMongoDB", "mongooseMongoDBJavascript", "ottomanJSCouchbaseJavascript", "mongoDBEntitiesCSharp" };

    public static final String[] ALL_RELATIONAL_QUERIES = new String[] {
            "q1", "q2", "q3", "q4", "q5",
            "a1", "a2", "a3", "a4",
            "b1", "b2",
            "c1", "c2", "c3", "c4", "c5",
            "d1", "d2", "d3",
            "e1", "e2", "e3"
    };

    public static final String[] ALL_EMBEDDED_QUERIES = new String[] {
            "r1", "r2", "r3", "r4", "r5", "r6", "r7", "r8", "r9"
    };

    public static final String[] ALL_QUERIES = ArrayUtils.addAll(ALL_RELATIONAL_QUERIES, ALL_EMBEDDED_QUERIES);

    public static final Map<String, String> EMBEDDED_QUERY_DESCRIPTIONS = Map.of(
            ALL_EMBEDDED_QUERIES[0], "db.ordersEWithLineitems.aggregate([{$match:{\"o_lineitems.l_quantity\":{$gt:5}}},{$project:{o_orderdate:1,\"o_lineitems.l_partkey\":1}}])",
            ALL_EMBEDDED_QUERIES[1], "db.ordersEWithLineitems.aggregate([{$match:{\"o_lineitems.l_partkey\":{$gt:20000}}},{$project:{o_orderdate:1,\"o_lineitems.l_partkey\":1}}])",
            ALL_EMBEDDED_QUERIES[2], "db.ordersEWithLineitemsArrayAsTags.find({o_lineitems_tags:\"MAIL\"},{o_orderdate:1,o_lineitems_tags:1})",
            ALL_EMBEDDED_QUERIES[3], "db.ordersEWithLineitemsArrayAsTagsIndexed.find({o_lineitems_tags_indexed:\"MAIL\"},{o_orderdate:1,o_lineitems_tags_indexed:1})",
            ALL_EMBEDDED_QUERIES[4], "db.ordersEWithCustomerWithNationWithRegion.find({\"o_customer.c_nation.n_region.r_name\":\"AMERICA\"})",
            ALL_EMBEDDED_QUERIES[5], "db.ordersEOnlyOComment.find({o_comment:/furiously/i})",
            ALL_EMBEDDED_QUERIES[6], "db.ordersEOnlyOCommentIndexed.find({$text:{$search:\"furiously\"}})",
            ALL_EMBEDDED_QUERIES[7], "db.ordersEWithLineitems.aggregate([{$unwind:\"$o_lineitems\"},{$project:{\"o_lineitems.l_partkey\":1}}])",
            ALL_EMBEDDED_QUERIES[8], "db.ordersEWithLineitems.aggregate([{$unwind:\"$o_lineitems\"},{$group:{_id:\"$_id\",totalRevenue:{$sum:\"$o_lineitems.l_extendedprice\"}}}])"
    );

    public static final Map<String, String> RELATIONAL_QUERY_DESCRIPTIONS = Map.ofEntries(
            Map.entry("test1", "Some non-existant test query"),
            // A queries
            Map.entry("a1", "SELECT * FROM lineitem;"),
            Map.entry("a2", "SELECT * FROM orders WHERE o_orderdate BETWEEN '1996-01-01' AND '1996-12-31';"),
            Map.entry("a3", "SELECT * FROM customer;"),
            Map.entry("a4", "SELECT * FROM orders WHERE o_orderkey BETWEEN 1000 AND 50000;"),

            // B queries
            Map.entry("b1",
                    "SELECT COUNT(o.o_orderkey) AS order_count, DATE_FORMAT(o.o_orderdate, '%Y-%m') AS order_month FROM orders o GROUP BY order_month;"),
            Map.entry("b2",
                    "SELECT DATE_FORMAT(l.l_shipdate, '%Y-%m') AS ship_month, MAX(l.l_extendedprice) AS max_price FROM lineitem l GROUP BY ship_month;"),

            // C queries
            Map.entry("c1", "SELECT c.c_name, o.o_orderdate, o.o_totalprice FROM customer c, orders o;"),
            Map.entry("c2",
                    "SELECT c.c_name, o.o_orderdate, o.o_totalprice FROM customer c JOIN orders o ON c.c_custkey = o.o_custkey;"),
            Map.entry("c3",
                    "SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice FROM customer c JOIN nation n ON c.c_nationkey = n.n_nationkey JOIN orders o ON c.c_custkey = o.o_custkey;"),
            Map.entry("c4",
                    "SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice FROM customer c JOIN nation n ON c.c_nationkey = n.n_nationkey JOIN region r ON n.n_regionkey = r.r_regionkey JOIN orders o ON c.c_custkey = o.o_custkey;"),
            Map.entry("c5",
                    "SELECT c.c_custkey, c.c_name, o.o_orderkey, o.o_orderdate FROM customer c LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey;"),

            // D queries
            Map.entry("d1", "(SELECT c_nationkey FROM customer) UNION (SELECT s_nationkey FROM supplier);"),
            Map.entry("d2",
                    "SELECT DISTINCT c.c_custkey FROM customer c WHERE c.c_custkey IN (SELECT s.s_suppkey FROM supplier s);"),
            Map.entry("d3",
                    "SELECT DISTINCT c.c_custkey FROM customer c WHERE c.c_custkey NOT IN (SELECT DISTINCT s.s_suppkey FROM supplier s);"),

            // E queries
            Map.entry("e1", "SELECT c_name, c_address, c_acctbal FROM customer ORDER BY c_acctbal DESC"),
            Map.entry("e2", "SELECT o_orderkey, o_custkey, o_orderdate, o_totalprice FROM orders ORDER BY o_orderkey"),
            Map.entry("e3", "SELECT DISTINCT c_nationkey, c_mktsegment FROM customer;"),

            // Q queries
            Map.entry("q1",
                    "SELECT l_returnflag, l_linestatus, SUM(l_quantity) AS sum_qty, SUM(l_extendedprice) AS sum_base_price, SUM(l_extendedprice * (1 - l_discount)) AS sum_disc_price, SUM(l_extendedprice * (1 - l_discount) * (1 + l_tax)) AS sum_charge, AVG(l_quantity) AS avg_qty, AVG(l_extendedprice) AS avg_price, AVG(l_discount) AS avg_disc, COUNT(*) AS count_order FROM lineitem WHERE l_shipdate <= DATE_SUB('1998-12-01', INTERVAL 90 DAY) GROUP BY l_returnflag, l_linestatus ORDER BY l_returnflag, l_linestatus"),
            Map.entry("q2",
                    "SELECT s.s_acctbal, s.s_name, n.n_name, p.p_partkey, p.p_mfgr, s.s_address, s.s_phone, s.s_comment FROM part p, supplier s, partsupp ps, nation n, region r WHERE p.p_partkey = ps.ps_partkey AND s.s_suppkey = ps.ps_suppkey AND p.p_size = 15 AND p.p_type LIKE '%BRASS' AND s.s_nationkey = n.n_nationkey AND n.n_regionkey = r.r_regionkey AND r.r_name = 'EUROPE' AND ps.ps_supplycost = (SELECT MIN(ps.ps_supplycost) FROM partsupp ps, supplier s, nation n, region r WHERE p.p_partkey = ps.ps_partkey AND s.s_suppkey = ps.ps_suppkey AND s.s_nationkey = n.n_nationkey AND n.n_regionkey = r.r_regionkey AND r.r_name = 'EUROPE') ORDER BY s.s_acctbal DESC, n.n_name, s.s_name, p.p_partkey LIMIT 100"),
            Map.entry("q3",
                    "SELECT l.l_orderkey, SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue, o.o_orderdate, o.o_shippriority FROM customer c, orders o, lineitem l WHERE c.c_mktsegment = 'BUILDING' AND c.c_custkey = o.o_custkey AND l.l_orderkey = o.o_orderkey AND o.o_orderdate < '1995-03-15' AND l.l_shipdate > '1995-03-15' GROUP BY l.l_orderkey, o.o_orderdate, o.o_shippriority ORDER BY revenue DESC, o.o_orderdate LIMIT 10"),
            Map.entry("q4",
                    "SELECT o_orderpriority, COUNT(*) AS order_count FROM orders WHERE o_orderdate >= '1993-07-01' AND o_orderdate < DATE_ADD('1993-07-01', INTERVAL 3 MONTH) AND EXISTS (SELECT * FROM lineitem WHERE l_orderkey = o_orderkey AND l_commitdate < l_receiptdate) GROUP BY o_orderpriority ORDER BY o_orderpriority"),
            Map.entry("q5",
                    "SELECT n.n_name, SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue FROM customer c, orders o, lineitem l, supplier s, nation n, region r WHERE c.c_custkey = o.o_custkey AND l.l_orderkey = o.o_orderkey AND l.l_suppkey = s.s_suppkey AND c.c_nationkey = s.s_nationkey AND s.s_nationkey = n.n_nationkey AND n.n_regionkey = r.r_regionkey AND r.r_name = 'ASIA' AND o.o_orderdate >= '1994-01-01' AND o.o_orderdate < DATE_ADD('1994-01-01', INTERVAL 1 YEAR) GROUP BY n.n_name ORDER BY revenue DESC"));


    public static final Map<String, String> QUERY_DESCRIPTIONS = new HashMap<>();
    static
    {
        QUERY_DESCRIPTIONS.putAll(RELATIONAL_QUERY_DESCRIPTIONS);
        QUERY_DESCRIPTIONS.putAll(EMBEDDED_QUERY_DESCRIPTIONS);
    }

}
