import ottoman from "ottoman";

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
    Ops/sec: 0.15
    Average time per op: 6655.253 ms
    */
    const c2 = await ottoman.getDefaultInstance().query(
        `SELECT
             c.c_name,
             o.o_orderdate,
             o.o_totalprice
         FROM ottoman_bucket_e.ottoman_scope_e.CustomerEWithOrders AS c
             UNNEST c.c_orders AS o;`
    )
    return c2.rows;
}

export {
    C2
}