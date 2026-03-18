// this stupid import is the only way how I was able to use the Ottoman.js
import {} from './config/db-r.js';

import {RegionR} from "./models/tpc_h_r/region-r.js";
import {NationR} from "./models/tpc_h_r/nation-r.js";
import ottoman, {getDefaultInstance, Query} from "ottoman";
import {loadRegions} from "./load-data-tpc-h-r.js";
import * as loadDataTPCHR from "./load-data-tpc-h-r.js";

import * as queriesR from "./benchmarks/queries-r.js";
import {benchmarkQuery} from "./benchmarks/benchmarks.js";

async function run() {
    /*const reg1 = new RegionR({
        id: "1",
        r_name: "test",
        r_comment: "test"
    });

    await reg1.save();
    console.log("reg1 saved");*/

    //await ottoman.start()
/*
    const nat1 = new NationR({
        id: "1",
        n_name: "test",
        n_regionkey: "1",
        n_comment: "test"
    });



    await nat1.save();
    console.log("nat1 saved");*/
/*
    await loadDataTPCHR.loadRegions("..\\..\\..\\dataset\\TPC-H\\tpch-data\\region.tbl")
    console.log("Regions loaded")

    await loadDataTPCHR.loadNations("..\\..\\..\\dataset\\TPC-H\\tpch-data\\nation.tbl")
    console.log("Nations loaded")

    await loadDataTPCHR.loadCustomers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl")
    console.log("Customers loaded")

    await loadDataTPCHR.loadOrders("..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl")
    console.log("Orders loaded");

    console.log("Loading Lineitems")
    await loadDataTPCHR.loadLineitems("..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl")
    console.log("Lineitems loaded");

    console.log("Loading Partsupps")
    await loadDataTPCHR.loadPartsupps("..\\..\\..\\dataset\\TPC-H\\tpch-data\\partsupp.tbl")
    console.log("Partsupps loaded");

    console.log("Loading Suppliers")
    await loadDataTPCHR.loadSuppliers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\supplier.tbl")
    console.log("Suppliers loaded")

    console.log("Loading Parts")
    await loadDataTPCHR.loadParts("..\\..\\..\\dataset\\TPC-H\\tpch-data\\part.tbl")
    console.log("Parts loaded")
*/

    //const a2 = await queriesR.A2();
    //console.log(a2.length)
    //console.log(a2)

    //const test = await queriesR.TEST();
    //console.log(test);

    /*onsole.log(
        await queriesR.A2()
    )*/
    benchmarkQuery(
        queriesR.A1,
        queriesR.A2,
        queriesR.B1,
        queriesR.C2,
        queriesR.D1
    )

}

run().catch(err => console.error(err));