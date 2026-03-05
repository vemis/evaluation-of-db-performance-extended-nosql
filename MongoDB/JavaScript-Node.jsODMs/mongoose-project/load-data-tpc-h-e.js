import mongoose, { mongo } from "mongoose";

import fs from 'fs';

import OrdersESchema from "./models/tpc_h_e/orders-e.js";
import CustomerEWithOrders from "./models/tpc_h_e/customer-e-with-orders.js";


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

// exported API
export {
    loadOrdersE,
    loadCustomersEWithOrders
}