import RegionR from "./../models/tpc_h_r/region-r.js";
import NationR from "./../models/tpc_h_r/nation-r.js";
import CustomerR from "./../models/tpc_h_r/customer-r.js";
import OrdersR from "./../models/tpc_h_r/orders-r.js";
import PartR from "./../models/tpc_h_r/part-r.js";
import PartsuppR from "./../models/tpc_h_r/partsupp-r.js";
import SupplierR from "./../models/tpc_h_r/supplier-r.js";
import LineitemR from "./../models/tpc_h_r/lineitem-r.js";


/**
 * A1) Non-Indexed Columns
 *
 * This query selects all records from the lineitem table
 * ```sql
 *         SELECT * FROM lineitem;
 * ```
 */
async function A1() {
    /*
    Ops/sec: 0.02
    Average time per op: 52809.167 ms
    */
    const a1 = LineitemR.find();

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
async function A2() {
    /*
    Ops/sec: 0.96
    Average time per op: 1041.984 ms
    */
    const a2 = OrdersR.find()
        .where('o_orderdate')
        .gte(new Date("1996-01-01"))
        .lte(new Date("1996-12-31"))
        .exec();

    return a2;
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
function B1() {
    /*
    Ops/sec: 3.75
    Average time per op: 266.713 ms
    */
    const b1 = OrdersR.aggregate()
        .group({
            _id: {
                $dateToString: {
                    format: "%Y-%m",
                    date: "$o_orderdate"
                }
            },
            order_count: { $sum: 1 }
        })
        .project({
            _id: 0,
            order_month: "$_id",
            order_count: 1
        })
        .exec();

    return b1
}

export {
    A1,
    A2,
    B1
}