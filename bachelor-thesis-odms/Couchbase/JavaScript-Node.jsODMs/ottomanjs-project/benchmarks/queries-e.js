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

/**
 * ### R3) Array Tags Query — Find Orders by Tag
 *
 * Test array filtering without an index. Finds orders whose o_lineitems_tags
 * array contains the value "MAIL".
 * ```sql
 * SELECT o.o_orderdate, o.o_lineitems_tags
 * FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitemsArrayAsTags AS o
 * WHERE ANY tag IN o.o_lineitems_tags SATISFIES tag = 'MAIL' END
 * ```
 */
async function R3(){
    const r3 = await ottoman.getDefaultInstance().query(
        `SELECT o.o_orderdate, o.o_lineitems_tags
         FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitemsArrayAsTags AS o
         WHERE ANY tag IN o.o_lineitems_tags SATISFIES tag = 'MAIL' END`
    )
    return r3.rows;
}

/**
 * ### R4) Array Tags Query — Find Orders by Tag (Indexed)
 *
 * Test array filtering with a Couchbase array index. Same filter as R3 but on
 * `OrdersEWithLineitemsArrayAsTagsIndexed` which has an array index on
 * `o_lineitems_tags_indexed`.
 * ```sql
 * SELECT o.o_orderdate, o.o_lineitems_tags_indexed
 * FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitemsArrayAsTagsIndexed AS o
 * WHERE ANY tag IN o.o_lineitems_tags_indexed SATISFIES tag = 'MAIL' END
 * ```
 */
async function R4(){
    const r4 = await ottoman.getDefaultInstance().query(
        `SELECT o.o_orderdate, o.o_lineitems_tags_indexed
         FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitemsArrayAsTagsIndexed AS o
         WHERE ANY tag IN o.o_lineitems_tags_indexed SATISFIES tag = 'MAIL' END`
    )
    return r4.rows;
}

/**
 * ### R5) Embedded Customer with Nation with Region — Filter by Region Name
 *
 * Test denormalization depth. Find all orders from customers in "AMERICA".
 * Uses the nested path `o_customer.c_nation.n_region.r_name` with no dedicated index.
 * ```sql
 * SELECT *
 * FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithCustomerWithNationWithRegion AS o
 * WHERE o.o_customer.c_nation.n_region.r_name = 'AMERICA'
 * ```
 */
async function R5(){
    const r5 = await ottoman.getDefaultInstance().query(
        `SELECT *
         FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithCustomerWithNationWithRegion AS o
         WHERE o.o_customer.c_nation.n_region.r_name = 'AMERICA'`
    )
    return r5.rows;
}

/**
 * ### R6) Regex Text Search on Comment Field
 *
 * Simulate text search without an index using REGEXP_CONTAINS.
 * Couchbase N1QL equivalent of MongoDB's `{ o_comment: /furiously/i }` regex filter.
 * ```sql
 * SELECT *
 * FROM ottoman_bucket_e.ottoman_scope_e.OrdersEOnlyOComment AS o
 * WHERE REGEXP_CONTAINS(o.o_comment, '(?i)furiously')
 * ```
 */
async function R6(){
    const r6 = await ottoman.getDefaultInstance().query(
        `SELECT *
         FROM ottoman_bucket_e.ottoman_scope_e.OrdersEOnlyOComment AS o
         WHERE REGEXP_CONTAINS(o.o_comment, '(?i)furiously')`
    )
    return r6.rows;
}

/**
 * ### R7) Text Index Search on Comment Field
 *
 * Simulate text search with an index on o_comment.
 * Couchbase does not have a MongoDB-style text index for N1QL; a regular index on o_comment
 * is present on this collection. REGEXP_CONTAINS with a non-anchored pattern cannot use a
 * B-tree index — for true full-text search acceleration Couchbase requires an FTS index
 * with the SEARCH() function, which is a separate service.
 * ```sql
 * SELECT *
 * FROM ottoman_bucket_e.ottoman_scope_e.OrdersEOnlyOCommentIndexed AS o
 * WHERE REGEXP_CONTAINS(o.o_comment, '(?i)furiously')
 * ```
 */
async function R7(){
    const r7 = await ottoman.getDefaultInstance().query(
        `SELECT *
         FROM ottoman_bucket_e.ottoman_scope_e.OrdersEOnlyOCommentIndexed AS o
         WHERE REGEXP_CONTAINS(o.o_comment, '(?i)furiously')`
    )
    return r7.rows;
}

/**
 * ### R8) Unwind Embedded Lineitems
 *
 * Test array flattening cost (equivalent of MongoDB $unwind).
 * In Couchbase N1QL, UNNEST replaces $unwind — it produces one output row per array element.
 * ```sql
 * SELECT META(o).id AS o_orderkey, l.l_partkey
 * FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems AS o
 * UNNEST o.o_lineitems AS l
 * ```
 */
async function R8(){
    const r8 = await ottoman.getDefaultInstance().query(
        `SELECT META(o).id AS o_orderkey, l.l_partkey
         FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems AS o
         UNNEST o.o_lineitems AS l`
    )
    return r8.rows;
}

/**
 * ### R9) Aggregation on Embedded Array — Sum Revenue per Order
 *
 * Test aggregation on embedded arrays (equivalent of MongoDB $unwind + $group + $sum).
 * In Couchbase N1QL, UNNEST replaces $unwind and GROUP BY + SUM replaces $group + $sum.
 * ```sql
 * SELECT META(o).id AS o_orderkey, SUM(l.l_extendedprice) AS totalRevenue
 * FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems AS o
 * UNNEST o.o_lineitems AS l
 * GROUP BY META(o).id
 * ```
 */
async function R9(){
    const r9 = await ottoman.getDefaultInstance().query(
        `SELECT META(o).id AS o_orderkey, SUM(l.l_extendedprice) AS totalRevenue
         FROM ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems AS o
         UNNEST o.o_lineitems AS l
         GROUP BY META(o).id`
    )
    return r9.rows;
}

export {
    C2,
    R1,
    R2,
    R3,
    R4,
    R5,
    R6,
    R7,
    R8,
    R9
}