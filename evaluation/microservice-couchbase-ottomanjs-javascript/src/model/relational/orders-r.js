import { Schema, model } from 'ottoman';

const OrdersRSchema = new Schema({
    id: String,
    o_orderkey_field: Number,
    o_custkey: String,
    o_orderstatus: String,
    o_totalprice: Number,
    o_orderdate: String,
    o_orderpriority: String,
    o_clerk: String,
    o_shippriority: String,
    o_comment: String
});

OrdersRSchema.index.findBy_o_custkey = { by: 'o_custkey', type: 'n1ql' };
OrdersRSchema.index.findBy_o_orderkey_field = { by: 'o_orderkey_field', type: 'n1ql' };

export const OrdersR = model('OrdersR', OrdersRSchema, {
    idKey: 'id',
    collectionName: 'OrdersR',
    scopeName: 'ottoman_scope_r',
    keyGenerator: ({ metadata }) => '',
    keyGeneratorDelimiter: ''
});
