import mongoose, { mongo } from "mongoose";

// setting off the letter s on the end of a collection
// probably have no meaning -> needs to be called before the initialization
// that already happened in the tpc_h_r folder
mongoose.pluralize(null)

import RegionR from "./models/tpc_h_r/region-r.js";
import NationR from "./models/tpc_h_r/nation-r.js";
import CustomerR from "./models/tpc_h_r/customer-r.js";
import OrdersR from "./models/tpc_h_r/orders-r.js";
import PartR from "./models/tpc_h_r/part-r.js";
import PartsuppR from "./models/tpc_h_r/partsupp-r.js";
import SupplierR from "./models/tpc_h_r/supplier-r.js";
import LineitemR from "./models/tpc_h_r/lineitem-r.js";

import * as loadDataTPCH from "./load-data-tpc-h.js";
import * as queriesR from "./benchmarks/queries-r.js";

import * as benchmarksR from "./benchmarks/benchmarks-r.js";

async function run() {
    // Connect to MongoDB
    await mongoose.connect("mongodb://localhost:27017/mongoose_database_r");
    
    console.log("Connected to MongoDB.");
    /*
    await loadDataTPCH.loadRegions("..\\..\\..\\dataset\\TPC-H\\tpch-data\\region.tbl")
    console.log("Regions loaded")

    await loadDataTPCH.loadNations("..\\..\\..\\dataset\\TPC-H\\tpch-data\\nation.tbl")
    console.log("Nations loaded")
    
    await loadDataTPCH.loadCustomers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl")
    console.log("Customers loaded")

    await loadDataTPCH.loadOrders("..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl")
    console.log("Orders loaded");

    console.log("Loading Lineitems")
    await loadDataTPCH.loadLineitems("..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl")
    console.log("Lineitems loaded");

    console.log("Loading Partsupps")
    await loadDataTPCH.loadPartsupps("..\\..\\..\\dataset\\TPC-H\\tpch-data\\partsupp.tbl")
    console.log("Partsupps loaded");

    console.log("Loading Suppliers")
    await loadDataTPCH.loadSuppliers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\supplier.tbl")
    console.log("Suppliers loaded")

    console.log("Loading Parts")
    await loadDataTPCH.loadParts("..\\..\\..\\dataset\\TPC-H\\tpch-data\\part.tbl")
    console.log("Parts loaded")
*/

    // Queries
    //console.log("Start A1")
    //const a1 = await queriesR.A1()
    //console.log(a1.length)
    //console.log("A1 finished")

    benchmarksR.benchmarkQuery(
        //queriesR.A1,
        //queriesR.A2,
        queriesR.B1
    )
    console.log("Start qeury")
    const c = await queriesR.B1()
    //c.forEach(query =>{
    //    console.log("test");
    //})
    console.log(c.length)
    console.log("end query")
    /*
        // Insert Joe Doe
        const customer1 = await CustomerR.insertMany([
            {
                _id: 1,
                c_name: 'Customer#000000001',
                c_address: 'IVhzIApeRb ot,c,E',
                c_nationkey: 15,
                c_phone: '25-989-741-2988',
                c_acctbal: 711.56,
                c_mktsegment: 'BUILDING',
                c_commen: 'to the even, regular platelets. regular, ironic epitaphs nag e'
            }

        ]);
        console.log("customers saved")
    */
}

run().catch((err) => console.error(err));