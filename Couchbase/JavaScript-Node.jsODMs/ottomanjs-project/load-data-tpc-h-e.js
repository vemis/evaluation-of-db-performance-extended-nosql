import {insertAll, partition, readDataFromCustomSeparator, getShuffledLineitemsTagsFromRow} from "./load-data-tpc-h.js";
import {CustomerEWithOrders} from "./models/tpc_h_e/customer-e-with-orders.js";
import {OrdersEWithLineitems} from "./models/tpc_h_e/orders-e-with-lineitems.js";
import {OrdersEWithLineitemsArrayAsTags} from "./models/tpc_h_e/orders-e-with-lineitems-array-as-tags.js";


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
                o_custkey,
                o_orderstatus,
                o_totalprice,
                o_orderdate: new Date(o_orderdate),
                o_orderpriority,
                o_clerk,
                o_shippriority,
                o_comment
            })
        );

        return rowsOfSchemas

    } catch (err) {
        console.error(err);
    }
}

async function loadCustomersEWithOrders(filePath, orders){
    try {
        var rowsOfData = await readDataFromCustomSeparator(filePath)

        const ordersByCustomers = mapOrdersByCustomer(orders)

        console.log("Mapping rowsOfData to rowsOfSchemas")
        const rowsOfSchemas = rowsOfData.map(([
                                                  id,
                                                  c_name,
                                                  c_address,
                                                  c_nationkey,
                                                  c_phone,
                                                  c_acctbal,
                                                  c_mktsegment,
                                                  c_commen
                                              ]) => ({
                id,
                c_name,
                c_address,
                c_nationkey,
                c_phone,
                c_acctbal,
                c_mktsegment,
                c_commen,
                c_orders: ordersByCustomers.get( id )
            })
        );

        console.log("Inserting rowsOfSchemas")

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 5_000);

        console.log("Batches created")

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await insertAll(rowsOfSchemasBatches[i], CustomerEWithOrders);
            console.log(`Batch inserted! ${i + 1}/${rowsOfSchemasBatches.length}`)
        }

    } catch (err) {
        console.error(err);
    }
}

function mapLineitemsByOrder(lineitems) {
    const lineitemsByOrder = new Map();

    for (const lineitem of lineitems) {
        const key = lineitem.l_orderkey;

        if (!lineitemsByOrder.has(key)) {
            lineitemsByOrder.set(key, []);
        }

        lineitemsByOrder.get(key).push(lineitem);
    }
    return lineitemsByOrder;
}

async function createLineitemsE(filePath) {
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
            l_orderkey: parseInt(l_orderkey),
            l_partkey: parseInt(l_partkey),
            l_suppkey: parseInt(l_suppkey),
            l_ps_id: l_partkey + "|" + l_suppkey,
            l_linenumber: parseInt(l_linenumber),
            l_quantity: parseInt(l_quantity),
            l_extendedprice: parseFloat(l_extendedprice),
            l_discount: parseFloat(l_discount),
            l_tax: parseFloat(l_tax),
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

async function loadOrdersEWithLineitems(ordersFilePath, lineitems) {
    try {
        const rowsOfData = await readDataFromCustomSeparator(ordersFilePath);

        const lineitemsByOrder = mapLineitemsByOrder(lineitems);

        console.log("Mapping rowsOfData to rowsOfSchemas")
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
            o_custkey,
            o_orderstatus,
            o_totalprice,
            o_orderdate: new Date(o_orderdate),
            o_orderpriority,
            o_clerk,
            o_shippriority,
            o_comment,
            o_lineitems: lineitemsByOrder.get(parseInt(id)) || []
        }));

        console.log("Inserting rowsOfSchemas")

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 5_000);

        console.log("Batches created")

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await insertAll(rowsOfSchemasBatches[i], OrdersEWithLineitems);
            console.log(`Batch inserted! ${i + 1}/${rowsOfSchemasBatches.length}`)
        }

    } catch (err) {
        console.error(err);
    }
}

/**
 * Converts one row of lineitem.tbl into a mixed-type tags array, mirroring
 * TPCHDatasetLoader.createLineitemsTags() in the Java reference implementation.
 */
function createLineitemsTags(lineitemsRow) {
    return [
        Number(lineitemsRow[0]),    // l_orderkey
        Number(lineitemsRow[1]),    // l_partkey
        Number(lineitemsRow[2]),    // l_suppkey
        Number(lineitemsRow[3]),    // l_linenumber
        Number(lineitemsRow[4]),    // l_quantity
        Number(lineitemsRow[5]),    // l_extendedprice
        Number(lineitemsRow[6]),    // l_discount
        Number(lineitemsRow[7]),    // l_tax
        lineitemsRow[8],            // l_returnflag
        lineitemsRow[9],            // l_linestatus
        new Date(lineitemsRow[10]), // l_shipdate
        new Date(lineitemsRow[11]), // l_commitdate
        new Date(lineitemsRow[12]), // l_receiptdate
        lineitemsRow[13],           // l_shipinstruct
        lineitemsRow[14],           // l_shipmode
        lineitemsRow[15]            // l_comment
    ];
}

async function loadOrdersEWithLineitemsArrayAsTags(ordersFilePath, lineitemsFilePath) {
    try {
        const ordersData = await readDataFromCustomSeparator(ordersFilePath);
        const lineitemsData = await readDataFromCustomSeparator(lineitemsFilePath);

        // 2nd row (index 1) is used as the source of unique tag values,
        // matching the Java reference implementation
        const lineitemsRow2 = lineitemsData[1];

        console.log("Mapping rowsOfData to rowsOfSchemas")
        const rowsOfSchemas = ordersData.map(([
                                                  id,
                                                  _o_custkey,
                                                  _o_orderstatus,
                                                  _o_totalprice,
                                                  o_orderdate,
                                              ]) => ({
            id,
            o_orderdate: new Date(o_orderdate),
            o_lineitems_tags: getShuffledLineitemsTagsFromRow(createLineitemsTags(lineitemsRow2), parseInt(id))
        }));

        console.log("Inserting rowsOfSchemas")

        const rowsOfSchemasBatches = partition(rowsOfSchemas, 5_000);

        console.log("Batches created")

        for (let i = 0; i < rowsOfSchemasBatches.length; i++) {
            await insertAll(rowsOfSchemasBatches[i], OrdersEWithLineitemsArrayAsTags);
            console.log(`Batch inserted! ${i + 1}/${rowsOfSchemasBatches.length}`)
        }

    } catch (err) {
        console.error(err);
    }
}

export {
    loadOrders,
    loadCustomersEWithOrders,
    createLineitemsE,
    loadOrdersEWithLineitems,
    loadOrdersEWithLineitemsArrayAsTags
}