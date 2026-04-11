import mongoose, { mongo } from "mongoose";

import fs from 'fs';

import OrdersESchema from "./models/tpc_h_e/orders-e.js";
import CustomerEWithOrders from "./models/tpc_h_e/customer-e-with-orders.js";
import OrdersEWithLineitems from "./models/tpc_h_e/orders-e-with-lineitems.js";


async function readDataFromCustomSeparator(filePath){
    const data = await fs.promises.readFile(filePath, 'utf8');
    var rows = data.split('\n');

    // the last one is just empty string
    if (rows.length > 1 && rows[rows.length - 1] == '') {
        rows.pop()
    }
    return rows.map(row => row.split('|'));
}

function partition(array, batchSize) {
    const result = [];

    for (let i = 0; i < array.length; i += batchSize) {
        result.push(array.slice(i, i + batchSize));
    }

    return result;
}

async function loadOrdersE(filePath){
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath)

        const rowsOfSchemas = rowsOfData.map(([
                                                  o_orderkey,
                                                  o_custkey,
                                                  o_orderstatus,
                                                  o_totalprice,
                                                  o_orderdate,
                                                  o_orderpriority,
                                                  o_clerk,
                                                  o_shippriority,
                                                  o_comment
                                              ]) =>  new mongoose.Types.Subdocument({
                o_orderkey: Number(o_orderkey),
                o_custkey: Number(o_custkey),
                o_orderstatus,
                o_totalprice,
                o_orderdate: new Date(o_orderdate),
                o_orderpriority,
                o_clerk,
                o_shippriority,
                o_comment
            }, OrdersESchema)

        );

        return rowsOfSchemas;

    } catch (err) {
        console.error(err);
    }
}

function mapOrdersByCustomer(ordersE){
    const ordersByCustomer = new Map();

    for (const order of ordersE) {
        const key = order.o_custkey;

        if (!ordersByCustomer.has(key)) {
            ordersByCustomer.set(key, []);
        }

        ordersByCustomer.get(key).push(order);
    }
    return ordersByCustomer;
}

async function loadCustomersEWithOrders(filePath, orders){
    try {
        var rowsOfData = await readDataFromCustomSeparator(filePath)

        const ordersByCustomers = mapOrdersByCustomer(orders)

        console.log("Mapping rowsOfData to rowsOfSchemas")
        const rowsOfSchemas = rowsOfData.map(([
                                                  _id,
                                                  c_name,
                                                  c_address,
                                                  c_nationkey,
                                                  c_phone,
                                                  c_acctbal,
                                                  c_mktsegment,
                                                  c_commen
                                              ]) => ({
                _id: Number(_id),
                c_name,
                c_address,
                c_nationkey: Number(c_nationkey),
                c_phone,
                c_acctbal: Number(c_acctbal),
                c_mktsegment,
                c_commen,
                c_orders: ordersByCustomers.get( Number(_id) )
            })
        );

        console.log("Inserting rowsOfSchemas")

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 100);

        console.log("Batches created")

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await CustomerEWithOrders.insertMany( rowsOfSchemasBatches[i] );
            console.log("Batch " + i + "/"  + rowsOfSchemasBatches.length + " inserted!")
        }
        //await CustomerEWithOrders.insertMany( rowsOfSchemas );

    } catch (err) {
        console.error(err);
    }
}

function mapLineitemsByOrder(lineitems) {
    const byOrder = new Map();
    for (const item of lineitems) {
        const key = item.l_orderkey;
        if (!byOrder.has(key)) {
            byOrder.set(key, []);
        }
        byOrder.get(key).push(item);
    }
    return byOrder;
}

async function loadLineitemsE(filePath) {
    try {
        const rowsOfData = await readDataFromCustomSeparator(filePath);

        return rowsOfData.map(([
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
            l_id: l_orderkey + l_linenumber,
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
        }));
    } catch (err) {
        console.error(err);
    }
}

async function loadOrdersEWithLineitems(filePathOrders, filePathLineitems) {
    try {
        const lineitems = await loadLineitemsE(filePathLineitems);
        console.log("Lineitems loaded");

        const lineitemsByOrder = mapLineitemsByOrder(lineitems);

        const rowsOfData = await readDataFromCustomSeparator(filePathOrders);

        const rowsOfSchemas = rowsOfData.map(([
            o_orderkey,
            o_custkey,
            o_orderstatus,
            o_totalprice,
            o_orderdate,
            o_orderpriority,
            o_clerk,
            o_shippriority,
            o_comment
        ]) => ({
            _id: Number(o_orderkey),
            o_custkey: Number(o_custkey),
            o_orderstatus,
            o_totalprice,
            o_orderdate: new Date(o_orderdate),
            o_orderpriority,
            o_clerk,
            o_shippriority,
            o_comment,
            o_lineitems: lineitemsByOrder.get(Number(o_orderkey)) ?? []
        }));

        const batches = partition(rowsOfSchemas, 200);

        console.log("Inserting ordersEWithLineitems batches");

        for (let i = 0; i < batches.length; i++) {
            await OrdersEWithLineitems.insertMany(batches[i]);
            console.log("Batch " + i + "/" + batches.length + " inserted!");
        }

        console.log("ordersEWithLineitems inserted!");
    } catch (err) {
        console.error(err);
    }
}

// exported API
export {
    loadOrdersE,
    loadCustomersEWithOrders,
    loadLineitemsE,
    loadOrdersEWithLineitems
}