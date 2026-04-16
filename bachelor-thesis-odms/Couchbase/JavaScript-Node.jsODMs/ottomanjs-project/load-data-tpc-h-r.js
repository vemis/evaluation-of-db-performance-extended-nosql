import fs from 'fs';
import {RegionR} from "./models/tpc_h_r/region-r.js";
import {NationR} from "./models/tpc_h_r/nation-r.js";
import {CustomerR} from "./models/tpc_h_r/customer-r.js";
import {OrdersR} from "./models/tpc_h_r/orders-r.js";
import {LineitemR} from "./models/tpc_h_r/lineitem-r.js";
import {PartsuppR} from "./models/tpc_h_r/partsupp-r.js";
import {SupplierR} from "./models/tpc_h_r/supplier-r.js";
import {PartR} from "./models/tpc_h_r/part-r.js";
import {insertAll, partition, readDataFromCustomSeparator} from "./load-data-tpc-h.js";




async function loadRegions(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)
        
        const rowsOfSchemas = rowsOfData.map(([id, r_name, r_comment]) => ({
            id,
            r_name,
            r_comment
        }));

        //console.log(rowsOfSchemas)

        await insertAll(rowsOfSchemas, RegionR);

    } catch (err) {
        console.error(err);
    }

}

async function loadNations(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)
        
        const rowsOfSchemas = rowsOfData.map(([id, n_name, n_regionkey, r_comment]) => ({
            id,
            n_name,
            n_regionkey,
            r_comment
        }));

        //console.log(rowsOfSchemas)

        await insertAll(rowsOfSchemas, NationR);

    } catch (err) {
        console.error(err);
    }
}

async function loadCustomers(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)
        
        const rowsOfSchemas = rowsOfData.map(([
            id,
            c_name,
            c_address,
            c_nationkey,
            c_phone,
            c_acctbal,
            c_mktsegment,
            c_commen ]) => ({
                id,
                c_name,
                c_address,
                c_nationkey,
                c_phone,
                c_acctbal: Number(c_acctbal),
                c_mktsegment,
                c_commen
            })
        );

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 2_500);

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await insertAll(rowsOfSchemasBatches[i], CustomerR);
            console.log(`Batch inserted! ${i + 1}/${rowsOfSchemasBatches.length}`)
        }

    } catch (err) {
        console.error(err);
    }
}

async function loadOrders(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)

        const rowsOfSchemas = rowsOfData.map(([
                 id,
                 o_custkey,
                 o_orderstatus,
                 o_totalprice,
                 o_orderdate,
                 o_orderpriority,
                 o_clerk,
                 o_shippriority,
                 o_comment
                 ]) => ({
                    id,
                    o_orderkey_field: Number(id),
                    o_custkey,
                    o_orderstatus,
                    o_totalprice: Number(o_totalprice),
                    o_orderdate: new Date(o_orderdate),
                    o_orderpriority,
                    o_clerk,
                    o_shippriority,
                    o_comment
                })
        );

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 2_500);

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await insertAll(rowsOfSchemasBatches[i], OrdersR);
            console.log(`Batch inserted! ${i + 1}/${rowsOfSchemasBatches.length}`)
        }

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
            id: l_orderkey + "|" + l_linenumber,
            l_orderkey,
            l_partkey,
            l_suppkey,
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

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 2_500);

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await insertAll(rowsOfSchemasBatches[i], LineitemR);
            console.log(`Batch inserted! ${i + 1}/${rowsOfSchemasBatches.length}`)
        }


    } catch (err) {
        console.error(err);
    }
}

async function loadPartsupps(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)

        const rowsOfSchemas = rowsOfData.map(([
                                                  ps_partkey,
                                                  ps_suppkey,
                                                  ps_availqty,
                                                  ps_supplycost,
                                                  ps_comment
                                              ]) => ({
                id: ps_partkey + "|" + ps_suppkey,
                ps_partkey: ps_partkey,
                ps_suppkey: ps_suppkey,
                ps_availqty: Number(ps_availqty),
                ps_supplycost: Number(ps_supplycost),
                ps_comment
            })
        );

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 2_500);

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await insertAll(rowsOfSchemasBatches[i], PartsuppR);
            console.log(`Batch inserted! ${i + 1}/${rowsOfSchemasBatches.length}`)
        }


    } catch (err) {
        console.error(err);
    }
}

async function loadSuppliers(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)

        const rowsOfSchemas = rowsOfData.map(([
                                                  id,
                                                  s_name,
                                                  s_address,

                                                  s_nationkey,
                                                  s_phone,
                                                  s_acctbal,

                                                  s_comment
                                              ]) => ({
                id,
                s_name,
                s_address,

                s_nationkey,
                s_phone,
                s_acctbal: Number(s_acctbal),

                s_comment
            })
        );

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 2_500);

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await insertAll(rowsOfSchemasBatches[i], SupplierR);
            console.log(`Batch inserted! ${i + 1}/${rowsOfSchemasBatches.length}`)
        }


    } catch (err) {
        console.error(err);
    }
}

async function loadParts(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)

        const rowsOfSchemas = rowsOfData.map(([
                                                  id,
                                                  p_name,
                                                  p_mfgr,
                                                  p_brand,
                                                  p_type,
                                                  p_size,
                                                  p_container,
                                                  p_retailprice,
                                                  p_commen
                                              ]) => ({
                id,
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

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 2_500);

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await insertAll(rowsOfSchemasBatches[i], PartR);
            console.log(`Batch inserted! ${i + 1}/${rowsOfSchemasBatches.length}`)
        }


    } catch (err) {
        console.error(err);
    }
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



