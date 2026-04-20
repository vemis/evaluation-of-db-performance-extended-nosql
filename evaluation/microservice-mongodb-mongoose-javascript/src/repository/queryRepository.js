import RegionR   from '../model/relational/region-r.js';
import NationR   from '../model/relational/nation-r.js';
import CustomerR from '../model/relational/customer-r.js';
import OrdersR   from '../model/relational/orders-r.js';
import PartR     from '../model/relational/part-r.js';
import PartsuppR from '../model/relational/partsupp-r.js';
import SupplierR from '../model/relational/supplier-r.js';
import LineitemR from '../model/relational/lineitem-r.js';

// A — Selection / Projection

export function a1() {
    return LineitemR.find();
}

export function a2(startDate, endDate) {
    return OrdersR.find().where('o_orderdate').gte(startDate).lte(endDate).exec();
}

export function a3() {
    return CustomerR.find();
}

export function a4(minOrderKey, maxOrderKey) {
    return OrdersR.find().where('_id').gte(minOrderKey).lte(maxOrderKey).exec();
}

// B — Aggregation

export function b1() {
    return OrdersR.aggregate()
        .group({
            _id: { $dateToString: { format: '%Y-%m', date: '$o_orderdate' } },
            order_count: { $sum: 1 }
        })
        .project({ _id: 0, order_month: '$_id', order_count: 1 })
        .exec();
}

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

export function c1() {
    return CustomerR.aggregate()
        .lookup({ from: 'OrdersR', pipeline: [], as: 'orders' })
        .unwind('$orders')
        .project({ _id: 0, c_name: 1, o_orderdate: '$orders.o_orderdate', o_totalprice: '$orders.o_totalprice' })
        .limit(1_500_000)
        .exec();
}

export function c2() {
    return CustomerR.aggregate()
        .lookup({ from: 'OrdersR', localField: '_id', foreignField: 'o_custkey', as: 'orders' })
        .unwind('$orders')
        .project({ _id: 0, c_name: 1, o_orderdate: '$orders.o_orderdate', o_totalprice: '$orders.o_totalprice' })
        .exec();
}

export function c3() {
    return CustomerR.aggregate()
        .lookup({ from: 'NationR', localField: 'c_nationkey', foreignField: '_id', as: 'nation' })
        .unwind('$nation')
        .lookup({ from: 'OrdersR', localField: '_id', foreignField: 'o_custkey', as: 'orders' })
        .unwind('$orders')
        .project({ _id: 0, c_name: 1, n_name: '$nation.n_name', o_orderdate: '$orders.o_orderdate', o_totalprice: '$orders.o_totalprice' })
        .exec();
}

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

export function c5() {
    return CustomerR.aggregate()
        .lookup({ from: 'OrdersR', localField: '_id', foreignField: 'o_custkey', as: 'orders' })
        .unwind({ path: '$orders', preserveNullAndEmptyArrays: true })
        .project({ _id: 0, c_custkey: '$_id', c_name: 1, o_orderkey: '$orders._id', o_orderdate: '$orders.o_orderdate' })
        .exec();
}

// D — Set operations

export function d1() {
    return CustomerR.aggregate()
        .project({ nationkey: '$c_nationkey', _id: 0 })
        .unionWith({ coll: 'SupplierR', pipeline: [{ $project: { nationkey: '$s_nationkey', _id: 0 } }] })
        .group({ _id: '$nationkey' })
        .project({ _id: 0, nationkey: '$_id' })
        .exec();
}

export function d2() {
    return CustomerR.aggregate()
        .lookup({ from: 'SupplierR', localField: '_id', foreignField: '_id', as: 'matched_supplier' })
        .match({ 'matched_supplier.0': { $exists: true } })
        .project({ _id: 0, c_custkey: '$_id' })
        .exec();
}

export function d3() {
    return CustomerR.aggregate()
        .lookup({ from: 'SupplierR', localField: '_id', foreignField: '_id', as: 'matched_supplier' })
        .match({ matched_supplier: { $eq: [] } })
        .project({ _id: 0, c_custkey: '$_id' })
        .exec();
}

// E — Result modification

export function e1() {
    return CustomerR.find({}, { _id: 0, c_name: 1, c_address: 1, c_acctbal: 1 })
        .sort({ c_acctbal: -1 })
        .exec();
}

export function e2() {
    return OrdersR.find({}, { _id: 1, o_custkey: 1, o_orderdate: 1, o_totalprice: 1 })
        .sort({ _id: 1 })
        .exec();
}

export function e3() {
    return CustomerR.aggregate()
        .group({ _id: { c_nationkey: '$c_nationkey', c_mktsegment: '$c_mktsegment' } })
        .project({ _id: 0, c_nationkey: '$_id.c_nationkey', c_mktsegment: '$_id.c_mktsegment' })
        .exec();
}

// Q — TPC-H benchmark queries

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
