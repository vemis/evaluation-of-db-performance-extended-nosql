// this stupid import is the only way how I was able to use the Ottoman.js
import {} from './config/db-e.js';

import ottoman from "ottoman";
import OrdersESchema from "./models/tpc_h_e/orders-e.js";
import {CustomerEWithOrders} from "./models/tpc_h_e/customer-e-with-orders.js";
import {OrdersEWithLineitems} from "./models/tpc_h_e/orders-e-with-lineitems.js";
import * as queriesE from "./benchmarks/queries-e.js";
import * as loadDataE from "./load-data-tpc-h-e.js";
import {benchmarkQuery} from "./benchmarks/benchmarks.js";



import path from 'path';
import { fileURLToPath } from 'url';

// recreate __dirname in ES modules
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

async function run(){
    const basePath = path.join(__dirname, '..', '..', '..', 'dataset', 'TPC-H', 'tpch-data');


    // Create indexes
    console.log("Creating indexes");
    await CustomerEWithOrders.createIndexes();
    await OrdersEWithLineitems.createIndexes();
    console.log("Indexes created");


    console.log("Query:")
    const res = await queriesE.R2();
    console.log(res[0])
    console.log(res.length)

    /*
    const lineitemsE = await loadDataE.createLineitemsE(path.join(basePath, 'lineitem.tbl'))
    await loadDataE.loadOrdersEWithLineitems(path.join(basePath, 'orders.tbl'), lineitemsE);


    // Create ordersE
    const ordersE =  await loadDataE.loadOrders("..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl")
    // Load CustomersEWithOrders
    await loadDataE.loadCustomersEWithOrders("..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl",
        ordersE)
    */



    /*benchmarkQuery(
        queriesE.C2
    )*/

    console.log("End")
}

run().catch(err => console.error(err));