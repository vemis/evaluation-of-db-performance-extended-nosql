import {insertAll, partition, readDataFromCustomSeparator} from "./load-data-tpc-h.js";
import {CustomerEWithOrders} from "./models/tpc_h_e/customer-e-with-orders.js";


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
            console.log("Batch inserted!")
        }
        //await CustomerEWithOrders.insertMany( rowsOfSchemas );

    } catch (err) {
        console.error(err);
    }
}

export {
    loadOrders,
    loadCustomersEWithOrders
}