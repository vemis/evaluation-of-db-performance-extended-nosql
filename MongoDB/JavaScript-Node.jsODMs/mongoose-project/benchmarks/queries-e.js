import CustomerEWithOrders from "./../models/tpc_h_e/customer-e-with-orders.js";

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
    Ops/sec: 0.46
    Average time per op: 2190.327 ms
    */
    const c2 = CustomerEWithOrders.aggregate([
        { $unwind: "$c_orders" },
        {
            $project: {
                _id: 0,
                c_name: 1,
                o_orderdate: "$c_orders.o_orderdate",
                o_totalprice: "$c_orders.o_totalprice"
            }
        }
    ]);
    return c2
}

export {
    C2
}