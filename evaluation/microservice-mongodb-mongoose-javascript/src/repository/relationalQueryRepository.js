import RegionR   from '../model/relational/region-r.js';
import NationR   from '../model/relational/nation-r.js';
import CustomerR from '../model/relational/customer-r.js';
import OrdersR   from '../model/relational/orders-r.js';
import PartR     from '../model/relational/part-r.js';
import PartsuppR from '../model/relational/partsupp-r.js';
import SupplierR from '../model/relational/supplier-r.js';
import LineitemR from '../model/relational/lineitem-r.js';

// A — Selection / Projection

/**
 * A1) Non-Indexed Columns
 *
 * This query selects all records from the lineitem table
 * ```sql
 *         SELECT * FROM lineitem;
 * ```
 */
export function a1() {
    return LineitemR.find();
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
export function a2(startDate, endDate) {
    return OrdersR.find().where('o_orderdate').gte(startDate).lte(endDate).exec();
}

/**
 * ### A3) Indexed Columns
 *
 * This query selects all records from the customer table
 * ```sql
 * SELECT * FROM customer;
 * ```
 */
export function a3() {
    return CustomerR.find();
}

/**
 * ### A4) Indexed Columns — Range Query
 *
 * This query selects all records from the orders table where the order key is between 1000 and 50000
 * ```sql
 * SELECT * FROM orders
 * WHERE o_orderkey BETWEEN 1000 AND 50000;
 * ```
 */
export function a4(minOrderKey, maxOrderKey) {
    return OrdersR.find().where('_id').gte(minOrderKey).lte(maxOrderKey).exec();
}

// B — Aggregation

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
export function b1() {
    return OrdersR.aggregate()
        .group({
            _id: { $dateToString: { format: '%Y-%m', date: '$o_orderdate' } },
            order_count: { $sum: 1 }
        })
        .project({ _id: 0, order_month: '$_id', order_count: 1 })
        .exec();
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
 **/
export function b2() {
    return LineitemR.aggregate()
        .group({
            _id: { $dateToString: { format: '%Y-%m', date: '$l_shipdate' } },
            max_price: { $max: '$l_extendedprice' }
        })
        .project({ _id: 0, ship_month: '$_id', max_price: 1 })
        .exec();
}

// C — Joins

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
export function c1() {
    return CustomerR.aggregate()
        .lookup({ from: 'OrdersR', pipeline: [], as: 'orders' })
        .unwind('$orders')
        .project({ _id: 0, c_name: 1, o_orderdate: '$orders.o_orderdate', o_totalprice: '$orders.o_totalprice' })
        .limit(1_500_000)
        .exec();
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
export function c2() {
    return CustomerR.aggregate()
        .lookup({ from: 'OrdersR', localField: '_id', foreignField: 'o_custkey', as: 'orders' })
        .unwind('$orders')
        .project({ _id: 0, c_name: 1, o_orderdate: '$orders.o_orderdate', o_totalprice: '$orders.o_totalprice' })
        .exec();
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
 * @returns {Promise<Array<any>>}
 * @constructor
 */
export function c3() {
    return CustomerR.aggregate()
        .lookup({ from: 'NationR', localField: 'c_nationkey', foreignField: '_id', as: 'nation' })
        .unwind('$nation')
        .lookup({ from: 'OrdersR', localField: '_id', foreignField: 'o_custkey', as: 'orders' })
        .unwind('$orders')
        .project({ _id: 0, c_name: 1, n_name: '$nation.n_name', o_orderdate: '$orders.o_orderdate', o_totalprice: '$orders.o_totalprice' })
        .exec();
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
 */
export function c4() {
    return CustomerR.aggregate()
        .lookup({ from: 'NationR', localField: 'c_nationkey', foreignField: '_id', as: 'nation' })
        .unwind('$nation')
        .lookup({ from: 'RegionR', localField: 'nation.n_regionkey', foreignField: '_id', as: 'region' })
        .unwind('$region')
        .lookup({ from: 'OrdersR', localField: '_id', foreignField: 'o_custkey', as: 'orders' })
        .unwind('$orders')
        .project({ _id: 0, c_name: 1, n_name: '$nation.n_name', r_name: '$region.r_name', o_orderdate: '$orders.o_orderdate', o_totalprice: '$orders.o_totalprice' })
        .exec();
}

/**
 * ### C5) Left Outer Join
 *
 * This query gives customer names and order dates for all customers, including those without orders
 * ```sql
 * SELECT c.c_custkey, c.c_name, o.o_orderkey, o.o_orderdate
 * FROM customer c
 * LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey;
 * ```
 */
export function c5() {
    return CustomerR.aggregate()
        .lookup({ from: 'OrdersR', localField: '_id', foreignField: 'o_custkey', as: 'orders' })
        .unwind({ path: '$orders', preserveNullAndEmptyArrays: true })
        .project({ _id: 0, c_custkey: '$_id', c_name: 1, o_orderkey: '$orders._id', o_orderdate: '$orders.o_orderdate' })
        .exec();
}

// D — Set operations

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
export function d1() {
    return CustomerR.aggregate()
        .project({ nationkey: '$c_nationkey', _id: 0 })
        .unionWith({ coll: 'SupplierR', pipeline: [{ $project: { nationkey: '$s_nationkey', _id: 0 } }] })
        .group({ _id: '$nationkey' })
        .project({ _id: 0, nationkey: '$_id' })
        .exec();
}

/**
 *
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
 */
export function d2() {
    return CustomerR.aggregate()
        .lookup({ from: 'SupplierR', localField: '_id', foreignField: '_id', as: 'matched_supplier' })
        .match({ 'matched_supplier.0': { $exists: true } })
        .project({ _id: 0, c_custkey: '$_id' })
        .exec();
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
 */
export function d3() {
    return CustomerR.aggregate()
        .lookup({ from: 'SupplierR', localField: '_id', foreignField: '_id', as: 'matched_supplier' })
        .match({ matched_supplier: { $eq: [] } })
        .project({ _id: 0, c_custkey: '$_id' })
        .exec();
}

// E — Result modification

/**
 * ### E1) Non-Indexed Columns Sorting
 *
 * This query sorts customer names, addresses, and account balances in descending order of account balance
 * ```sql
 * SELECT c_name, c_address, c_acctbal
 * FROM customer
 * ORDER BY c_acctbal DESC
 * ```
 */
export function e1() {
    return CustomerR.find({}, { _id: 0, c_name: 1, c_address: 1, c_acctbal: 1 })
        .sort({ c_acctbal: -1 })
        .exec();
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
export function e2() {
    return OrdersR.find({}, { _id: 1, o_custkey: 1, o_orderdate: 1, o_totalprice: 1 })
        .sort({ _id: 1 })
        .exec();
}

/**
 * ### E3) Distinct
 *
 * This query selects distinct nation keys and market segments from the customer table
 * ```sql
 * SELECT DISTINCT c_nationkey, c_mktsegment
 * FROM customer;
 * ```
 */
export function e3() {
    return CustomerR.aggregate()
        .group({ _id: { c_nationkey: '$c_nationkey', c_mktsegment: '$c_mktsegment' } })
        .project({ _id: 0, c_nationkey: '$_id.c_nationkey', c_mktsegment: '$_id.c_mktsegment' })
        .exec();
}

// Q — TPC-H benchmark queries

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
export function q1() {
    return LineitemR.aggregate()
        .addFields({ ship_date_limit: { $dateSubtract: { startDate: new Date('1998-12-01'), unit: 'day', amount: 90 } } })
        .match({ $expr: { $lte: ['$l_shipdate', '$ship_date_limit'] } })
        .group({
            _id: { l_returnflag: '$l_returnflag', l_linestatus: '$l_linestatus' },
            sum_qty:        { $sum: '$l_quantity' },
            sum_base_price: { $sum: '$l_extendedprice' },
            sum_disc_price: { $sum: { $multiply: ['$l_extendedprice', { $subtract: [1, '$l_discount'] }] } },
            sum_charge:     { $sum: { $multiply: ['$l_extendedprice', { $subtract: [1, '$l_discount'] }, { $add: [1, '$l_tax'] }] } },
            avg_qty:        { $avg: '$l_quantity' },
            avg_price:      { $avg: '$l_extendedprice' },
            avg_disc:       { $avg: '$l_discount' },
            count_order:    { $sum: 1 }
        })
        .project({ _id: 0, l_returnflag: '$_id.l_returnflag', l_linestatus: '$_id.l_linestatus', sum_qty: 1, sum_base_price: 1, sum_disc_price: 1, sum_charge: 1, avg_qty: 1, avg_price: 1, avg_disc: 1, count_order: 1 })
        .sort({ l_returnflag: 1, l_linestatus: 1 })
        .exec();
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
 */
export function q2() {
    return PartR.aggregate()
        .match({ p_size: 15, p_type: /BRASS$/ })
        .lookup({ from: 'PartsuppR', localField: '_id', foreignField: 'ps_partkey', as: 'partsupp' })
        .unwind('$partsupp')
        .lookup({ from: 'SupplierR', localField: 'partsupp.ps_suppkey', foreignField: '_id', as: 'supplier' })
        .unwind('$supplier')
        .lookup({ from: 'NationR', localField: 'supplier.s_nationkey', foreignField: '_id', as: 'nation' })
        .unwind('$nation')
        .lookup({ from: 'RegionR', localField: 'nation.n_regionkey', foreignField: '_id', as: 'region' })
        .unwind('$region')
        .match({ 'region.r_name': 'EUROPE' })
        .group({ _id: '$_id', min_supplycost: { $min: '$partsupp.ps_supplycost' }, docs: { $push: '$$ROOT' } })
        .project({ docs: { $filter: { input: '$docs', as: 'doc', cond: { $eq: ['$$doc.partsupp.ps_supplycost', '$min_supplycost'] } } } })
        .unwind('$docs')
        .project({ _id: 0, s_acctbal: '$docs.supplier.s_acctbal', s_name: '$docs.supplier.s_name', n_name: '$docs.nation.n_name', p_partkey: '$docs._id', p_mfgr: '$docs.p_mfgr', s_address: '$docs.supplier.s_address', s_phone: '$docs.supplier.s_phone', s_comment: '$docs.supplier.s_comment' })
        .sort({ s_acctbal: -1, n_name: 1, s_name: 1, p_partkey: 1 })
        .exec();
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
 */
export function q3() {
    const cutoffDate = new Date('1995-03-15');
    return CustomerR.aggregate()
        .match({ c_mktsegment: 'BUILDING' })
        .lookup({ from: 'OrdersR', localField: '_id', foreignField: 'o_custkey', as: 'orders' })
        .unwind('$orders')
        .match({ 'orders.o_orderdate': { $lt: cutoffDate } })
        .lookup({ from: 'LineitemR', localField: 'orders._id', foreignField: 'l_orderkey', as: 'lineitems' })
        .unwind('$lineitems')
        .match({ 'lineitems.l_shipdate': { $gt: cutoffDate } })
        .group({ _id: { l_orderkey: '$orders._id', o_orderdate: '$orders.o_orderdate', o_shippriority: '$orders.o_shippriority' }, revenue: { $sum: { $multiply: ['$lineitems.l_extendedprice', { $subtract: [1, '$lineitems.l_discount'] }] } } })
        .project({ _id: 0, l_orderkey: '$_id.l_orderkey', revenue: 1, o_orderdate: '$_id.o_orderdate', o_shippriority: '$_id.o_shippriority' })
        .sort({ revenue: -1, o_orderdate: 1 })
        .limit(10)
        .exec();
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
 */
export function q4() {
    const startDate = new Date('1993-07-01');
    return OrdersR.aggregate()
        .addFields({ endDate: { $dateAdd: { startDate, unit: 'month', amount: 3 } } })
        .match({ $expr: { $and: [{ $gte: ['$o_orderdate', startDate] }, { $lt: ['$o_orderdate', '$endDate'] }] } })
        .lookup({ from: 'LineitemR', let: { orderKey: '$_id' }, pipeline: [{ $match: { $expr: { $lt: ['$l_commitdate', '$l_receiptdate'] } } }, { $match: { $expr: { $eq: ['$l_orderkey', '$$orderKey'] } } }], as: 'valid_lineitems' })
        .match({ 'valid_lineitems.0': { $exists: true } })
        .group({ _id: '$o_orderpriority', order_count: { $sum: 1 } })
        .project({ _id: 0, o_orderpriority: '$_id', order_count: 1 })
        .sort({ o_orderpriority: 1 })
        .exec();
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
 */
export function q5() {
    const startDate = new Date('1994-01-01');
    return CustomerR.aggregate()
        .lookup({ from: 'OrdersR', localField: '_id', foreignField: 'o_custkey', as: 'orders' })
        .unwind('$orders')
        .addFields({ endDate: { $dateAdd: { startDate, unit: 'year', amount: 1 } } })
        .match({ $expr: { $and: [{ $gte: ['$orders.o_orderdate', startDate] }, { $lt: ['$orders.o_orderdate', '$endDate'] }] } })
        .lookup({ from: 'LineitemR', localField: 'orders._id', foreignField: 'l_orderkey', as: 'lineitems' })
        .unwind('$lineitems')
        .lookup({ from: 'SupplierR', localField: 'lineitems.l_suppkey', foreignField: '_id', as: 'supplier' })
        .unwind('$supplier')
        .lookup({ from: 'NationR', localField: 'supplier.s_nationkey', foreignField: '_id', as: 'nation' })
        .unwind('$nation')
        .lookup({ from: 'RegionR', localField: 'nation.n_regionkey', foreignField: '_id', as: 'region' })
        .unwind('$region')
        .match({ $expr: { $and: [{ $eq: ['$c_nationkey', '$supplier.s_nationkey'] }, { $eq: ['$region.r_name', 'ASIA'] }] } })
        .group({ _id: '$nation.n_name', revenue: { $sum: { $multiply: ['$lineitems.l_extendedprice', { $subtract: [1, '$lineitems.l_discount'] }] } } })
        .project({ _id: 0, n_name: '$_id', revenue: 1 })
        .sort({ revenue: -1 })
        .exec();
}
