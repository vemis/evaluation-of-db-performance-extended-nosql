import ottoman, { Schema, model } from 'ottoman';
import LineitemESchema from "./lineitem-e.js";

const OrdersEWithLineitemsSchema = new Schema({
    // o_orderkey is used as the document id
    id: String,
    o_custkey: String,
    o_orderstatus: String,
    o_totalprice: String,
    o_orderdate: Date,
    o_orderpriority: String,
    o_clerk: String,
    o_shippriority: String,
    o_comment: String,

    o_lineitems: [LineitemESchema]
});

async function createIndexesOrdersEWithLineitems() {
    await ottoman.getDefaultInstance().query(
        `CREATE INDEX idx_OrdersEWithLineitems_l_partkey IF NOT EXISTS
         ON ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems
         (DISTINCT ARRAY l.l_partkey FOR l IN o_lineitems END)`
    );
    await ottoman.getDefaultInstance().query(
        `CREATE INDEX idx_OrdersEWithLineitems_l_orderkey IF NOT EXISTS
         ON ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems
         (DISTINCT ARRAY l.l_orderkey FOR l IN o_lineitems END)`
    );
    await ottoman.getDefaultInstance().query(
        `CREATE INDEX idx_OrdersEWithLineitems_l_suppkey IF NOT EXISTS
         ON ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems
         (DISTINCT ARRAY l.l_suppkey FOR l IN o_lineitems END)`
    );
    await ottoman.getDefaultInstance().query(
        `CREATE INDEX idx_OrdersEWithLineitems_l_id IF NOT EXISTS
         ON ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems
         (DISTINCT ARRAY l.l_id FOR l IN o_lineitems END)`
    );
    await ottoman.getDefaultInstance().query(
        `CREATE INDEX idx_OrdersEWithLineitems_l_ps_id IF NOT EXISTS
         ON ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems
         (DISTINCT ARRAY l.l_ps_id FOR l IN o_lineitems END)`
    );
    await ottoman.getDefaultInstance().query(
        `CREATE INDEX idx_OrdersEWithLineitems_o_custkey IF NOT EXISTS
         ON ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitems
         (o_custkey)`
    );
}

export const OrdersEWithLineitems = model("OrdersEWithLineitems", OrdersEWithLineitemsSchema,
    {
        idKey: "id",
        collectionName: 'OrdersEWithLineitems',
        scopeName: 'ottoman_scope_e',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });

OrdersEWithLineitems.createIndexes = createIndexesOrdersEWithLineitems;
