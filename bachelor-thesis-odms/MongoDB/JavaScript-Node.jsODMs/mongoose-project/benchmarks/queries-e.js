import CustomerEWithOrders from "./../models/tpc_h_e/customer-e-with-orders.js";
import OrdersEWithLineitems from "./../models/tpc_h_e/orders-e-with-lineitems.js";
import OrdersEWithLineitemsArrayAsTags from "./../models/tpc_h_e/orders-e-with-lineitems-array-as-tags.js";
import OrdersEWithLineitemsArrayAsTagsIndexed from "./../models/tpc_h_e/orders-e-with-lineitems-array-as-tags-indexed.js";
import OrdersEWithCustomerWithNationWithRegion from "./../models/tpc_h_e/orders-e-with-customer-with-nation-with-region.js";
import OrdersEOnlyOComment from "./../models/tpc_h_e/orders-e-only-o-comment.js";
import OrdersEOnlyOCommentIndexed from "./../models/tpc_h_e/orders-e-only-o-comment-indexed.js";

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

/**
 * ### R1) Embedded Orders with Lineitems Query
 *
 * Test performance of fetching nested documents (1:N relationship embedded).
 * ```MongoDB
 * db.ordersEWithLineitems.aggregate([
 *   { $match: { "o_lineitems.l_quantity": { $gt: 5 } } },
 *   { $project: { o_orderdate: 1, "o_lineitems.l_partkey": 1 } }
 * ])
 * ```
 */
async function R1() {
    const r1 = OrdersEWithLineitems.aggregate([
        { $match: { "o_lineitems.l_quantity": { $gt: 5 } } },
        {
            $project: {
                o_orderdate: 1,
                "o_lineitems.l_partkey": 1
            }
        }
    ]);
    return r1;
}

/**
 * ### R2) Embedded Orders with Lineitems Query — Indexed Field
 *
 * Test performance of fetching nested documents (1:N relationship embedded) on indexed field.
 * ```MongoDB
 * db.ordersEWithLineitems.aggregate([
 *   { $match: { "o_lineitems.l_partkey": { $gt: 20000 } } },
 *   { $project: { o_orderdate: 1, "o_lineitems.l_partkey": 1 } }
 * ])
 * ```
 */
async function R2() {
    const r2 = OrdersEWithLineitems.aggregate([
        { $match: { "o_lineitems.l_partkey": { $gt: 20000 } } },
        {
            $project: {
                o_orderdate: 1,
                "o_lineitems.l_partkey": 1
            }
        }
    ]);
    return r2;
}

/**
 * ### R3) Array Tags Query — Find Orders by Tag
 *
 * Test array indexing and filtering. Finds orders whose o_lineitems_tags array contains the value "MAIL".
 * ```MongoDB
 * db.ordersEWithLineitemsArrayAsTags.find(
 *   { o_lineitems_tags: "MAIL" },
 *   { o_orderdate: 1, o_lineitems_tags: 1 }
 * )
 * ```
 */
async function R3() {
    const r3 = OrdersEWithLineitemsArrayAsTags.find(
        { o_lineitems_tags: "MAIL" },
        { o_orderdate: 1, o_lineitems_tags: 1 }
    );
    return r3;
}

/**
 * ### R4) Indexed Array Tags Query — Find Orders by Tag
 *
 * Test array indexing and filtering on an indexed field. Finds orders whose o_lineitems_tags_indexed array contains the value "MAIL".
 * ```MongoDB
 * db.ordersEWithLineitemsArrayAsTagsIndexed.find(
 *   { o_lineitems_tags_indexed: "MAIL" },
 *   { o_orderdate: 1, o_lineitems_tags_indexed: 1 }
 * )
 * ```
 */
async function R4() {
    const r4 = OrdersEWithLineitemsArrayAsTagsIndexed.find(
        { o_lineitems_tags_indexed: "MAIL" },
        { o_orderdate: 1, o_lineitems_tags_indexed: 1 }
    );
    return r4;
}

/**
 * ### R5) Embedded Customer with Nation with Region — Filter by Region Name
 *
 * Test denormalization vs join simulation in documents.
 * Find all orders from customers in "AMERICA".
 * ```MongoDB
 * db.ordersEWithCustomerWithNationWithRegion.find(
 *   { "o_customer.c_nation.n_region.r_name": "AMERICA" }
 * )
 * ```
 */
async function R5() {
    const r5 = OrdersEWithCustomerWithNationWithRegion.find(
        { "o_customer.c_nation.n_region.r_name": "AMERICA" }
    );
    return r5;
}

/**
 * ### R6) Regex Text Search on Comment Field
 *
 * Simulate text search without an index.
 * ```MongoDB
 * db.ordersEOnlyOComment.find({ o_comment: /furiously/i })
 * ```
 */
async function R6() {
    const r6 = OrdersEOnlyOComment.find(
        { o_comment: /furiously/i }
    );
    return r6;
}

/**
 * ### R7) Text Index Search on Comment Field
 *
 * Simulate text search with a text index.
 * ```MongoDB
 * db.ordersEOnlyOCommentIndexed.find({ $text: { $search: "furiously" } })
 * ```
 */
async function R7() {
    const r7 = OrdersEOnlyOCommentIndexed.find(
        { $text: { $search: "furiously" } }
    );
    return r7;
}

/**
 * ### R8) Unwind Embedded Lineitems
 *
 * Test unwind of embedded objects (array flattening cost).
 * ```MongoDB
 * db.ordersEWithLineitems.aggregate([
 *   { $unwind: "$o_lineitems" },
 *   { $project: { _id: 1, "o_lineitems.l_partkey": 1 } }
 * ])
 * ```
 */
async function R8() {
    const r8 = OrdersEWithLineitems.aggregate([
        { $unwind: "$o_lineitems" },
        {
            $project: {
                "o_lineitems.l_partkey": 1
            }
        }
    ]);
    return r8;
}

/**
 * ### R9) Aggregation on Embedded Array — Sum Revenue per Order
 *
 * Test aggregation on embedded arrays ($unwind + $group interaction).
 * ```MongoDB
 * db.ordersEWithLineitems.aggregate([
 *   { $unwind: "$o_lineitems" },
 *   {
 *     $group: {
 *       _id: "$_id",
 *       totalRevenue: { $sum: "$o_lineitems.l_extendedprice" }
 *     }
 *   }
 * ])
 * ```
 */
async function R9() {
    const r9 = OrdersEWithLineitems.aggregate([
        { $unwind: "$o_lineitems" },
        {
            $group: {
                _id: "$_id",
                totalRevenue: { $sum: "$o_lineitems.l_extendedprice" }
            }
        }
    ]);
    return r9;
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