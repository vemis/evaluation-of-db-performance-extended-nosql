import ottoman, { Schema, model } from 'ottoman';

const OrdersEOnlyOCommentIndexedSchema = new Schema({
    id: String,
    o_orderdate: String,
    o_comment: String
});

export const OrdersEOnlyOCommentIndexed = model('OrdersEOnlyOCommentIndexed', OrdersEOnlyOCommentIndexedSchema, {
    idKey: 'id',
    collectionName: 'OrdersEOnlyOCommentIndexed',
    scopeName: 'ottoman_scope_e',
    keyGenerator: ({ metadata }) => '',
    keyGeneratorDelimiter: ''
});

async function createIndexes() {
    await ottoman.getDefaultInstance().query(
        `CREATE INDEX idx_OrdersEOnlyOCommentIndexed_o_comment IF NOT EXISTS
         ON \`bucket-main\`.\`ottoman_scope_e\`.\`OrdersEOnlyOCommentIndexed\` (o_comment)`
    );
}

OrdersEOnlyOCommentIndexed.createIndexes = createIndexes;
