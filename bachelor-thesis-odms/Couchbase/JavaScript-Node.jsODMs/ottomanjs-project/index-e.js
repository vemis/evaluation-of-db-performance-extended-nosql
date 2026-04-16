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
import {OrdersEWithLineitemsArrayAsTags} from "./models/tpc_h_e/orders-e-with-lineitems-array-as-tags.js";
import {OrdersEWithLineitemsArrayAsTagsIndexed} from "./models/tpc_h_e/orders-e-with-lineitems-array-as-tags-indexed.js";
import {OrdersEWithCustomerWithNationWithRegion} from "./models/tpc_h_e/orders-e-with-customer-with-nation-with-region.js";
import {OrdersEOnlyOComment} from "./models/tpc_h_e/orders-e-only-o-comment.js";
import {OrdersEOnlyOCommentIndexed} from "./models/tpc_h_e/orders-e-only-o-comment-indexed.js";
import {loadOrdersEWithCustomerWithNationWithRegion} from "./load-data-tpc-h-e.js";

// recreate __dirname in ES modules
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

async function run(){
    const basePath = path.join(__dirname, '..', '..', '..', 'dataset', 'TPC-H', 'tpch-data');


    // Create indexes
    console.log("Creating indexes");
    await CustomerEWithOrders.createIndexes();
    await OrdersEWithLineitems.createIndexes();
    await OrdersEWithLineitemsArrayAsTags.createIndexes();
    await OrdersEWithLineitemsArrayAsTagsIndexed.createIndexes();
    await OrdersEWithCustomerWithNationWithRegion.createIndexes();
    await OrdersEOnlyOComment.createIndexes();
    await OrdersEOnlyOCommentIndexed.createIndexes();
    console.log("Indexes created");


    console.log("Query:")
    const res = await queriesE.R9();
    console.log(res[0])
    console.log(res.length)


    /*
    await loadDataE.loadOrdersEOnlyOCommentIndexed(
        path.join(basePath, 'orders.tbl')
    )

    await loadDataE.loadOrdersEOnlyOComment(
        path.join(basePath, 'orders.tbl')
    )

    await loadDataE.loadOrdersEWithCustomerWithNationWithRegion(
        path.join(basePath, 'orders.tbl'),
        path.join(basePath, 'customer.tbl'),
        path.join(basePath, 'nation.tbl'),
        path.join(basePath, 'region.tbl')
    )

    await loadDataE.loadOrdersEWithLineitemsArrayAsTagsIndexed(
        path.join(basePath, 'orders.tbl'),
        path.join(basePath, 'lineitem.tbl')
    )

    await loadDataE.loadOrdersEWithLineitemsArrayAsTags(
        path.join(basePath, 'orders.tbl'),
        path.join(basePath, 'lineitem.tbl')
    )

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