import ottoman, { Schema, model } from 'ottoman';
import OrdersESchema from "./orders-e.js";

const CustomerEWithOrdersSchema = new Schema({
    //c_custkey
    id: String,
    c_name: String,
    c_address: String,

    c_nationkey: String,
    c_phone: String,
    c_acctbal: Number,
    c_mktsegment: String,
    c_commen: String,

    c_orders: [OrdersESchema]
});

CustomerEWithOrdersSchema.index.findBy_c_nationkey = {
    by: "c_nationkey",
    type: 'n1ql',
};




/*CustomerEWithOrdersSchema.index.findBy_o_orderkey = {
    by: "c_orders.o_orderkey",
    type: 'n1ql',
};

CustomerEWithOrdersSchema.index.findBy_o_custkey = {
    by: "c_orders.o_custkey",
    type: 'n1ql',
};*/

async function createEmbeddedIndexesCustomerEWithOrders() {
    await ottoman.getDefaultInstance().query(
        `
            CREATE INDEX idx_customers_orders IF NOT EXISTS
                ON ottoman_bucket_e.ottoman_scope_e.CustomerEWithOrders (
                        DISTINCT ARRAY o.o_orderkey FOR o IN c_orders END
                    )
        `
    )
}


export const CustomerEWithOrders = model("CustomerEWithOrders", CustomerEWithOrdersSchema,
    {
        idKey: "id",
        collectionName: 'CustomerEWithOrders',
        scopeName: 'ottoman_scope_e',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });

CustomerEWithOrders.createIndexes = createEmbeddedIndexesCustomerEWithOrders;
