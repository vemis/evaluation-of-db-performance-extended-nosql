import OrdersEWithLineitems                     from '../models/tpc_h_e/orders-e-with-lineitems.js';
import OrdersEWithLineitemsArrayAsTags          from '../models/tpc_h_e/orders-e-with-lineitems-array-as-tags.js';
import OrdersEWithLineitemsArrayAsTagsIndexed   from '../models/tpc_h_e/orders-e-with-lineitems-array-as-tags-indexed.js';
import OrdersEWithCustomerWithNationWithRegion  from '../models/tpc_h_e/orders-e-with-customer-with-nation-with-region.js';
import OrdersEOnlyOComment                      from '../models/tpc_h_e/orders-e-only-o-comment.js';
import OrdersEOnlyOCommentIndexed               from '../models/tpc_h_e/orders-e-only-o-comment-indexed.js';

export function r1() {
    return OrdersEWithLineitems.aggregate([
        { $match: { 'o_lineitems.l_quantity': { $gt: 5 } } },
        { $project: { o_orderdate: 1, 'o_lineitems.l_partkey': 1 } }
    ]);
}

export function r2() {
    return OrdersEWithLineitems.aggregate([
        { $match: { 'o_lineitems.l_partkey': { $gt: 20000 } } },
        { $project: { o_orderdate: 1, 'o_lineitems.l_partkey': 1 } }
    ]);
}

export function r3() {
    return OrdersEWithLineitemsArrayAsTags.find(
        { o_lineitems_tags: 'MAIL' },
        { o_orderdate: 1, o_lineitems_tags: 1 }
    );
}

export function r4() {
    return OrdersEWithLineitemsArrayAsTagsIndexed.find(
        { o_lineitems_tags_indexed: 'MAIL' },
        { o_orderdate: 1, o_lineitems_tags_indexed: 1 }
    );
}

export function r5() {
    return OrdersEWithCustomerWithNationWithRegion.find(
        { 'o_customer.c_nation.n_region.r_name': 'AMERICA' }
    );
}

export function r6() {
    return OrdersEOnlyOComment.find(
        { o_comment: /furiously/i }
    );
}

export function r7() {
    return OrdersEOnlyOCommentIndexed.find(
        { $text: { $search: 'furiously' } }
    );
}

export function r8() {
    return OrdersEWithLineitems.aggregate([
        { $unwind: '$o_lineitems' },
        { $project: { 'o_lineitems.l_partkey': 1 } }
    ]);
}

export function r9() {
    return OrdersEWithLineitems.aggregate([
        { $unwind: '$o_lineitems' },
        { $group: { _id: '$_id', totalRevenue: { $sum: '$o_lineitems.l_extendedprice' } } }
    ]);
}
