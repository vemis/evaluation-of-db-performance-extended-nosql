import mongoose, { mongo } from "mongoose";

import fs from 'fs';
import seedrandom from 'seedrandom';

import OrdersESchema from "./models/tpc_h_e/orders-e.js";
import CustomerEWithOrders from "./models/tpc_h_e/customer-e-with-orders.js";
import OrdersEWithLineitems from "./models/tpc_h_e/orders-e-with-lineitems.js";
import OrdersEWithLineitemsArrayAsTags from "./models/tpc_h_e/orders-e-with-lineitems-array-as-tags.js";
import OrdersEWithLineitemsArrayAsTagsIndexed from "./models/tpc_h_e/orders-e-with-lineitems-array-as-tags-indexed.js";
import OrdersEWithCustomerWithNationWithRegion from "./models/tpc_h_e/orders-e-with-customer-with-nation-with-region.js";
import OrdersEOnlyOComment from "./models/tpc_h_e/orders-e-only-o-comment.js";
import OrdersEOnlyOCommentIndexed from "./models/tpc_h_e/orders-e-only-o-comment-indexed.js";


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

/**
 * Converts a pipe-split lineitem row into a mixed-type array of 16 values.
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

/**
 * Seeded Fisher-Yates shuffle that also picks a random prefix length (1–16).
 * Using seedrandom so the same o_orderkey always produces the same tags array.
 */
function shuffleArrayItemsAndLength(tags, shuffleSeed) {
    const rng = seedrandom(shuffleSeed);
    const list = [...tags];

    for (let i = list.length - 1; i > 0; i--) {
        const j = Math.floor(rng() * (i + 1));
        [list[i], list[j]] = [list[j], list[i]];
    }

    const size = 1 + Math.floor(rng() * list.length);
    return list.slice(0, size);
}

function getShuffledLineitemsTagsFromRow(lineitemsRow, shuffleSeed) {
    return shuffleArrayItemsAndLength(createLineitemsTags(lineitemsRow), shuffleSeed);
}

/**
 * Mirrors TPCHDatasetLoaderMorphiaE.loadOrdersEWithLineitemsArrayAsTags / SpringDataE equivalent.
 *
 * Populates the ordersEWithLineitemsArrayAsTags collection.
 * Each document stores a randomly shuffled and truncated subset of the fields from
 * the 2nd row of lineitems.tbl as a mixed-type array (o_lineitems_tags).
 * The shuffle seed is the o_orderkey value, matching the Java implementation exactly.
 */
async function loadOrdersEWithLineitemsArrayAsTags(filePathOrders, filePathLineitems) {
    try {
        const ordersData = await readDataFromCustomSeparator(filePathOrders);
        const lineitemsData = await readDataFromCustomSeparator(filePathLineitems);

        const linetemsRow2 = lineitemsData[1]; // 2nd row - used as source of unique tag elements

        const rowsOfSchemas = ordersData.map(row => ({
            _id: Number(row[0]),
            o_orderdate: new Date(row[4]),
            o_lineitems_tags: getShuffledLineitemsTagsFromRow(linetemsRow2, Number(row[0]))
        }));

        const batches = partition(rowsOfSchemas, 200);

        console.log("Inserting ordersEWithLineitemsArrayAsTags batches");

        for (let i = 0; i < batches.length; i++) {
            await OrdersEWithLineitemsArrayAsTags.insertMany(batches[i]);
            console.log(`Batch ${i}/${batches.length} inserted!`);
        }

        console.log("ordersEWithLineitemsArrayAsTags inserted!");
    } catch (err) {
        console.error(err);
    }
}

async function loadOrdersEWithLineitemsArrayAsTagsIndexed(filePathOrders, filePathLineitems) {
    try {
        const ordersData = await readDataFromCustomSeparator(filePathOrders);
        const lineitemsData = await readDataFromCustomSeparator(filePathLineitems);

        const linetemsRow2 = lineitemsData[1]; // 2nd row - used as source of unique tag elements

        const rowsOfSchemas = ordersData.map(row => ({
            _id: Number(row[0]),
            o_orderdate: new Date(row[4]),
            o_lineitems_tags_indexed: getShuffledLineitemsTagsFromRow(linetemsRow2, Number(row[0]))
        }));

        const batches = partition(rowsOfSchemas, 200);

        console.log("Inserting ordersEWithLineitemsArrayAsTagsIndexed batches");

        for (let i = 0; i < batches.length; i++) {
            await OrdersEWithLineitemsArrayAsTagsIndexed.insertMany(batches[i]);
            console.log(`Batch ${i}/${batches.length} inserted!`);
        }

        console.log("ordersEWithLineitemsArrayAsTagsIndexed inserted!");
    } catch (err) {
        console.error(err);
    }
}

async function loadOrdersEWithCustomerWithNationWithRegion(filePathOrders, filePathCustomers, filePathNations, filePathRegions) {
    try {
        const ordersData   = await readDataFromCustomSeparator(filePathOrders);
        const regionRows   = await readDataFromCustomSeparator(filePathRegions);
        const nationRows   = await readDataFromCustomSeparator(filePathNations);
        const customerRows = await readDataFromCustomSeparator(filePathCustomers);

        // Build region map keyed by r_regionkey
        // region.tbl: r_regionkey|r_name|r_comment
        const regionMap = new Map();
        for (const row of regionRows) {
            regionMap.set(Number(row[0]), {
                r_regionkey: Number(row[0]),
                r_name: row[1]
            });
        }

        // Build nation map keyed by n_nationkey (with embedded region)
        // nation.tbl: n_nationkey|n_name|n_regionkey|n_comment
        const nationMap = new Map();
        for (const row of nationRows) {
            const n_nationkey = Number(row[0]);
            const n_regionkey = Number(row[2]);
            nationMap.set(n_nationkey, {
                n_nationkey,
                n_name: row[1],
                n_regionkey,
                n_region: regionMap.get(n_regionkey)
            });
        }

        // Build customer map keyed by c_custkey (with embedded nation)
        // customer.tbl: c_custkey|c_name|c_address|c_nationkey|c_phone|c_acctbal|c_mktsegment|c_comment
        const customerMap = new Map();
        for (const row of customerRows) {
            const c_custkey  = Number(row[0]);
            const c_nationkey = Number(row[3]);
            customerMap.set(c_custkey, {
                c_custkey,
                c_name: row[1],
                c_nationkey,
                c_nation: nationMap.get(c_nationkey)
            });
        }

        // orders.tbl: o_orderkey|o_custkey|o_orderstatus|o_totalprice|o_orderdate|...
        const rowsOfSchemas = ordersData.map(row => ({
            _id: Number(row[0]),
            o_orderdate: new Date(row[4]),
            o_customer: customerMap.get(Number(row[1]))
        }));

        const batches = partition(rowsOfSchemas, 200);

        console.log("Inserting ordersEWithCustomerWithNationWithRegion batches");

        for (let i = 0; i < batches.length; i++) {
            await OrdersEWithCustomerWithNationWithRegion.insertMany(batches[i]);
            console.log(`Batch ${i}/${batches.length} inserted!`);
        }

        console.log("ordersEWithCustomerWithNationWithRegion inserted!");
    } catch (err) {
        console.error(err);
    }
}

async function loadOrdersEOnlyOComment(filePathOrders) {
    try {
        const ordersData = await readDataFromCustomSeparator(filePathOrders);

        // orders.tbl: o_orderkey|o_custkey|o_orderstatus|o_totalprice|o_orderdate|...|o_comment(8)
        const rowsOfSchemas = ordersData.map(row => ({
            _id: Number(row[0]),
            o_orderdate: new Date(row[4]),
            o_comment: row[8]
        }));

        const batches = partition(rowsOfSchemas, 200);

        console.log("Inserting ordersEOnlyOComment batches");

        for (let i = 0; i < batches.length; i++) {
            await OrdersEOnlyOComment.insertMany(batches[i]);
            console.log(`Batch ${i}/${batches.length} inserted!`);
        }

        console.log("ordersEOnlyOComment inserted!");
    } catch (err) {
        console.error(err);
    }
}

async function loadOrdersEOnlyOCommentIndexed(filePathOrders) {
    try {
        const ordersData = await readDataFromCustomSeparator(filePathOrders);

        // orders.tbl: o_orderkey|o_custkey|o_orderstatus|o_totalprice|o_orderdate|...|o_comment(8)
        const rowsOfSchemas = ordersData.map(row => ({
            _id: Number(row[0]),
            o_orderdate: new Date(row[4]),
            o_comment: row[8]
        }));

        const batches = partition(rowsOfSchemas, 200);

        console.log("Inserting ordersEOnlyOCommentIndexed batches");

        for (let i = 0; i < batches.length; i++) {
            await OrdersEOnlyOCommentIndexed.insertMany(batches[i]);
            console.log(`Batch ${i}/${batches.length} inserted!`);
        }

        console.log("ordersEOnlyOCommentIndexed inserted!");
    } catch (err) {
        console.error(err);
    }
}

// exported API
export {
    loadOrdersE,
    loadCustomersEWithOrders,
    loadLineitemsE,
    loadOrdersEWithLineitems,
    loadOrdersEWithLineitemsArrayAsTags,
    loadOrdersEWithLineitemsArrayAsTagsIndexed,
    loadOrdersEWithCustomerWithNationWithRegion,
    loadOrdersEOnlyOComment,
    loadOrdersEOnlyOCommentIndexed
}