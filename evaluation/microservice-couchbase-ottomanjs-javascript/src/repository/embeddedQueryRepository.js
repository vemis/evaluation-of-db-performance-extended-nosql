import { getDefaultInstance } from 'ottoman';

const B = '`bucket-main`.`ottoman_scope_e`';

function query(n1ql) {
    return getDefaultInstance().query(n1ql).then(r => r.rows);
}

/**
 * R1) Filter embedded array on non-indexed field (l_quantity > 5)
 */
export function r1() {
    return query(
        `SELECT o.o_orderdate,
                ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems
         FROM ${B}.\`OrdersEWithLineitems\` AS o
         WHERE ANY l IN o.o_lineitems SATISFIES l.l_quantity > 5 END`
    );
}

/**
 * R2) Filter embedded array on indexed field (l_partkey > 20000)
 */
export function r2() {
    return query(
        `SELECT o.o_orderdate,
                ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems
         FROM ${B}.\`OrdersEWithLineitems\` AS o
         WHERE ANY l IN o.o_lineitems SATISFIES l.l_partkey > 20000 END`
    );
}

/**
 * R3) Array tags filter — no index on o_lineitems_tags
 */
export function r3() {
    return query(
        `SELECT o.o_orderdate, o.o_lineitems_tags
         FROM ${B}.\`OrdersEWithLineitemsArrayAsTags\` AS o
         WHERE ANY tag IN o.o_lineitems_tags SATISFIES tag = 'MAIL' END`
    );
}

/**
 * R4) Array tags filter — indexed o_lineitems_tags_indexed
 */
export function r4() {
    return query(
        `SELECT o.o_orderdate, o.o_lineitems_tags_indexed
         FROM ${B}.\`OrdersEWithLineitemsArrayAsTagsIndexed\` AS o
         WHERE ANY tag IN o.o_lineitems_tags_indexed SATISFIES tag = 'MAIL' END`
    );
}

/**
 * R5) Deeply nested field filter (region name via denormalized customer)
 */
export function r5() {
    return query(
        `SELECT *
         FROM ${B}.\`OrdersEWithCustomerWithNationWithRegion\` AS o
         WHERE o.o_customer.c_nation.n_region.r_name = 'AMERICA'`
    );
}

/**
 * R6) Regex text search on o_comment (no index)
 */
export function r6() {
    return query(
        `SELECT *
         FROM ${B}.\`OrdersEOnlyOComment\` AS o
         WHERE REGEXP_CONTAINS(o.o_comment, '(?i)furiously')`
    );
}

/**
 * R7) Regex text search on o_comment (with index on o_comment field)
 */
export function r7() {
    return query(
        `SELECT *
         FROM ${B}.\`OrdersEOnlyOCommentIndexed\` AS o
         WHERE REGEXP_CONTAINS(o.o_comment, '(?i)furiously')`
    );
}

/**
 * R8) UNNEST embedded lineitems array (equivalent of $unwind)
 */
export function r8() {
    return query(
        `SELECT META(o).id AS o_orderkey, l.l_partkey
         FROM ${B}.\`OrdersEWithLineitems\` AS o
         UNNEST o.o_lineitems AS l`
    );
}

/**
 * R9) Aggregation on embedded array — sum revenue per order
 */
export function r9() {
    return query(
        `SELECT META(o).id AS o_orderkey, SUM(l.l_extendedprice) AS totalRevenue
         FROM ${B}.\`OrdersEWithLineitems\` AS o
         UNNEST o.o_lineitems AS l
         GROUP BY META(o).id`
    );
}
