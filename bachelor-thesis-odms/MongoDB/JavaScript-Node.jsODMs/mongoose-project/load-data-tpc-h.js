import fs from 'fs';

import RegionR from "./models/tpc_h_r/region-r.js";
import NationR from "./models/tpc_h_r/nation-r.js";
import CustomerR from "./models/tpc_h_r/customer-r.js";
import OrdersR from "./models/tpc_h_r/orders-r.js";
import PartR from "./models/tpc_h_r/part-r.js";
import PartsuppR from "./models/tpc_h_r/partsupp-r.js";
import SupplierR from "./models/tpc_h_r/supplier-r.js";
import LineitemR from "./models/tpc_h_r/lineitem-r.js";

async function readDataFromCustomSeparator(filePath){
    const data = await fs.promises.readFile(filePath, 'utf8');
    var rows = data.split('\n');

    // the last one is just empty string
    if (rows.length > 1 && rows[rows.length - 1] == '') {
        rows.pop()
    }
    return rows.map(row => row.split('|'));
}



async function loadRegions(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)
        
        const rowsOfSchemas = rowsOfData.map(([_id, r_name, r_comment]) => ({
            _id: Number(_id),
            r_name,
            r_comment
        }));

        //console.log(rowsOfSchemas)

        await RegionR.insertMany( rowsOfSchemas );

    } catch (err) {
        console.error(err);
    }

}

async function loadNations(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)
        
        const rowsOfSchemas = rowsOfData.map(([_id, n_name, n_regionkey, r_comment]) => ({
            _id: Number(_id),
            n_name,
            n_regionkey,
            r_comment
        }));

        console.log(rowsOfSchemas)

        await NationR.insertMany( rowsOfSchemas );

    } catch (err) {
        console.error(err);
    }
}

async function loadCustomers(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)
        
        const rowsOfSchemas = rowsOfData.map(([
            _id,
            c_name,
            c_address,
            c_nationkey,
            c_phone,
            c_acctbal,
            c_mktsegment,
            c_commen ]) => ({
                _id: Number(_id),
                c_name,
                c_address,
                c_nationkey: Number(c_nationkey),
                c_phone,
                c_acctbal: Number(c_acctbal),
                c_mktsegment,
                c_commen
            })
        );

        await CustomerR.insertMany( rowsOfSchemas );

    } catch (err) {
        console.error(err);
    }
}

async function loadOrders(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)

        const rowsOfSchemas = rowsOfData.map(([
                 _id,
                 o_custkey,
                 o_orderstatus,
                 o_totalprice,
                 o_orderdate,
                 o_orderpriority,
                 o_clerk,
                 o_shippriority,
                 o_comment
                 ]) => ({
                    _id: Number(_id),
                    o_custkey: Number(o_custkey),
                    o_orderstatus,
                    o_totalprice,
                    o_orderdate: new Date(o_orderdate),
                    o_orderpriority,
                    o_clerk,
                    o_shippriority,
                    o_comment
                })
        );

        await OrdersR.insertMany( rowsOfSchemas );

    } catch (err) {
        console.error(err);
    }
}

async function loadLineitems(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)

        const rowsOfSchemas = rowsOfData.map(([
                                                  l_orderkey,
                                                  l_partkey,
                                                  l_suppkey,
                                                  l_linenumber,
                                                  l_quantity,
                                                  l_extendedprice,
                                                  l_discount,
                                                  l_tax,
                                                  l_returnflag,
                                                  l_linestatus,
                                                  l_shipdate,
                                                  l_commitdate,
                                                  l_receiptdate,
                                                  l_shipinstruct,
                                                  l_shipmode,
                                                  l_comment
                                              ]) => ({
            _id: l_orderkey + "|" + l_linenumber,
            l_orderkey: Number(l_orderkey),
            l_partkey: Number(l_partkey),
            l_suppkey: Number(l_suppkey),
            l_ps_id: l_partkey + "|" + l_suppkey,
            l_linenumber: Number(l_linenumber),
            l_quantity: Number(l_quantity),
            l_extendedprice: Number(l_extendedprice),
            l_discount: Number(l_discount),
            l_tax: Number(l_tax),
            l_returnflag,
            l_linestatus,
            l_shipdate: new Date(l_shipdate),
            l_commitdate: new Date(l_commitdate),
            l_receiptdate: new Date(l_receiptdate),
            l_shipinstruct,
            l_shipmode,
            l_comment
            })
        );

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 100_000);

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await LineitemR.insertMany( rowsOfSchemasBatches[i] );
            console.log("Batch inserted!")
        }


    } catch (err) {
        console.error(err);
    }
}

async function loadPartsupps(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)

        const rowsOfSchemas = rowsOfData.map(([
                                                  ps_partKey,
                                                  ps_suppKey,
                                                  ps_availqty,
                                                  ps_supplycost,
                                                  ps_comment
                                              ]) => ({
                _id: ps_partKey + "|" + ps_suppKey,
                ps_partkey: Number(ps_partKey),
                ps_suppkey: Number(ps_suppKey),
                ps_availqty: Number(ps_availqty),
                ps_supplycost: Number(ps_supplycost),
                ps_comment
            })
        );

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 100_000);

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await PartsuppR.insertMany( rowsOfSchemasBatches[i] );
            console.log("Batch inserted!")
        }


    } catch (err) {
        console.error(err);
    }
}

async function loadSuppliers(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)

        const rowsOfSchemas = rowsOfData.map(([
                                                  _id,
                                                  s_name,
                                                  s_address,

                                                  s_nationkey,
                                                  s_phone,
                                                  s_acctbal,

                                                  s_comment
                                              ]) => ({
                _id: Number(_id),
                s_name,
                s_address,

                s_nationkey: Number(s_nationkey),
                s_phone,
                s_acctbal: Number(s_acctbal),

                s_comment
            })
        );

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 100_000);

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await SupplierR.insertMany( rowsOfSchemasBatches[i] );
            console.log("Batch inserted!")
        }


    } catch (err) {
        console.error(err);
    }
}

async function loadParts(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)

        const rowsOfSchemas = rowsOfData.map(([
                                                  _id,
                                                  p_name,
                                                  p_mfgr,
                                                  p_brand,
                                                  p_type,
                                                  p_size,
                                                  p_container,
                                                  p_retailprice,
                                                  p_commen
                                              ]) => ({
                _id: Number(_id),
                p_name,
                p_mfgr,
                p_brand,
                p_type,
                p_size: Number(p_size),
                p_container,
                p_retailprice: Number(p_retailprice),
                p_commen
            })
        );

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 100_000);

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await PartR.insertMany( rowsOfSchemasBatches[i] );
            console.log("Batch inserted!")
        }


    } catch (err) {
        console.error(err);
    }
}

function partition(array, batchSize) {
    const result = [];

    for (let i = 0; i < array.length; i += batchSize) {
        result.push(array.slice(i, i + batchSize));
    }

    return result;
}

// exported API
export {
    loadRegions,
    loadNations,
    loadCustomers,
    loadOrders,
    loadLineitems,
    loadPartsupps,
    loadSuppliers,
    loadParts
}



