// this stupid import is the only way how I was able to use the Ottoman.js
import {} from './config/db-e.js';

import ottoman from "ottoman";
import OrdersESchema from "./models/tpc_h_e/orders-e.js";
import {createEmbeddedIndexesCustomerEWithOrders, CustomerEWithOrders} from "./models/tpc_h_e/customer-e-with-orders.js";
import * as queriesE from "./benchmarks/queries-e.js";

async function run(){

    // create indexes of orders

    /*await createEmbeddedIndexesCustomerEWithOrders();
    const cuse1 = new CustomerEWithOrders({
        id: "1",
        c_name: "test",
        c_address: "test",

        c_nationkey: "1",
        c_phone: "test",
        c_acctbal: 123,
        c_mktsegment: "test",
        c_commen: "test",

        c_orders: [
            {
                o_orderkey: "1",
                o_custkey: "1",
                o_orderstatus: "test",
                o_totalprice: 123,
                o_orderdate: new Date("1993-01-01"),
                o_orderpriority: "test",
                o_clerk: "test",
                o_shippriority: "test",
                o_comment:"test"
            }
        ]
    });

    await cuse1.save();*/

    console.log(
        await queriesE.C2()
    );
}

run().catch(err => console.error(err));