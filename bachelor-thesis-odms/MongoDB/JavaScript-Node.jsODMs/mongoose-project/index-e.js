import mongoose, { mongo } from "mongoose";

// setting off the letter s on the end of a collection
// probably have no meaning -> needs to be called before the initialization
// that already happened in the tpc_h_r folder
mongoose.pluralize(null)

import OrdersESchema from "./models/tpc_h_e/orders-e.js";
import CustomerEWithOrders from "./models/tpc_h_e/customer-e-with-orders.js";

import * as loadDataTPCHE from "./load-data-tpc-h-e.js";
import ordersE from "./models/tpc_h_e/orders-e.js";
import * as queriesE from "./benchmarks/queries-e.js";
import * as benchmarksR from "./benchmarks/benchmarks-r.js";

async function run() {
    // Connect to MongoDB
    await mongoose.connect("mongodb://localhost:27017/mongoose_database_e");

    console.log("Connected to MongoDB.");

    /*benchmarksR.benchmarkQuery(
        queriesE.C2
    )*/


    const res = await queriesE.R9()
    console.log(res[0]);
    console.log(res.length)



    /*
    await loadDataTPCHE.loadOrdersEOnlyOCommentIndexed(
        "..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl"
    )

    await loadDataTPCHE.loadOrdersEOnlyOComment(
        "..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl"
    )

    await loadDataTPCHE.loadOrdersEWithCustomerWithNationWithRegion(
        "..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl",
        "..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl",
        "..\\..\\..\\dataset\\TPC-H\\tpch-data\\nation.tbl",
        "..\\..\\..\\dataset\\TPC-H\\tpch-data\\region.tbl",
    )

    await loadDataTPCHE.loadOrdersEWithLineitemsArrayAsTagsIndexed(
        "..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl",
        "..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl"
    )

    await loadDataTPCHE.loadOrdersEWithLineitemsArrayAsTags(
        "..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl",
        "..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl"
    )

    await loadDataTPCHE.loadOrdersEWithLineitems(
        "..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl",
        "..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl"
    )

    const ordersE = await loadDataTPCHE.loadOrdersE("..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl")
    //console.log(ordersE.slice(0, 2))
    console.log("Orders loaded");


    await loadDataTPCHE.loadCustomersEWithOrders("..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl"
    ,ordersE)
    console.log("Customers loaded")
*/
/*
    // Create Order1
    const order1 = new mongoose.Types.Subdocument({
        o_orderkey: 1,
        o_custkey: 1,
        o_orderstatus:"String",
        o_totalprice:"String",
        o_orderdate: Date.now(),
        o_orderpriority:"String",
        o_clerk:"String",
        o_shippriority:"String",
        o_comment:"String"
    }, OrdersESchema);

    console.log(order1);
    console.log("order1 created");

    // Create CustomerEWithOrders
    const customer1 = await CustomerEWithOrders.create
    ({
        _id:1,
        c_name: "String",
        c_address: "String",

        c_nationkey: 1,
        c_phone: "String",
        c_acctbal: 1,
        c_mktsegment: "String",
        c_commen: "String",

        c_orders: [order1]
    });
*/
}

run().catch((err) => console.error(err));