import fs from 'fs';
import path from 'path';
import mongoose from 'mongoose';
import RegionR   from '../models/tpc_h_r/region-r.js';
import NationR   from '../models/tpc_h_r/nation-r.js';
import CustomerR from '../models/tpc_h_r/customer-r.js';
import OrdersR   from '../models/tpc_h_r/orders-r.js';
import PartR     from '../models/tpc_h_r/part-r.js';
import PartsuppR from '../models/tpc_h_r/partsupp-r.js';
import SupplierR from '../models/tpc_h_r/supplier-r.js';
import LineitemR from '../models/tpc_h_r/lineitem-r.js';

const RELATIONAL_COLLECTIONS = ['regionR', 'nationR', 'customerR', 'ordersR', 'lineitemR', 'partsuppR', 'partR', 'supplierR'];
const SENTINEL_ID = 'load_r_complete';
const BATCH = 100_000;

async function readTbl(filePath) {
    const data = await fs.promises.readFile(filePath, 'utf8');
    const rows = data.split('\n');
    if (rows.length > 1 && rows[rows.length - 1] === '') rows.pop();
    return rows.map(r => r.split('|'));
}

async function insertBatched(Model, docs, batchSize = BATCH) {
    for (let i = 0; i < docs.length; i += batchSize) {
        await Model.insertMany(docs.slice(i, i + batchSize));
    }
}

async function isAlreadyLoaded() {
    return (await mongoose.connection.db.collection('_metadata').findOne({ _id: SENTINEL_ID })) !== null;
}

async function dropCollections() {
    for (const col of RELATIONAL_COLLECTIONS) {
        try { await mongoose.connection.db.collection(col).drop(); } catch (_) {}
    }
}

async function loadAll(dataPath) {
    const p = f => path.join(dataPath, f);

    const regions = (await readTbl(p('region.tbl'))).map(([_id, r_name, r_comment]) => ({
        _id: Number(_id), r_name, r_comment
    }));
    await RegionR.insertMany(regions);
    console.log('regionR loaded');

    const nations = (await readTbl(p('nation.tbl'))).map(([_id, n_name, n_regionkey, n_comment]) => ({
        _id: Number(_id), n_name, n_regionkey: Number(n_regionkey), n_comment
    }));
    await NationR.insertMany(nations);
    console.log('nationR loaded');

    const customers = (await readTbl(p('customer.tbl'))).map(([_id, c_name, c_address, c_nationkey, c_phone, c_acctbal, c_mktsegment, c_commen]) => ({
        _id: Number(_id), c_name, c_address, c_nationkey: Number(c_nationkey), c_phone, c_acctbal: Number(c_acctbal), c_mktsegment, c_commen
    }));
    await insertBatched(CustomerR, customers);
    console.log('customerR loaded');

    const orders = (await readTbl(p('orders.tbl'))).map(([_id, o_custkey, o_orderstatus, o_totalprice, o_orderdate, o_orderpriority, o_clerk, o_shippriority, o_comment]) => ({
        _id: Number(_id), o_custkey: Number(o_custkey), o_orderstatus, o_totalprice, o_orderdate: new Date(o_orderdate), o_orderpriority, o_clerk, o_shippriority, o_comment
    }));
    await insertBatched(OrdersR, orders);
    console.log('ordersR loaded');

    const lineitems = (await readTbl(p('lineitem.tbl'))).map(([l_orderkey, l_partkey, l_suppkey, l_linenumber, l_quantity, l_extendedprice, l_discount, l_tax, l_returnflag, l_linestatus, l_shipdate, l_commitdate, l_receiptdate, l_shipinstruct, l_shipmode, l_comment]) => ({
        _id: l_orderkey + '|' + l_linenumber,
        l_orderkey: Number(l_orderkey), l_partkey: Number(l_partkey), l_suppkey: Number(l_suppkey),
        l_ps_id: l_partkey + '|' + l_suppkey,
        l_linenumber: Number(l_linenumber), l_quantity: Number(l_quantity),
        l_extendedprice: Number(l_extendedprice), l_discount: Number(l_discount), l_tax: Number(l_tax),
        l_returnflag, l_linestatus,
        l_shipdate: new Date(l_shipdate), l_commitdate: new Date(l_commitdate), l_receiptdate: new Date(l_receiptdate),
        l_shipinstruct, l_shipmode, l_comment
    }));
    await insertBatched(LineitemR, lineitems);
    console.log('lineitemR loaded');

    const partsupps = (await readTbl(p('partsupp.tbl'))).map(([ps_partkey, ps_suppkey, ps_availqty, ps_supplycost, ps_comment]) => ({
        _id: ps_partkey + '|' + ps_suppkey,
        ps_partkey: Number(ps_partkey), ps_suppkey: Number(ps_suppkey),
        ps_availqty: Number(ps_availqty), ps_supplycost: Number(ps_supplycost), ps_comment
    }));
    await insertBatched(PartsuppR, partsupps);
    console.log('partsuppR loaded');

    const suppliers = (await readTbl(p('supplier.tbl'))).map(([_id, s_name, s_address, s_nationkey, s_phone, s_acctbal, s_comment]) => ({
        _id: Number(_id), s_name, s_address, s_nationkey: Number(s_nationkey), s_phone, s_acctbal: Number(s_acctbal), s_comment
    }));
    await insertBatched(SupplierR, suppliers);
    console.log('supplierR loaded');

    const parts = (await readTbl(p('part.tbl'))).map(([_id, p_name, p_mfgr, p_brand, p_type, p_size, p_container, p_retailprice, p_commen]) => ({
        _id: Number(_id), p_name, p_mfgr, p_brand, p_type, p_size: Number(p_size), p_container, p_retailprice: Number(p_retailprice), p_commen
    }));
    await insertBatched(PartR, parts);
    console.log('partR loaded');
}

async function insertSentinel() {
    await mongoose.connection.db.collection('_metadata').insertOne({ _id: SENTINEL_ID });
}

export async function runRelationalLoader() {
    const dataPath = process.env.TPCH_DATA_PATH || '/data/tpch-data-small';
    if (await isAlreadyLoaded()) {
        console.log('Relational data already loaded, skipping.');
        return 'Relational data already loaded, skipping.';
    }
    await dropCollections();
    await loadAll(dataPath);
    await insertSentinel();
    const msg = `Relational data loaded from: ${dataPath}`;
    console.log(msg);
    return msg;
}
