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

/**
 * ### R1) Embedded Orders with Lineitems Query
 *
 * Test performance of fetching nested documents (1:N relationship embedded).
 * ```sql
 * SELECT o.o_orderdate,
 *        ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems
 * FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems AS o
 * WHERE ANY l IN o.o_lineitems SATISFIES l.l_quantity > 5 END
 * ```
 */
async function R1(){
    const r1 = await ottoman.getDefaultInstance().query(
        `SELECT o.o_orderdate,
                ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems
         FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems AS o
         WHERE ANY l IN o.o_lineitems SATISFIES l.l_quantity > 5 END`
    )
    return r1.rows;
}

/**
 * ### R2) Embedded Orders with Lineitems Query — Indexed Field
 *
 * Test performance of fetching nested documents (1:N relationship embedded) on indexed field.
 * Uses the array index idx_OrdersEWithLineitems_l_partkey on o_lineitems[].l_partkey.
 * ```sql
 * SELECT o.o_orderdate,
 *        ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems
 * FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems AS o
 * WHERE ANY l IN o.o_lineitems SATISFIES l.l_partkey > 20000 END
 * ```
 */
async function R2(){
    const r2 = await ottoman.getDefaultInstance().query(
        `SELECT o.o_orderdate,
                ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems
         FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems AS o
         WHERE ANY l IN o.o_lineitems SATISFIES l.l_partkey > 20000 END`
    )
    return r2.rows;
}

export {
    C2,
    R1,
    R2
}