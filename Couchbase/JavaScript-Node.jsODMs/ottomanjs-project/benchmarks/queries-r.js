import {LineitemR} from "../models/tpc_h_r/lineitem-r.js";
import {OrdersR} from "../models/tpc_h_r/orders-r.js";
import {RegionR} from "../models/tpc_h_r/region-r.js";
import ottoman, {getDefaultInstance, Query} from "ottoman";
import {CustomerR} from "../models/tpc_h_r/customer-r.js";


/**
 * A1) Non-Indexed Columns
 *
 * This query selects all records from the lineitem table
 * ```sql
 *         SELECT * FROM lineitem;
 * ```
 */
async function A1(){
    /*
    Ops/sec: 0.00
    Average time per op: 220113.647 ms
    */
    const a1 = await LineitemR.find({} );

    return a1.rows;
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
async function A2(){
    /*
    Ops/sec: 0.08
    Average time per op: 12577.724 ms
    */
    const a2 = await OrdersR.find({
        $or: [{
                o_orderdate: { $btw: [new Date('1996-01-01'), new Date('1996-12-31')] }
        }]
    });

    return a2.rows;
}

/**
 *
 ### A3) Indexed Columns

 This query selects all records from the customer table
 ```sql
 SELECT * FROM customer;
 ```
 */
async function A3(){
    const a3 = await CustomerR.find({} );

    return a3.rows;
}

/**
 * ### A4) Indexed Columns — Range Query
 *
 * This query selects all records from the orders table where the order key is between 1000 and 50000
 * ```sql
 * SELECT * FROM orders
 * WHERE o_orderkey BETWEEN 1000 AND 50000;
 * ```
 * @returns {Promise<TRow[]|any[]|TRow[]|TRow[]|ViewRow<TKey, TValue>[]|string[]|CppSearchResponseSearchRow[]|CppDocumentViewResponseRow[]|string|HTMLCollectionOf<HTMLTableRowElement>|number|SQLResultSetRowList>}
 * @constructor
 */
async function A4(){

    const a4 = await OrdersR.find({
        $or: [{
            o_orderkey_field: { $btw: [1000, 50000] }
        }]
    });

    return a4.rows;
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
async function B1() {
    /*
    Ops/sec: 0.10
    Average time per op: 9653.446 ms
    */
    const b1 = await ottoman.getDefaultInstance()
        .query(
            "SELECT COUNT(META(o).id) AS order_count, DATE_FORMAT_STR(o.o_orderdate, \"YYYY-MM\") AS order_month\n" +
            "FROM ottoman_bucket_r.ottoman_scope_r.OrdersR AS o\n" +
            "GROUP BY DATE_FORMAT_STR(o.o_orderdate, \"YYYY-MM\")"
        );

    return b1.rows;
}

async function B2() {

    const b2 = await ottoman.getDefaultInstance()
        .query(
            `SELECT DATE_FORMAT_STR(l.l_shipdate, "%Y-%m") AS ship_month,
                    MAX(l.l_extendedprice)                 AS max_price
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`LineitemR\` AS l
             GROUP BY DATE_FORMAT_STR(l.l_shipdate, "%Y-%m");`
        );

    return b2.rows;
}

/**
 * ### C1) Non-Indexed Columns
 *
 * This query gives customer names, order dates, and total prices for customers
 * ```sql
 * SELECT c.c_name, o.o_orderdate, o.o_totalprice
 * FROM customer c, orders o;
 * ```
 * @returns {Promise<any[]>}
 * @constructor
 */
async function C1(){
    throw new Error("Not possible to implement in Couchbase")
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
async function C2(){
    /*
    Ops/sec: 0.10
    Average time per op: 9905.980 ms
    */
    const c2 = await ottoman.getDefaultInstance()
        .query(
    "SELECT c.c_name AS cName, o.o_orderdate AS oOrderDate, o.o_totalprice AS oTotalPrice"+
    " FROM ottoman_bucket_r.ottoman_scope_r.CustomerR c"+
    " JOIN ottoman_bucket_r.ottoman_scope_r.OrdersR o ON META(c).id = o.o_custkey"
    );

    return c2.rows;
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
 * @returns {Promise<any[]>}
 * @constructor
 */
async function C3(){

    const c3 = await ottoman.getDefaultInstance()
        .query(
            `SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`CustomerR\` AS c
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`NationR\` AS n
                           ON c.c_nationkey = META(n).id
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`OrdersR\` AS o
                           ON META(c).id = o.o_custkey;`

        );

    return c3.rows;
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
 * @returns {Promise<any[]>}
 * @constructor
 */
async function C4(){

    const c4 = await ottoman.getDefaultInstance()
        .query(
            `SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`CustomerR\` AS c
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`NationR\` AS n
                           ON c.c_nationkey = META(n).id
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`RegionR\` AS r
                           ON n.n_regionkey = META(r).id
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`OrdersR\` AS o
                           ON META(c).id = o.o_custkey;`

        );

    return c4.rows;
}

async function C5(){

    const c5 = await ottoman.getDefaultInstance()
        .query(
            `SELECT META(c).id AS c_custkey, c.c_name, META(o).id AS o_orderkey, o.o_orderdate
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`CustomerR\` AS c
                      LEFT JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`OrdersR\` AS o
                                ON META(c).id = o.o_custkey;`

        );

    return c5.rows;
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
async function D1(){
    /*
    Ops/sec: 1.33
    Average time per op: 751.370 ms
    */
    const d1 = await ottoman.getDefaultInstance()
        .query(
            `SELECT c.c_nationkey AS nationkey
                    FROM ottoman_bucket_r.ottoman_scope_r.CustomerR AS c
                    UNION
                    SELECT s.s_nationkey AS nationkey
                    FROM ottoman_bucket_r.ottoman_scope_r.SupplierR AS s;`
        )
    return d1.rows;
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
async function D2(){

    const d2 = await ottoman.getDefaultInstance()
        .query(
            `SELECT DISTINCT META(c).id AS c_custkey
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`CustomerR\` AS c
             WHERE META(c).id IN (SELECT RAW META(s).id
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`SupplierR\` AS s
                 );`
        )
    return d2.rows;
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
async function D3(){

    const d3 = await ottoman.getDefaultInstance()
        .query(
            `SELECT DISTINCT META(c).id AS c_custkey
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`CustomerR\` AS c
             WHERE META(c).id NOT IN (SELECT RAW META(s).id
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`SupplierR\` AS s
                 );`
        )
    return d3.rows;
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
async function E1(){

    const e1 = await ottoman.getDefaultInstance()
        .query(
            `SELECT c.c_name, c.c_address, c.c_acctbal
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`CustomerR\` AS c
             ORDER BY c.c_acctbal DESC;`
        )
    return e1.rows;
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
async function E2(){

    const e2 = await ottoman.getDefaultInstance()
        .query(
            `SELECT META(o).id AS o_orderkey, o.o_custkey, o.o_orderdate, o.o_totalprice
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`OrdersR\` AS o
             ORDER BY META(o).id ASC;`
        )
    return e2.rows;
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
async function E3(){

    const e3 = await ottoman.getDefaultInstance()
        .query(
            `SELECT DISTINCT c.c_nationkey, c.c_mktsegment
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`CustomerR\` AS c;`
        )
    return e3.rows;
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
async function Q1(){

    const q1 = await ottoman.getDefaultInstance()
        .query(
            `SELECT l.l_returnflag,
                    l.l_linestatus,
                    SUM(l.l_quantity)                                           AS sum_qty,
                    SUM(l.l_extendedprice)                                      AS sum_base_price,
                    SUM(l.l_extendedprice * (1 - l.l_discount))                 AS sum_disc_price,
                    SUM(l.l_extendedprice * (1 - l.l_discount) * (1 + l.l_tax)) AS sum_charge,
                    AVG(l.l_quantity)                                           AS avg_qty,
                    AVG(l.l_extendedprice)                                      AS avg_price,
                    AVG(l.l_discount)                                           AS avg_disc,
                    COUNT(*)                                                    AS count_order
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`LineitemR\` AS l
             WHERE l.l_shipdate <= DATE_ADD_STR('1998-12-01', -90, 'day')
             GROUP BY l.l_returnflag, l.l_linestatus
             ORDER BY l.l_returnflag, l.l_linestatus;`
        )
    return q1.rows;
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
async function Q2(){

    const q2 = await ottoman.getDefaultInstance()
        .query(
            `SELECT s.s_acctbal,
                    s.s_name,
                    n.n_name,
                    p.p_partkey,
                    p.p_mfgr,
                    s.s_address,
                    s.s_phone,
                    s.s_comment
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`PartR\` AS p
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`PartsuppR\` AS ps
                           ON META(p).id = ps.ps_partkey
                      JOIN (SELECT ps_inner.ps_partkey,
                                   MIN(ps_inner.ps_supplycost) AS min_supplycost
                            FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`PartsuppR\` AS ps_inner
                                     JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`SupplierR\` AS s_inner
                                          ON META(s_inner).id = ps_inner.ps_suppkey
                                     JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`NationR\` AS n_inner
                                          ON s_inner.s_nationkey = META(n_inner).id
                                     JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`RegionR\` AS r_inner
                                          ON n_inner.n_regionkey = META(r_inner).id
                            WHERE r_inner.r_name = 'EUROPE'
                            GROUP BY ps_inner.ps_partkey) AS minps
                           ON ps.ps_partkey = minps.ps_partkey
                               AND ps.ps_supplycost = minps.min_supplycost
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`SupplierR\` AS s
                           ON META(s).id = ps.ps_suppkey
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`NationR\` AS n
                           ON s.s_nationkey = META(n).id
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`RegionR\` AS r
                           ON n.n_regionkey = META(r).id
             WHERE p.p_size = 15
               AND p.p_type LIKE '%BRASS'
               AND r.r_name = 'EUROPE'
             ORDER BY s.s_acctbal DESC, n.n_name, s.s_name, META(p).id;`
        )
    return q2.rows;
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
async function Q3(){

    const q3 = await ottoman.getDefaultInstance()
        .query(
            `SELECT META(o).id                                  AS l_orderkey,
                    SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue,
                    o.o_orderdate,
                    o.o_shippriority
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`CustomerR\` AS c
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`OrdersR\` AS o
                           ON META(c).id = o.o_custkey
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`LineitemR\` AS l
                           ON META(o).id = l.l_orderkey
             WHERE c.c_mktsegment = 'BUILDING'
               AND o.o_orderdate < STR_TO_UTC('1995-03-15')
               AND l.l_shipdate > STR_TO_UTC('1995-03-15')
             GROUP BY META(o).id, o.o_orderdate, o.o_shippriority
             ORDER BY revenue DESC, o.o_orderdate LIMIT 10;`
        )
    return q3.rows;
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
async function Q4(){

    const q4 = await ottoman.getDefaultInstance()
        .query(
            `SELECT o.o_orderpriority,
                    COUNT(DISTINCT META(o).id) AS order_count
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`OrdersR\` AS o
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`LineitemR\` AS l
                           ON META(o).id = l.l_orderkey LET start_date = STR_TO_UTC('1993-07-01'),
                            end_date = DATE_ADD_STR('1993-07-01', 3, 'month')
             WHERE o.o_orderdate >= start_date
               AND o.o_orderdate
                 < end_date
               AND l.l_commitdate
                 < l.l_receiptdate
             GROUP BY o.o_orderpriority
             ORDER BY o.o_orderpriority;`
        )
    return q4.rows;
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
async function Q5(){

    const q5 = await ottoman.getDefaultInstance()
        .query(
            `SELECT n.n_name,
                    SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue
             FROM \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`CustomerR\` AS c
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`OrdersR\` AS o
                           ON META(c).id = o.o_custkey
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`LineitemR\` AS l
                           ON META(o).id = l.l_orderkey
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`SupplierR\` AS s
                           ON META(s).id = l.l_suppkey
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`NationR\` AS n
                           ON s.s_nationkey = META(n).id
                      JOIN \`ottoman_bucket_r\`.\`ottoman_scope_r\`.\`RegionR\` AS r
                           ON n.n_regionkey = META(r).id LET start_date = STR_TO_UTC('1994-01-01'),
                            end_date = DATE_ADD_STR('1994-01-01', 1, 'year')
             WHERE c.c_nationkey = s.s_nationkey
               AND r.r_name = 'ASIA'
               AND o.o_orderdate >= start_date
               AND o.o_orderdate
                 < end_date
             GROUP BY n.n_name
             ORDER BY revenue DESC;`
        )
    return q5.rows;
}

export {
    A1,
    A2,
    A3,
    A4,
    B1,
    B2,
    C1,
    C2,
    C3,
    C4,
    C5,
    D1,
    D2,
    D3,
    E1,
    E2,
    E3,
    Q1,
    Q2,
    Q3,
    Q4,
    Q5
}