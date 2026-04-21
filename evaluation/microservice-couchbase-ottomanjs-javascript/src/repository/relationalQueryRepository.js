import { getDefaultInstance } from 'ottoman';

const B = '`bucket-main`.`ottoman_scope_r`';

function query(n1ql, options) {
    return getDefaultInstance().query(n1ql, options).then(r => r.rows);
}

// A — Selection / Projection

/**
 * A1) Full scan of lineitem
 * SELECT * FROM lineitem;
 */
export function a1() {
    return query(`SELECT * FROM ${B}.\`LineitemR\``);
}

/**
 * A2) Date range query on orders
 * SELECT * FROM orders WHERE o_orderdate BETWEEN '1996-01-01' AND '1996-12-31';
 */
export function a2(startDate, endDate) {
    return query(
        `SELECT * FROM ${B}.\`OrdersR\` AS o
         WHERE o.o_orderdate BETWEEN $startDate AND $endDate`,
        { parameters: { startDate, endDate } }
    );
}

/**
 * A3) Full scan of customer
 * SELECT * FROM customer;
 */
export function a3() {
    return query(`SELECT * FROM ${B}.\`CustomerR\``);
}

/**
 * A4) PK range query on orders (uses numeric o_orderkey_field index)
 * SELECT * FROM orders WHERE o_orderkey BETWEEN 1000 AND 50000;
 */
export function a4(minOrderKey, maxOrderKey) {
    return query(
        `SELECT * FROM ${B}.\`OrdersR\` AS o
         WHERE o.o_orderkey_field BETWEEN $minOrderKey AND $maxOrderKey`,
        { parameters: { minOrderKey, maxOrderKey } }
    );
}

// B — Aggregation

/**
 * B1) COUNT grouped by order month
 * SELECT COUNT(o_orderkey) AS order_count, DATE_FORMAT(o_orderdate,'%Y-%m') AS order_month FROM orders GROUP BY order_month;
 */
export function b1() {
    return query(
        `SELECT COUNT(META(o).id) AS order_count,
                DATE_FORMAT_STR(o.o_orderdate, "YYYY-MM") AS order_month
         FROM ${B}.\`OrdersR\` AS o
         GROUP BY DATE_FORMAT_STR(o.o_orderdate, "YYYY-MM")`
    );
}

/**
 * B2) MAX grouped by ship month
 * SELECT DATE_FORMAT(l_shipdate,'%Y-%m') AS ship_month, MAX(l_extendedprice) AS max_price FROM lineitem GROUP BY ship_month;
 */
export function b2() {
    return query(
        `SELECT DATE_FORMAT_STR(l.l_shipdate, "YYYY-MM") AS ship_month,
                MAX(l.l_extendedprice) AS max_price
         FROM ${B}.\`LineitemR\` AS l
         GROUP BY DATE_FORMAT_STR(l.l_shipdate, "YYYY-MM")`
    );
}

// C — Joins

/**
 * C1) Cartesian product — not feasible in Couchbase (always DNF)
 * SELECT c.c_name, o.o_orderdate, o.o_totalprice FROM customer c, orders o;
 */
export function c1() {
    throw new Error('C1 cartesian product is not supported — always DNF in Couchbase');
}

/**
 * C2) Inner join customer → orders
 * SELECT c.c_name, o.o_orderdate, o.o_totalprice FROM customer c JOIN orders o ON c.c_custkey = o.o_custkey;
 */
export function c2() {
    return query(
        `SELECT c.c_name AS cName, o.o_orderdate AS oOrderDate, o.o_totalprice AS oTotalPrice
         FROM ${B}.\`CustomerR\` AS c
         JOIN ${B}.\`OrdersR\` AS o ON META(c).id = o.o_custkey`
    );
}

/**
 * C3) 3-way join customer → nation → orders
 * SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice FROM customer c JOIN nation n ON c.c_nationkey = n.n_nationkey JOIN orders o ON c.c_custkey = o.o_custkey;
 */
export function c3() {
    return query(
        `SELECT c.c_name, n.n_name, o.o_orderdate, o.o_totalprice
         FROM ${B}.\`CustomerR\` AS c
         JOIN ${B}.\`NationR\`   AS n ON c.c_nationkey = META(n).id
         JOIN ${B}.\`OrdersR\`   AS o ON META(c).id = o.o_custkey`
    );
}

/**
 * C4) 4-way join customer → nation → region → orders
 * SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice FROM customer c JOIN nation n ... JOIN region r ... JOIN orders o ...;
 */
export function c4() {
    return query(
        `SELECT c.c_name, n.n_name, r.r_name, o.o_orderdate, o.o_totalprice
         FROM ${B}.\`CustomerR\` AS c
         JOIN ${B}.\`NationR\`   AS n ON c.c_nationkey = META(n).id
         JOIN ${B}.\`RegionR\`   AS r ON n.n_regionkey = META(r).id
         JOIN ${B}.\`OrdersR\`   AS o ON META(c).id = o.o_custkey`
    );
}

/**
 * C5) LEFT OUTER JOIN customer → orders
 * SELECT c.c_custkey, c.c_name, o.o_orderkey, o.o_orderdate FROM customer c LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey;
 */
export function c5() {
    return query(
        `SELECT META(c).id AS c_custkey, c.c_name, META(o).id AS o_orderkey, o.o_orderdate
         FROM ${B}.\`CustomerR\` AS c
         LEFT JOIN ${B}.\`OrdersR\` AS o ON META(c).id = o.o_custkey`
    );
}

// D — Set operations

/**
 * D1) UNION of customer and supplier nation keys
 * (SELECT c_nationkey FROM customer) UNION (SELECT s_nationkey FROM supplier);
 */
export function d1() {
    return query(
        `SELECT c.c_nationkey AS nationkey FROM ${B}.\`CustomerR\` AS c
         UNION
         SELECT s.s_nationkey AS nationkey FROM ${B}.\`SupplierR\` AS s`
    );
}

/**
 * D2) Simulated INTERSECT — customer keys also in supplier
 * SELECT DISTINCT c.c_custkey FROM customer c WHERE c.c_custkey IN (SELECT s.s_suppkey FROM supplier s);
 */
export function d2() {
    return query(
        `SELECT DISTINCT META(c).id AS c_custkey
         FROM ${B}.\`CustomerR\` AS c
         WHERE META(c).id IN (SELECT RAW META(s).id FROM ${B}.\`SupplierR\` AS s)`
    );
}

/**
 * D3) Simulated EXCEPT — customer keys not in supplier
 * SELECT DISTINCT c.c_custkey FROM customer c WHERE c.c_custkey NOT IN (SELECT DISTINCT s.s_suppkey FROM supplier s);
 */
export function d3() {
    return query(
        `SELECT DISTINCT META(c).id AS c_custkey
         FROM ${B}.\`CustomerR\` AS c
         WHERE META(c).id NOT IN (SELECT RAW META(s).id FROM ${B}.\`SupplierR\` AS s)`
    );
}

// E — Result modification

/**
 * E1) Sort by non-indexed column (c_acctbal)
 * SELECT c_name, c_address, c_acctbal FROM customer ORDER BY c_acctbal DESC;
 */
export function e1() {
    return query(
        `SELECT c.c_name, c.c_address, c.c_acctbal
         FROM ${B}.\`CustomerR\` AS c
         ORDER BY c.c_acctbal DESC`
    );
}

/**
 * E2) Sort by PK (o_orderkey)
 * SELECT o_orderkey, o_custkey, o_orderdate, o_totalprice FROM orders ORDER BY o_orderkey;
 */
export function e2() {
    return query(
        `SELECT META(o).id AS o_orderkey, o.o_custkey, o.o_orderdate, o.o_totalprice
         FROM ${B}.\`OrdersR\` AS o
         ORDER BY META(o).id ASC`
    );
}

/**
 * E3) DISTINCT nation key + market segment
 * SELECT DISTINCT c_nationkey, c_mktsegment FROM customer;
 */
export function e3() {
    return query(
        `SELECT DISTINCT c.c_nationkey, c.c_mktsegment
         FROM ${B}.\`CustomerR\` AS c`
    );
}

// Q — TPC-H benchmark queries

/**
 * Q1) Pricing Summary Report — aggregation with date cutoff
 */
export function q1() {
    return query(
        `SELECT l.l_returnflag,
                l.l_linestatus,
                SUM(l.l_quantity)                                            AS sum_qty,
                SUM(l.l_extendedprice)                                       AS sum_base_price,
                SUM(l.l_extendedprice * (1 - l.l_discount))                  AS sum_disc_price,
                SUM(l.l_extendedprice * (1 - l.l_discount) * (1 + l.l_tax)) AS sum_charge,
                AVG(l.l_quantity)                                            AS avg_qty,
                AVG(l.l_extendedprice)                                       AS avg_price,
                AVG(l.l_discount)                                            AS avg_disc,
                COUNT(*)                                                     AS count_order
         FROM ${B}.\`LineitemR\` AS l
         WHERE l.l_shipdate <= DATE_ADD_STR('1998-12-01', -90, 'day')
         GROUP BY l.l_returnflag, l.l_linestatus
         ORDER BY l.l_returnflag, l.l_linestatus`
    );
}

/**
 * Q2) Minimum Cost Supplier — correlated subquery via derived table
 */
export function q2() {
    return query(
        `SELECT s.s_acctbal, s.s_name, n.n_name, META(p).id AS p_partkey,
                p.p_mfgr, s.s_address, s.s_phone, s.s_comment
         FROM ${B}.\`PartR\` AS p
         JOIN ${B}.\`PartsuppR\` AS ps ON META(p).id = ps.ps_partkey
         JOIN (SELECT ps_inner.ps_partkey, MIN(ps_inner.ps_supplycost) AS min_supplycost
               FROM ${B}.\`PartsuppR\` AS ps_inner
               JOIN ${B}.\`SupplierR\` AS s_inner ON META(s_inner).id = ps_inner.ps_suppkey
               JOIN ${B}.\`NationR\`   AS n_inner ON s_inner.s_nationkey = META(n_inner).id
               JOIN ${B}.\`RegionR\`   AS r_inner ON n_inner.n_regionkey = META(r_inner).id
               WHERE r_inner.r_name = 'EUROPE'
               GROUP BY ps_inner.ps_partkey) AS minps
           ON ps.ps_partkey = minps.ps_partkey AND ps.ps_supplycost = minps.min_supplycost
         JOIN ${B}.\`SupplierR\` AS s ON META(s).id = ps.ps_suppkey
         JOIN ${B}.\`NationR\`   AS n ON s.s_nationkey = META(n).id
         JOIN ${B}.\`RegionR\`   AS r ON n.n_regionkey = META(r).id
         WHERE p.p_size = 15 AND p.p_type LIKE '%BRASS' AND r.r_name = 'EUROPE'
         ORDER BY s.s_acctbal DESC, n.n_name, s.s_name, META(p).id`
    );
}

/**
 * Q3) Shipping Priority — 3-table join with date filters
 */
export function q3() {
    return query(
        `SELECT META(o).id AS l_orderkey,
                SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue,
                o.o_orderdate, o.o_shippriority
         FROM ${B}.\`CustomerR\` AS c
         JOIN ${B}.\`OrdersR\`   AS o ON META(c).id = o.o_custkey
         JOIN ${B}.\`LineitemR\` AS l ON META(o).id = l.l_orderkey
         WHERE c.c_mktsegment = 'BUILDING'
           AND o.o_orderdate < '1995-03-15'
           AND l.l_shipdate  > '1995-03-15'
         GROUP BY META(o).id, o.o_orderdate, o.o_shippriority
         ORDER BY revenue DESC, o.o_orderdate
         LIMIT 10`
    );
}

/**
 * Q4) Order Priority — EXISTS-style join with commitdate < receiptdate
 */
export function q4() {
    return query(
        `SELECT o.o_orderpriority, COUNT(DISTINCT META(o).id) AS order_count
         FROM ${B}.\`OrdersR\`   AS o
         JOIN ${B}.\`LineitemR\` AS l ON META(o).id = l.l_orderkey
         WHERE o.o_orderdate >= '1993-07-01'
           AND o.o_orderdate  < '1993-10-01'
           AND l.l_commitdate < l.l_receiptdate
         GROUP BY o.o_orderpriority
         ORDER BY o.o_orderpriority`
    );
}

/**
 * Q5) Local Supplier Volume — 6-table join, same-nation filter
 */
export function q5() {
    return query(
        `SELECT n.n_name, SUM(l.l_extendedprice * (1 - l.l_discount)) AS revenue
         FROM ${B}.\`CustomerR\` AS c
         JOIN ${B}.\`OrdersR\`   AS o ON META(c).id = o.o_custkey
         JOIN ${B}.\`LineitemR\` AS l ON META(o).id = l.l_orderkey
         JOIN ${B}.\`SupplierR\` AS s ON META(s).id = l.l_suppkey
         JOIN ${B}.\`NationR\`   AS n ON s.s_nationkey = META(n).id
         JOIN ${B}.\`RegionR\`   AS r ON n.n_regionkey = META(r).id
         WHERE c.c_nationkey = s.s_nationkey
           AND r.r_name = 'ASIA'
           AND o.o_orderdate >= '1994-01-01'
           AND o.o_orderdate  < '1995-01-01'
         GROUP BY n.n_name
         ORDER BY revenue DESC`
    );
}
