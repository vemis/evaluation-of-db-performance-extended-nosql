import { Schema, model } from 'ottoman';

const OrdersEOnlyOCommentSchema = new Schema({
    id: String,
    o_orderdate: String,
    o_comment: String
});

export const OrdersEOnlyOComment = model('OrdersEOnlyOComment', OrdersEOnlyOCommentSchema, {
    idKey: 'id',
    collectionName: 'OrdersEOnlyOComment',
    scopeName: 'ottoman_scope_e',
    keyGenerator: ({ metadata }) => '',
    keyGeneratorDelimiter: ''
});

OrdersEOnlyOComment.createIndexes = async () => {};
