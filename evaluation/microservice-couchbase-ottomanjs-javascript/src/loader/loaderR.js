import path from 'path';
import { connect } from 'couchbase';
import { readDataFromCustomSeparator, runLoader,
         upsertBatch, upsertAll,
         ensureCouchbaseScopeAndCollections, createCouchbaseOps } from 'common-js';

async function getCluster() {
    const host = process.env.COUCHBASE_HOST     || 'localhost';
    const user = process.env.COUCHBASE_USER     || 'Administrator';
    const pass = process.env.COUCHBASE_PASSWORD || 'password';
    return connect(`couchbase://${host}`, {
        username: user,
        password: pass,
        timeouts: { kvTimeout: 20_000, queryTimeout: 300_000 }
    });
}

const BUCKET        = 'bucket-main';
const SCOPE         = 'ottoman_scope_r';
const SENTINEL_KEY  = 'ottoman_load_r_complete';
const COLLECTIONS_R = ['RegionR', 'NationR', 'CustomerR', 'OrdersR', 'LineitemR', 'PartsuppR', 'SupplierR', 'PartR'];
const BATCH         = 2_500;

async function createSecondaryIndexes(cluster) {
    const indexes = [
        `CREATE INDEX idx_r_nation_regionkey    IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`NationR\`    (n_regionkey)`,
        `CREATE INDEX idx_r_customer_nationkey  IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`CustomerR\`  (c_nationkey)`,
        `CREATE INDEX idx_r_orders_custkey      IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`OrdersR\`    (o_custkey)`,
        `CREATE INDEX idx_r_orders_orderkey_num IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`OrdersR\`    (o_orderkey_field)`,
        `CREATE INDEX idx_r_orders_orderdate    IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`OrdersR\`    (o_orderdate)`,
        `CREATE INDEX idx_r_lineitem_orderkey   IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`LineitemR\`  (l_orderkey)`,
        `CREATE INDEX idx_r_lineitem_partkey    IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`LineitemR\`  (l_partkey)`,
        `CREATE INDEX idx_r_lineitem_suppkey    IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`LineitemR\`  (l_suppkey)`,
        `CREATE INDEX idx_r_lineitem_ps_id      IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`LineitemR\`  (l_ps_id)`,
        `CREATE INDEX idx_r_lineitem_shipdate   IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`LineitemR\`  (l_shipdate)`,
        `CREATE INDEX idx_r_partsupp_partkey    IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`PartsuppR\`  (ps_partkey)`,
        `CREATE INDEX idx_r_partsupp_suppkey    IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`PartsuppR\`  (ps_suppkey)`,
        `CREATE INDEX idx_r_supplier_nationkey  IF NOT EXISTS ON \`${BUCKET}\`.\`${SCOPE}\`.\`SupplierR\`  (s_nationkey)`
    ];
    for (const idx of indexes) {
        try { await cluster.query(idx); } catch (e) {
            console.warn(`Index warning: ${e.message}`);
        }
    }
}

// ── loaders ───────────────────────────────────────────────────────────────────

async function loadRegions(cluster, dataPath) {
    const rows = await readDataFromCustomSeparator(path.join(dataPath, 'region.tbl'));
    const coll = cluster.bucket(BUCKET).scope(SCOPE).collection('RegionR');
    const docs = rows.map(([id, r_name, r_comment]) => ({ id, r_name, r_comment }));
    await upsertBatch(coll, docs);
    console.log('RegionR loaded');
}

async function loadNations(cluster, dataPath) {
    const rows = await readDataFromCustomSeparator(path.join(dataPath, 'nation.tbl'));
    const coll = cluster.bucket(BUCKET).scope(SCOPE).collection('NationR');
    const docs = rows.map(([id, n_name, n_regionkey, n_comment]) => ({ id, n_name, n_regionkey, n_comment }));
    await upsertBatch(coll, docs);
    console.log('NationR loaded');
}

async function loadCustomers(cluster, dataPath) {
    const rows = await readDataFromCustomSeparator(path.join(dataPath, 'customer.tbl'));
    const coll = cluster.bucket(BUCKET).scope(SCOPE).collection('CustomerR');
    const docs = rows.map(([id, c_name, c_address, c_nationkey, c_phone, c_acctbal, c_mktsegment, c_commen]) => ({
        id, c_name, c_address, c_nationkey, c_phone,
        c_acctbal: Number(c_acctbal), c_mktsegment, c_commen
    }));
    await upsertAll(coll, docs, BATCH);
    console.log('CustomerR loaded');
}

async function loadOrders(cluster, dataPath) {
    const rows = await readDataFromCustomSeparator(path.join(dataPath, 'orders.tbl'));
    const coll = cluster.bucket(BUCKET).scope(SCOPE).collection('OrdersR');
    const docs = rows.map(([id, o_custkey, o_orderstatus, o_totalprice, o_orderdate, o_orderpriority, o_clerk, o_shippriority, o_comment]) => ({
        id,
        o_orderkey_field: Number(id),
        o_custkey,
        o_orderstatus,
        o_totalprice: Number(o_totalprice),
        o_orderdate,
        o_orderpriority,
        o_clerk,
        o_shippriority,
        o_comment
    }));
    await upsertAll(coll, docs, BATCH);
    console.log('OrdersR loaded');
}

async function loadLineitems(cluster, dataPath) {
    const rows = await readDataFromCustomSeparator(path.join(dataPath, 'lineitem.tbl'));
    const coll = cluster.bucket(BUCKET).scope(SCOPE).collection('LineitemR');
    const docs = rows.map(([l_orderkey, l_partkey, l_suppkey, l_linenumber, l_quantity, l_extendedprice, l_discount, l_tax, l_returnflag, l_linestatus, l_shipdate, l_commitdate, l_receiptdate, l_shipinstruct, l_shipmode, l_comment]) => ({
        id: l_orderkey + '|' + l_linenumber,
        l_orderkey,
        l_partkey,
        l_suppkey,
        l_ps_id: l_partkey + '|' + l_suppkey,
        l_linenumber:    Number(l_linenumber),
        l_quantity:      Number(l_quantity),
        l_extendedprice: Number(l_extendedprice),
        l_discount:      Number(l_discount),
        l_tax:           Number(l_tax),
        l_returnflag,
        l_linestatus,
        l_shipdate,
        l_commitdate,
        l_receiptdate,
        l_shipinstruct,
        l_shipmode,
        l_comment
    }));
    await upsertAll(coll, docs, BATCH);
    console.log('LineitemR loaded');
}

async function loadPartsupps(cluster, dataPath) {
    const rows = await readDataFromCustomSeparator(path.join(dataPath, 'partsupp.tbl'));
    const coll = cluster.bucket(BUCKET).scope(SCOPE).collection('PartsuppR');
    const docs = rows.map(([ps_partkey, ps_suppkey, ps_availqty, ps_supplycost, ps_comment]) => ({
        id: ps_partkey + '|' + ps_suppkey,
        ps_partkey,
        ps_suppkey,
        ps_availqty:   Number(ps_availqty),
        ps_supplycost: Number(ps_supplycost),
        ps_comment
    }));
    await upsertAll(coll, docs, BATCH);
    console.log('PartsuppR loaded');
}

async function loadSuppliers(cluster, dataPath) {
    const rows = await readDataFromCustomSeparator(path.join(dataPath, 'supplier.tbl'));
    const coll = cluster.bucket(BUCKET).scope(SCOPE).collection('SupplierR');
    const docs = rows.map(([id, s_name, s_address, s_nationkey, s_phone, s_acctbal, s_comment]) => ({
        id, s_name, s_address, s_nationkey, s_phone,
        s_acctbal: Number(s_acctbal), s_comment
    }));
    await upsertAll(coll, docs, BATCH);
    console.log('SupplierR loaded');
}

async function loadParts(cluster, dataPath) {
    const rows = await readDataFromCustomSeparator(path.join(dataPath, 'part.tbl'));
    const coll = cluster.bucket(BUCKET).scope(SCOPE).collection('PartR');
    const docs = rows.map(([id, p_name, p_mfgr, p_brand, p_type, p_size, p_container, p_retailprice, p_commen]) => ({
        id, p_name, p_mfgr, p_brand, p_type,
        p_size:        Number(p_size),
        p_container,
        p_retailprice: Number(p_retailprice),
        p_commen
    }));
    await upsertAll(coll, docs, BATCH);
    console.log('PartR loaded');
}

async function loadAll(cluster, dataPath) {
    await loadRegions(cluster, dataPath);
    await loadNations(cluster, dataPath);
    await loadCustomers(cluster, dataPath);
    await loadOrders(cluster, dataPath);
    await loadLineitems(cluster, dataPath);
    await loadPartsupps(cluster, dataPath);
    await loadSuppliers(cluster, dataPath);
    await loadParts(cluster, dataPath);
    await createSecondaryIndexes(cluster);
}

// ── public API ────────────────────────────────────────────────────────────────

export async function runRelationalLoader() {
    const cluster = await getCluster();
    await ensureCouchbaseScopeAndCollections(cluster, BUCKET, SCOPE, COLLECTIONS_R);

    const result = await runLoader(
        SENTINEL_KEY,
        COLLECTIONS_R,
        (dataPath) => loadAll(cluster, dataPath),
        'Relational data (Ottoman.js)',
        createCouchbaseOps(cluster, BUCKET, SCOPE)
    );

    await cluster.close();
    return result;
}
