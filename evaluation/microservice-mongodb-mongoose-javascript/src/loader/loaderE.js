import path from 'path';
import OrdersEWithLineitems                    from '../model/embedded/orders-e-with-lineitems.js';
import OrdersEWithLineitemsArrayAsTags         from '../model/embedded/orders-e-with-lineitems-array-as-tags.js';
import OrdersEWithLineitemsArrayAsTagsIndexed  from '../model/embedded/orders-e-with-lineitems-array-as-tags-indexed.js';
import OrdersEWithCustomerWithNationWithRegion from '../model/embedded/orders-e-with-customer-with-nation-with-region.js';
import OrdersEOnlyOComment                     from '../model/embedded/orders-e-only-o-comment.js';
import OrdersEOnlyOCommentIndexed              from '../model/embedded/orders-e-only-o-comment-indexed.js';
import mongoose from 'mongoose';
import { readDataFromCustomSeparator, createLineitemsTags, shuffleAndTruncate, runLoader, createMongoOps } from 'common-js';

const EMBEDDED_COLLECTIONS = [
    'ordersEWithLineitems',
    'ordersEWithLineitemsArrayAsTags',
    'ordersEWithLineitemsArrayAsTagsIndexed',
    'ordersEWithCustomerWithNationWithRegion',
    'ordersEOnlyOComment',
    'ordersEOnlyOCommentIndexed'
];
const SENTINEL_ID = 'load_e_complete';
const BATCH = 200;

async function insertBatched(Model, docs, batchSize = BATCH) {
    for (let i = 0; i < docs.length; i += batchSize) {
        await Model.insertMany(docs.slice(i, i + batchSize));
        console.log(`  batch ${Math.floor(i / batchSize) + 1}/${Math.ceil(docs.length / batchSize)}`);
    }
}

async function loadOrdersEWithLineitems(filePathOrders, filePathLineitems) {
    console.log('Loading ordersEWithLineitems...');

    const lineitemsData = await readDataFromCustomSeparator(filePathLineitems);

    const lineitemsByOrder = new Map();
    for (const row of lineitemsData) {
        const key = Number(row[0]);
        if (!lineitemsByOrder.has(key)) lineitemsByOrder.set(key, []);
        lineitemsByOrder.get(key).push({
            l_id: row[0] + row[3],
            l_orderkey: Number(row[0]),
            l_partkey: Number(row[1]),
            l_suppkey: Number(row[2]),
            l_ps_id: row[1] + '|' + row[2],
            l_linenumber: Number(row[3]),
            l_quantity: Number(row[4]),
            l_extendedprice: Number(row[5]),
            l_discount: Number(row[6]),
            l_tax: Number(row[7]),
            l_returnflag: row[8],
            l_linestatus: row[9],
            l_shipdate: new Date(row[10]),
            l_commitdate: new Date(row[11]),
            l_receiptdate: new Date(row[12]),
            l_shipinstruct: row[13],
            l_shipmode: row[14],
            l_comment: row[15]
        });
    }

    const ordersData = await readDataFromCustomSeparator(filePathOrders);

    const rowsOfSchemas = ordersData.map(row => ({
        _id: Number(row[0]),
        o_custkey: Number(row[1]),
        o_orderstatus: row[2],
        o_totalprice: row[3],
        o_orderdate: new Date(row[4]),
        o_orderpriority: row[5],
        o_clerk: row[6],
        o_shippriority: row[7],
        o_comment: row[8],
        o_lineitems: lineitemsByOrder.get(Number(row[0])) ?? []
    }));

    await insertBatched(OrdersEWithLineitems, rowsOfSchemas);
    console.log('ordersEWithLineitems loaded');
}

async function loadOrdersEWithLineitemsArrayAsTags(filePathOrders, filePathLineitems) {
    console.log('Loading ordersEWithLineitemsArrayAsTags...');

    const ordersData    = await readDataFromCustomSeparator(filePathOrders);
    const lineitemsData = await readDataFromCustomSeparator(filePathLineitems);

    const lineitemRow2 = lineitemsData[1];

    const rowsOfSchemas = ordersData.map(row => ({
        _id: Number(row[0]),
        o_orderdate: new Date(row[4]),
        o_lineitems_tags: shuffleAndTruncate(createLineitemsTags(lineitemRow2), Number(row[0]))
    }));

    await insertBatched(OrdersEWithLineitemsArrayAsTags, rowsOfSchemas);
    console.log('ordersEWithLineitemsArrayAsTags loaded');
}

async function loadOrdersEWithLineitemsArrayAsTagsIndexed(filePathOrders, filePathLineitems) {
    console.log('Loading ordersEWithLineitemsArrayAsTagsIndexed...');

    const ordersData    = await readDataFromCustomSeparator(filePathOrders);
    const lineitemsData = await readDataFromCustomSeparator(filePathLineitems);

    const lineitemRow2 = lineitemsData[1];

    const rowsOfSchemas = ordersData.map(row => ({
        _id: Number(row[0]),
        o_orderdate: new Date(row[4]),
        o_lineitems_tags_indexed: shuffleAndTruncate(createLineitemsTags(lineitemRow2), Number(row[0]))
    }));

    await insertBatched(OrdersEWithLineitemsArrayAsTagsIndexed, rowsOfSchemas);
    console.log('ordersEWithLineitemsArrayAsTagsIndexed loaded');
}

async function loadOrdersEWithCustomerWithNationWithRegion(filePathOrders, filePathCustomers, filePathNations, filePathRegions) {
    console.log('Loading ordersEWithCustomerWithNationWithRegion...');

    const regionRows   = await readDataFromCustomSeparator(filePathRegions);
    const nationRows   = await readDataFromCustomSeparator(filePathNations);
    const customerRows = await readDataFromCustomSeparator(filePathCustomers);
    const ordersData   = await readDataFromCustomSeparator(filePathOrders);

    const regionMap = new Map(regionRows.map(r => [
        Number(r[0]),
        { r_regionkey: Number(r[0]), r_name: r[1] }
    ]));

    const nationMap = new Map(nationRows.map(r => {
        const n_regionkey = Number(r[2]);
        return [Number(r[0]), {
            n_nationkey: Number(r[0]),
            n_name: r[1],
            n_regionkey,
            n_region: regionMap.get(n_regionkey)
        }];
    }));

    const customerMap = new Map(customerRows.map(r => {
        const c_nationkey = Number(r[3]);
        return [Number(r[0]), {
            c_custkey: Number(r[0]),
            c_name: r[1],
            c_nationkey,
            c_nation: nationMap.get(c_nationkey)
        }];
    }));

    const rowsOfSchemas = ordersData.map(row => ({
        _id: Number(row[0]),
        o_orderdate: new Date(row[4]),
        o_customer: customerMap.get(Number(row[1]))
    }));

    await insertBatched(OrdersEWithCustomerWithNationWithRegion, rowsOfSchemas);
    console.log('ordersEWithCustomerWithNationWithRegion loaded');
}

async function loadOrdersEOnlyOComment(filePathOrders) {
    console.log('Loading ordersEOnlyOComment...');

    const ordersData = await readDataFromCustomSeparator(filePathOrders);

    const rowsOfSchemas = ordersData.map(row => ({
        _id: Number(row[0]),
        o_orderdate: new Date(row[4]),
        o_comment: row[8]
    }));

    await insertBatched(OrdersEOnlyOComment, rowsOfSchemas);
    console.log('ordersEOnlyOComment loaded');
}

async function loadOrdersEOnlyOCommentIndexed(filePathOrders) {
    console.log('Loading ordersEOnlyOCommentIndexed...');

    const ordersData = await readDataFromCustomSeparator(filePathOrders);

    const rowsOfSchemas = ordersData.map(row => ({
        _id: Number(row[0]),
        o_orderdate: new Date(row[4]),
        o_comment: row[8]
    }));

    await insertBatched(OrdersEOnlyOCommentIndexed, rowsOfSchemas);
    console.log('ordersEOnlyOCommentIndexed loaded');
}

async function loadAll(dataPath) {
    const p = f => path.join(dataPath, f);

    await loadOrdersEWithLineitems(p('orders.tbl'), p('lineitem.tbl'));
    await loadOrdersEWithLineitemsArrayAsTags(p('orders.tbl'), p('lineitem.tbl'));
    await loadOrdersEWithLineitemsArrayAsTagsIndexed(p('orders.tbl'), p('lineitem.tbl'));
    await loadOrdersEWithCustomerWithNationWithRegion(p('orders.tbl'), p('customer.tbl'), p('nation.tbl'), p('region.tbl'));
    await loadOrdersEOnlyOComment(p('orders.tbl'));
    await loadOrdersEOnlyOCommentIndexed(p('orders.tbl'));
}

export async function runEmbeddedLoader() {
    return runLoader(SENTINEL_ID, EMBEDDED_COLLECTIONS, loadAll, 'Embedded data', createMongoOps(mongoose.connection.db));
}
