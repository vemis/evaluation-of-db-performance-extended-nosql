import path from 'path';
import { connect, getDefaultInstance } from 'ottoman';
import { readDataFromCustomSeparator, runLoader,
         ensureCouchbaseScopeAndCollections, createCouchbaseOps, insertBatched } from 'common-js';

const BUCKET        = 'bucket-main';
const SCOPE         = 'ottoman_scope_r';
const SENTINEL_KEY  = 'ottoman_load_r_complete';
const COLLECTIONS_R = ['RegionR', 'NationR', 'CustomerR', 'OrdersR', 'LineitemR', 'PartsuppR', 'SupplierR', 'PartR'];
const BATCH         = 2_500;

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

// ── public API ────────────────────────────────────────────────────────────────

export async function runRelationalLoader() {
    const cluster = await connectOttoman();

    // Dynamic imports ensure model() is called only after connect() registers the Ottoman instance
    const { RegionR }   = await import('../model/relational/region-r.js');
    const { NationR }   = await import('../model/relational/nation-r.js');
    const { CustomerR } = await import('../model/relational/customer-r.js');
    const { OrdersR }   = await import('../model/relational/orders-r.js');
    const { LineitemR } = await import('../model/relational/lineitem-r.js');
    const { PartsuppR } = await import('../model/relational/partsupp-r.js');
    const { SupplierR } = await import('../model/relational/supplier-r.js');
    const { PartR }     = await import('../model/relational/part-r.js');

    await ensureCouchbaseScopeAndCollections(cluster, BUCKET, SCOPE, COLLECTIONS_R);

    async function loadRegions(dataPath) {
        const rows = await readDataFromCustomSeparator(path.join(dataPath, 'region.tbl'));
        const docs = rows.map(([id, r_name, r_comment]) => ({ id, r_name, r_comment }));
        await insertBatched(RegionR, docs);
        console.log('RegionR loaded');
    }

    async function loadNations(dataPath) {
        const rows = await readDataFromCustomSeparator(path.join(dataPath, 'nation.tbl'));
        const docs = rows.map(([id, n_name, n_regionkey, n_comment]) => ({ id, n_name, n_regionkey, n_comment }));
        await insertBatched(NationR, docs);
        console.log('NationR loaded');
    }

    async function loadCustomers(dataPath) {
        const rows = await readDataFromCustomSeparator(path.join(dataPath, 'customer.tbl'));
        const docs = rows.map(([id, c_name, c_address, c_nationkey, c_phone, c_acctbal, c_mktsegment, c_commen]) => ({
            id, c_name, c_address, c_nationkey, c_phone,
            c_acctbal: Number(c_acctbal), c_mktsegment, c_commen
        }));
        await insertBatched(CustomerR, docs);
        console.log('CustomerR loaded');
    }

    async function loadOrders(dataPath) {
        const rows = await readDataFromCustomSeparator(path.join(dataPath, 'orders.tbl'));
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
        await insertBatched(OrdersR, docs);
        console.log('OrdersR loaded');
    }

    async function loadLineitems(dataPath) {
        const rows = await readDataFromCustomSeparator(path.join(dataPath, 'lineitem.tbl'));
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
        await insertBatched(LineitemR, docs);
        console.log('LineitemR loaded');
    }

    async function loadPartsupps(dataPath) {
        const rows = await readDataFromCustomSeparator(path.join(dataPath, 'partsupp.tbl'));
        const docs = rows.map(([ps_partkey, ps_suppkey, ps_availqty, ps_supplycost, ps_comment]) => ({
            id: ps_partkey + '|' + ps_suppkey,
            ps_partkey,
            ps_suppkey,
            ps_availqty:   Number(ps_availqty),
            ps_supplycost: Number(ps_supplycost),
            ps_comment
        }));
        await insertBatched(PartsuppR, docs);
        console.log('PartsuppR loaded');
    }

    async function loadSuppliers(dataPath) {
        const rows = await readDataFromCustomSeparator(path.join(dataPath, 'supplier.tbl'));
        const docs = rows.map(([id, s_name, s_address, s_nationkey, s_phone, s_acctbal, s_comment]) => ({
            id, s_name, s_address, s_nationkey, s_phone,
            s_acctbal: Number(s_acctbal), s_comment
        }));
        await insertBatched(SupplierR, docs);
        console.log('SupplierR loaded');
    }

    async function loadParts(dataPath) {
        const rows = await readDataFromCustomSeparator(path.join(dataPath, 'part.tbl'));
        const docs = rows.map(([id, p_name, p_mfgr, p_brand, p_type, p_size, p_container, p_retailprice, p_commen]) => ({
            id, p_name, p_mfgr, p_brand, p_type,
            p_size:        Number(p_size),
            p_container,
            p_retailprice: Number(p_retailprice),
            p_commen
        }));
        await insertBatched(PartR, docs);
        console.log('PartR loaded');
    }

    return runLoader(
        SENTINEL_KEY,
        COLLECTIONS_R,
        async (dataPath) => {
            await loadRegions(dataPath);
            await loadNations(dataPath);
            await loadCustomers(dataPath);
            await loadOrders(dataPath);
            await loadLineitems(dataPath);
            await loadPartsupps(dataPath);
            await loadSuppliers(dataPath);
            await loadParts(dataPath);
            await createSecondaryIndexes(cluster);
        },
        'Relational data (Ottoman.js)',
        createCouchbaseOps(cluster, BUCKET, SCOPE)
    );
}
