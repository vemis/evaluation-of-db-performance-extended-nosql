import path from 'path';
import { connect, getDefaultInstance } from 'ottoman';
import { readDataFromCustomSeparator, createLineitemsTags, shuffleAndTruncate, runLoader,
         ensureCouchbaseScopeAndCollections, createCouchbaseOps, insertBatched } from 'common-js';

const BUCKET       = 'bucket-main';
const SCOPE        = 'ottoman_scope_e';
const SENTINEL_KEY = 'ottoman_load_e_complete';
const COLLECTIONS_E = [
    'OrdersEWithLineitems',
    'OrdersEWithLineitemsArrayAsTags',
    'OrdersEWithLineitemsArrayAsTagsIndexed',
    'OrdersEWithCustomerWithNationWithRegion',
    'OrdersEOnlyOComment',
    'OrdersEOnlyOCommentIndexed'
];
const BATCH_LARGE  = 500;
const BATCH_SMALL  = 2_500;

async function connectOttoman() {
    const host = process.env.COUCHBASE_HOST     || 'localhost';
    const user = process.env.COUCHBASE_USER     || 'Administrator';
    const pass = process.env.COUCHBASE_PASSWORD || 'password';
    await connect({
        connectionString: `couchbase://${host}`,
        bucketName: BUCKET,
        username: user,
        password: pass,
        timeouts: { kvTimeout: 20_000, queryTimeout: 300_000 }
    });
    console.log(`Connected to Couchbase: ${host}/${BUCKET}`);
    return getDefaultInstance().cluster;
}



async function createSecondaryIndexes(cluster) {
    const indexes = [
        `CREATE INDEX idx_e_lineitems_partkey IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`OrdersEWithLineitems\` (DISTINCT ARRAY l.l_partkey FOR l IN o_lineitems END)`,
        `CREATE INDEX idx_e_tags_indexed IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`OrdersEWithLineitemsArrayAsTagsIndexed\` (DISTINCT ARRAY tag FOR tag IN o_lineitems_tags_indexed END)`,
        `CREATE INDEX idx_e_comment_indexed IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`OrdersEOnlyOCommentIndexed\` (o_comment)`
    ];
    for (const idx of indexes) {
        try { await cluster.query(idx); } catch (e) {
            console.warn(`Index warning: ${e.message}`);
        }
    }
}

// ── public API ────────────────────────────────────────────────────────────────

export async function runEmbeddedLoader() {
    const cluster = await connectOttoman();

    // Dynamic imports ensure model() is called only after connect() registers the Ottoman instance
    const { OrdersEWithLineitems }                    = await import('../model/embedded/orders-e-with-lineitems.js');
    const { OrdersEWithLineitemsArrayAsTags }         = await import('../model/embedded/orders-e-with-lineitems-array-as-tags.js');
    const { OrdersEWithLineitemsArrayAsTagsIndexed }  = await import('../model/embedded/orders-e-with-lineitems-array-as-tags-indexed.js');
    const { OrdersEWithCustomerWithNationWithRegion } = await import('../model/embedded/orders-e-with-customer-with-nation-with-region.js');
    const { OrdersEOnlyOComment }                     = await import('../model/embedded/orders-e-only-o-comment.js');
    const { OrdersEOnlyOCommentIndexed }              = await import('../model/embedded/orders-e-only-o-comment-indexed.js');

    await ensureCouchbaseScopeAndCollections(cluster, BUCKET, SCOPE, COLLECTIONS_E);

    async function loadOrdersEWithLineitems(dataPath) {
        console.log('Loading OrdersEWithLineitems...');

        const lineitemsData = await readDataFromCustomSeparator(path.join(dataPath, 'lineitem.tbl'));
        const lineitemsByOrder = new Map();
        for (const row of lineitemsData) {
            const key = row[0];
            if (!lineitemsByOrder.has(key)) lineitemsByOrder.set(key, []);
            lineitemsByOrder.get(key).push({
                l_id:            row[0] + '|' + row[3],
                l_orderkey:      row[0],
                l_partkey:       Number(row[1]),
                l_suppkey:       Number(row[2]),
                l_ps_id:         row[1] + '|' + row[2],
                l_linenumber:    Number(row[3]),
                l_quantity:      Number(row[4]),
                l_extendedprice: Number(row[5]),
                l_discount:      Number(row[6]),
                l_tax:           Number(row[7]),
                l_returnflag:    row[8],
                l_linestatus:    row[9],
                l_shipdate:      row[10],
                l_commitdate:    row[11],
                l_receiptdate:   row[12],
                l_shipinstruct:  row[13],
                l_shipmode:      row[14],
                l_comment:       row[15]
            });
        }

        const ordersData = await readDataFromCustomSeparator(path.join(dataPath, 'orders.tbl'));
        const docs = ordersData.map(([id, o_custkey, o_orderstatus, o_totalprice, o_orderdate, o_orderpriority, o_clerk, o_shippriority, o_comment]) => ({
            id,
            o_custkey,
            o_orderstatus,
            o_totalprice:    Number(o_totalprice),
            o_orderdate,
            o_orderpriority,
            o_clerk,
            o_shippriority,
            o_comment,
            o_lineitems: lineitemsByOrder.get(id) ?? []
        }));

        await insertBatched(OrdersEWithLineitems, docs, BATCH_LARGE);
        console.log('OrdersEWithLineitems loaded');
    }

    async function loadOrdersEWithLineitemsArrayAsTags(dataPath) {
        console.log('Loading OrdersEWithLineitemsArrayAsTags...');

        const lineitemsData = await readDataFromCustomSeparator(path.join(dataPath, 'lineitem.tbl'));
        const baseTags = createLineitemsTags(lineitemsData[1]);

        const ordersData = await readDataFromCustomSeparator(path.join(dataPath, 'orders.tbl'));
        const docs = ordersData.map(([id, , , , o_orderdate]) => ({
            id,
            o_orderdate,
            o_lineitems_tags: shuffleAndTruncate(baseTags, Number(id))
        }));

        await insertBatched(OrdersEWithLineitemsArrayAsTags, docs, BATCH_SMALL);
        console.log('OrdersEWithLineitemsArrayAsTags loaded');
    }

    async function loadOrdersEWithLineitemsArrayAsTagsIndexed(dataPath) {
        console.log('Loading OrdersEWithLineitemsArrayAsTagsIndexed...');

        const lineitemsData = await readDataFromCustomSeparator(path.join(dataPath, 'lineitem.tbl'));
        const baseTags = createLineitemsTags(lineitemsData[1]);

        const ordersData = await readDataFromCustomSeparator(path.join(dataPath, 'orders.tbl'));
        const docs = ordersData.map(([id, , , , o_orderdate]) => ({
            id,
            o_orderdate,
            o_lineitems_tags_indexed: shuffleAndTruncate(baseTags, Number(id))
        }));

        await insertBatched(OrdersEWithLineitemsArrayAsTagsIndexed, docs, BATCH_SMALL);
        console.log('OrdersEWithLineitemsArrayAsTagsIndexed loaded');
    }

    async function loadOrdersEWithCustomerWithNationWithRegion(dataPath) {
        console.log('Loading OrdersEWithCustomerWithNationWithRegion...');

        const regionRows = await readDataFromCustomSeparator(path.join(dataPath, 'region.tbl'));
        const regionMap  = new Map(regionRows.map(r => [r[0], { r_regionkey: Number(r[0]), r_name: r[1] }]));

        const nationRows = await readDataFromCustomSeparator(path.join(dataPath, 'nation.tbl'));
        const nationMap  = new Map(nationRows.map(r => [r[0], {
            n_nationkey: Number(r[0]),
            n_name:      r[1],
            n_regionkey: Number(r[2]),
            n_region:    regionMap.get(r[2])
        }]));

        const customerRows = await readDataFromCustomSeparator(path.join(dataPath, 'customer.tbl'));
        const customerMap  = new Map(customerRows.map(r => [r[0], {
            c_custkey:   Number(r[0]),
            c_name:      r[1],
            c_nationkey: Number(r[3]),
            c_nation:    nationMap.get(r[3])
        }]));

        const ordersData = await readDataFromCustomSeparator(path.join(dataPath, 'orders.tbl'));
        const docs = ordersData.map(([id, o_custkey, , , o_orderdate]) => ({
            id,
            o_orderdate,
            o_customer: customerMap.get(o_custkey)
        }));

        await insertBatched(OrdersEWithCustomerWithNationWithRegion, docs, BATCH_SMALL);
        console.log('OrdersEWithCustomerWithNationWithRegion loaded');
    }

    async function loadOrdersEOnlyOComment(dataPath) {
        console.log('Loading OrdersEOnlyOComment...');

        const ordersData = await readDataFromCustomSeparator(path.join(dataPath, 'orders.tbl'));
        const docs = ordersData.map(([id, , , , o_orderdate, , , , o_comment]) => ({
            id, o_orderdate, o_comment
        }));

        await insertBatched(OrdersEOnlyOComment, docs, BATCH_SMALL);
        console.log('OrdersEOnlyOComment loaded');
    }

    async function loadOrdersEOnlyOCommentIndexed(dataPath) {
        console.log('Loading OrdersEOnlyOCommentIndexed...');

        const ordersData = await readDataFromCustomSeparator(path.join(dataPath, 'orders.tbl'));
        const docs = ordersData.map(([id, , , , o_orderdate, , , , o_comment]) => ({
            id, o_orderdate, o_comment
        }));

        await insertBatched(OrdersEOnlyOCommentIndexed, docs, BATCH_SMALL);
        console.log('OrdersEOnlyOCommentIndexed loaded');
    }

    return runLoader(
        SENTINEL_KEY,
        COLLECTIONS_E,
        async (dataPath) => {
            await loadOrdersEWithLineitems(dataPath);
            await loadOrdersEWithLineitemsArrayAsTags(dataPath);
            await loadOrdersEWithLineitemsArrayAsTagsIndexed(dataPath);
            await loadOrdersEWithCustomerWithNationWithRegion(dataPath);
            await loadOrdersEOnlyOComment(dataPath);
            await loadOrdersEOnlyOCommentIndexed(dataPath);
            await createSecondaryIndexes(cluster);
        },
        'Embedded data (Ottoman.js)',
        createCouchbaseOps(cluster, BUCKET, SCOPE)
    );
}
