import OrdersEWithLineitems                     from '../model/embedded/orders-e-with-lineitems.js';
import OrdersEWithLineitemsArrayAsTags          from '../model/embedded/orders-e-with-lineitems-array-as-tags.js';
import OrdersEWithLineitemsArrayAsTagsIndexed   from '../model/embedded/orders-e-with-lineitems-array-as-tags-indexed.js';
import OrdersEWithCustomerWithNationWithRegion  from '../model/embedded/orders-e-with-customer-with-nation-with-region.js';
import OrdersEOnlyOComment                      from '../model/embedded/orders-e-only-o-comment.js';
import OrdersEOnlyOCommentIndexed               from '../model/embedded/orders-e-only-o-comment-indexed.js';

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
export function r1() {
    return OrdersEWithLineitems.aggregate([
        { $match: { 'o_lineitems.l_quantity': { $gt: 5 } } },
        { $project: { o_orderdate: 1, 'o_lineitems.l_partkey': 1 } }
    ]);
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
export function r2() {
    return OrdersEWithLineitems.aggregate([
        { $match: { 'o_lineitems.l_partkey': { $gt: 20000 } } },
        { $project: { o_orderdate: 1, 'o_lineitems.l_partkey': 1 } }
    ]);
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
export function r3() {
    return OrdersEWithLineitemsArrayAsTags.find(
        { o_lineitems_tags: 'MAIL' },
        { o_orderdate: 1, o_lineitems_tags: 1 }
    );
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
export function r4() {
    return OrdersEWithLineitemsArrayAsTagsIndexed.find(
        { o_lineitems_tags_indexed: 'MAIL' },
        { o_orderdate: 1, o_lineitems_tags_indexed: 1 }
    );
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
export function r5() {
    return OrdersEWithCustomerWithNationWithRegion.find(
        { 'o_customer.c_nation.n_region.r_name': 'AMERICA' }
    );
}

/**
 * ### R6) Regex Text Search on Comment Field
 *
 * Simulate text search without an index.
 * ```MongoDB
 * db.ordersEOnlyOComment.find({ o_comment: /furiously/i })
 * ```
 */
export function r6() {
    return OrdersEOnlyOComment.find(
        { o_comment: /furiously/i }
    );
}

/**
 * ### R7) Text Index Search on Comment Field
 *
 * Simulate text search with a text index.
 * ```MongoDB
 * db.ordersEOnlyOCommentIndexed.find({ $text: { $search: "furiously" } })
 * ```
 */
export function r7() {
    return OrdersEOnlyOCommentIndexed.find(
        { $text: { $search: 'furiously' } }
    );
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
export function r8() {
    return OrdersEWithLineitems.aggregate([
        { $unwind: '$o_lineitems' },
        { $project: { 'o_lineitems.l_partkey': 1 } }
    ]);
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
export function r9() {
    return OrdersEWithLineitems.aggregate([
        { $unwind: '$o_lineitems' },
        { $group: { _id: '$_id', totalRevenue: { $sum: '$o_lineitems.l_extendedprice' } } }
    ]);
}
