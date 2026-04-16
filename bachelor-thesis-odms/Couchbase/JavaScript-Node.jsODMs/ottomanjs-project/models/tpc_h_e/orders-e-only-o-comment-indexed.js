import ottoman, { Schema, model } from 'ottoman';

const OrdersEOnlyOCommentIndexedSchema = new Schema({
    // o_orderkey is used as the document id
    id: String,
    o_orderdate: Date,
    o_comment: String
});

export const OrdersEOnlyOCommentIndexed = model("OrdersEOnlyOCommentIndexed", OrdersEOnlyOCommentIndexedSchema,
    {
        idKey: "id",
        collectionName: 'OrdersEOnlyOCommentIndexed',
        scopeName: 'ottoman_scope_e',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });

// Couchbase does not have a MongoDB-style text index for N1QL.
// A regular index on o_comment is created here; it accelerates equality/range lookups
// but not REGEXP_CONTAINS with a non-anchored pattern.
// For true full-text search acceleration an FTS (Full Text Search) index would be
// required alongside the SEARCH() N1QL function, but that is a separate Couchbase service.
async function createIndexesOrdersEOnlyOCommentIndexed() {
    await ottoman.getDefaultInstance().query(
        `CREATE INDEX idx_OrdersEOnlyOCommentIndexed_o_comment IF NOT EXISTS
         ON ottoman_bucket_e.ottoman_scope_e.OrdersEOnlyOCommentIndexed (o_comment)`
    );
}

OrdersEOnlyOCommentIndexed.createIndexes = createIndexesOrdersEOnlyOCommentIndexed;
