import { Schema, model } from 'ottoman';

const OrdersEOnlyOCommentSchema = new Schema({
    // o_orderkey is used as the document id
    id: String,
    o_orderdate: Date,
    o_comment: String
});

export const OrdersEOnlyOComment = model("OrdersEOnlyOComment", OrdersEOnlyOCommentSchema,
    {
        idKey: "id",
        collectionName: 'OrdersEOnlyOComment',
        scopeName: 'ottoman_scope_e',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });

// No index for R6 — the query uses REGEXP_CONTAINS without a supporting index.
OrdersEOnlyOComment.createIndexes = async () => {};
