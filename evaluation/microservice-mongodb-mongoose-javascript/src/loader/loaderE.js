import fs from 'fs';
import path from 'path';
import mongoose from 'mongoose';
import seedrandom from 'seedrandom';
import OrdersEWithLineitems                    from '../models/tpc_h_e/orders-e-with-lineitems.js';
import OrdersEWithLineitemsArrayAsTags         from '../models/tpc_h_e/orders-e-with-lineitems-array-as-tags.js';
import OrdersEWithLineitemsArrayAsTagsIndexed  from '../models/tpc_h_e/orders-e-with-lineitems-array-as-tags-indexed.js';
import OrdersEWithCustomerWithNationWithRegion from '../models/tpc_h_e/orders-e-with-customer-with-nation-with-region.js';
import OrdersEOnlyOComment                     from '../models/tpc_h_e/orders-e-only-o-comment.js';
import OrdersEOnlyOCommentIndexed              from '../models/tpc_h_e/orders-e-only-o-comment-indexed.js';

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

async function readTbl(filePath) {
    const data = await fs.promises.readFile(filePath, 'utf8');
    const rows = data.split('\n');
    if (rows.length > 1 && rows[rows.length - 1] === '') rows.pop();
    return rows.map(r => r.split('|'));
}

async function insertBatched(Model, docs, batchSize = BATCH) {
    for (let i = 0; i < docs.length; i += batchSize) {
        await Model.insertMany(docs.slice(i, i + batchSize));
        console.log(`  batch ${Math.floor(i / batchSize) + 1}/${Math.ceil(docs.length / batchSize)}`);
    }
}

function createLineitemsTags(row) {
    return [
        Number(row[0]), Number(row[1]), Number(row[2]), Number(row[3]),
        Number(row[4]), Number(row[5]), Number(row[6]), Number(row[7]),
        row[8], row[9],
        new Date(row[10]), new Date(row[11]), new Date(row[12]),
        row[13], row[14], row[15]
    ];
}

// Seeded Fisher-Yates shuffle + random prefix length — must match Java implementation exactly
function shuffleAndTruncate(tags, seed) {
    const rng  = seedrandom(seed);
    const list = [...tags];
    for (let i = list.length - 1; i > 0; i--) {
        const j = Math.floor(rng() * (i + 1));
        [list[i], list[j]] = [list[j], list[i]];
    }
    const size = 1 + Math.floor(rng() * list.length);
    return list.slice(0, size);
}

async function isAlreadyLoaded() {
    return (await mongoose.connection.db.collection('_metadata').findOne({ _id: SENTINEL_ID })) !== null;
}

async function dropCollections() {
    for (const col of EMBEDDED_COLLECTIONS) {
        try { await mongoose.connection.db.collection(col).drop(); } catch (_) {}
    }
}

async function loadAll(dataPath) {
    const p = f => path.join(dataPath, f);

    const ordersData   = await readTbl(p('orders.tbl'));
    const lineitemsData = await readTbl(p('lineitem.tbl'));

    // ordersEWithLineitems
    console.log('Loading ordersEWithLineitems...');
    const lineitemsByOrder = new Map();
    for (const row of lineitemsData) {
        const key = Number(row[0]);
        if (!lineitemsByOrder.has(key)) lineitemsByOrder.set(key, []);
        lineitemsByOrder.get(key).push({
            l_id: row[0] + row[3],
            l_orderkey: Number(row[0]), l_partkey: Number(row[1]), l_suppkey: Number(row[2]),
            l_ps_id: row[1] + '|' + row[2],
            l_linenumber: Number(row[3]), l_quantity: Number(row[4]),
            l_extendedprice: Number(row[5]), l_discount: Number(row[6]), l_tax: Number(row[7]),
            l_returnflag: row[8], l_linestatus: row[9],
            l_shipdate: new Date(row[10]), l_commitdate: new Date(row[11]), l_receiptdate: new Date(row[12]),
            l_shipinstruct: row[13], l_shipmode: row[14], l_comment: row[15]
        });
    }
    const ordersWithLineitems = ordersData.map(row => ({
        _id: Number(row[0]), o_custkey: Number(row[1]), o_orderstatus: row[2], o_totalprice: row[3],
        o_orderdate: new Date(row[4]), o_orderpriority: row[5], o_clerk: row[6], o_shippriority: row[7], o_comment: row[8],
        o_lineitems: lineitemsByOrder.get(Number(row[0])) ?? []
    }));
    await insertBatched(OrdersEWithLineitems, ordersWithLineitems);

    // ordersEWithLineitemsArrayAsTags
    console.log('Loading ordersEWithLineitemsArrayAsTags...');
    const lineitemRow2Tags = createLineitemsTags(lineitemsData[1]);
    const ordersAsTags = ordersData.map(row => ({
        _id: Number(row[0]),
        o_orderdate: new Date(row[4]),
        o_lineitems_tags: shuffleAndTruncate(lineitemRow2Tags, Number(row[0]))
    }));
    await insertBatched(OrdersEWithLineitemsArrayAsTags, ordersAsTags);

    // ordersEWithLineitemsArrayAsTagsIndexed
    console.log('Loading ordersEWithLineitemsArrayAsTagsIndexed...');
    const ordersAsTagsIndexed = ordersData.map(row => ({
        _id: Number(row[0]),
        o_orderdate: new Date(row[4]),
        o_lineitems_tags_indexed: shuffleAndTruncate(lineitemRow2Tags, Number(row[0]))
    }));
    await insertBatched(OrdersEWithLineitemsArrayAsTagsIndexed, ordersAsTagsIndexed);

    // ordersEWithCustomerWithNationWithRegion
    console.log('Loading ordersEWithCustomerWithNationWithRegion...');
    const regionRows   = await readTbl(p('region.tbl'));
    const nationRows   = await readTbl(p('nation.tbl'));
    const customerRows = await readTbl(p('customer.tbl'));

    const regionMap = new Map(regionRows.map(r => [Number(r[0]), { r_regionkey: Number(r[0]), r_name: r[1] }]));
    const nationMap = new Map(nationRows.map(r => {
        const n_regionkey = Number(r[2]);
        return [Number(r[0]), { n_nationkey: Number(r[0]), n_name: r[1], n_regionkey, n_region: regionMap.get(n_regionkey) }];
    }));
    const customerMap = new Map(customerRows.map(r => {
        const c_nationkey = Number(r[3]);
        return [Number(r[0]), { c_custkey: Number(r[0]), c_name: r[1], c_nationkey, c_nation: nationMap.get(c_nationkey) }];
    }));
    const ordersWithCustomer = ordersData.map(row => ({
        _id: Number(row[0]),
        o_orderdate: new Date(row[4]),
        o_customer: customerMap.get(Number(row[1]))
    }));
    await insertBatched(OrdersEWithCustomerWithNationWithRegion, ordersWithCustomer);

    // ordersEOnlyOComment
    console.log('Loading ordersEOnlyOComment...');
    const ordersOnlyComment = ordersData.map(row => ({
        _id: Number(row[0]), o_orderdate: new Date(row[4]), o_comment: row[8]
    }));
    await insertBatched(OrdersEOnlyOComment, ordersOnlyComment);

    // ordersEOnlyOCommentIndexed
    console.log('Loading ordersEOnlyOCommentIndexed...');
    const ordersOnlyCommentIndexed = ordersData.map(row => ({
        _id: Number(row[0]), o_orderdate: new Date(row[4]), o_comment: row[8]
    }));
    await insertBatched(OrdersEOnlyOCommentIndexed, ordersOnlyCommentIndexed);
}

async function insertSentinel() {
    await mongoose.connection.db.collection('_metadata').insertOne({ _id: SENTINEL_ID });
}

export async function runEmbeddedLoader() {
    const dataPath = process.env.TPCH_DATA_PATH || '/data/tpch-data-small';
    if (await isAlreadyLoaded()) {
        console.log('Embedded data already loaded, skipping.');
        return 'Embedded data already loaded, skipping.';
    }
    await dropCollections();
    await loadAll(dataPath);
    await insertSentinel();
    const msg = `Embedded data loaded from: ${dataPath}`;
    console.log(msg);
    return msg;
}
