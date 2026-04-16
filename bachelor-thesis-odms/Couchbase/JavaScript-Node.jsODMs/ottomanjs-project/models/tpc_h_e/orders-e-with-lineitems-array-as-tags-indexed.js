import ottoman, { Schema, model } from 'ottoman';

const OrdersEWithLineitemsArrayAsTagsIndexedSchema = new Schema({
    // o_orderkey is used as the document id
    id: String,
    o_orderdate: Date,
    o_lineitems_tags_indexed: [Schema.Types.Mixed]
});

export const OrdersEWithLineitemsArrayAsTagsIndexed = model("OrdersEWithLineitemsArrayAsTagsIndexed", OrdersEWithLineitemsArrayAsTagsIndexedSchema,
    {
        idKey: "id",
        collectionName: 'OrdersEWithLineitemsArrayAsTagsIndexed',
        scopeName: 'ottoman_scope_e',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });

async function createIndexesOrdersEWithLineitemsArrayAsTagsIndexed() {
    await ottoman.getDefaultInstance().query(
        `CREATE INDEX idx_OrdersEWithLineitemsArrayAsTagsIndexed_tags IF NOT EXISTS
         ON ottoman_bucket_e.ottoman_scope_e.OrdersEWithLineitemsArrayAsTagsIndexed
         (DISTINCT ARRAY tag FOR tag IN o_lineitems_tags_indexed END)`
    );
}

OrdersEWithLineitemsArrayAsTagsIndexed.createIndexes = createIndexesOrdersEWithLineitemsArrayAsTagsIndexed;
