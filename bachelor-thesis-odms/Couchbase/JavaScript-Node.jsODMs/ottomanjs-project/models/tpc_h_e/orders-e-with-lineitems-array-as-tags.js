import { Schema, model } from 'ottoman';

const OrdersEWithLineitemsArrayAsTagsSchema = new Schema({
    // o_orderkey is used as the document id
    id: String,
    o_orderdate: Date,
    o_lineitems_tags: [Schema.Types.Mixed]
});

export const OrdersEWithLineitemsArrayAsTags = model("OrdersEWithLineitemsArrayAsTags", OrdersEWithLineitemsArrayAsTagsSchema,
    {
        idKey: "id",
        collectionName: 'OrdersEWithLineitemsArrayAsTags',
        scopeName: 'ottoman_scope_e',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });

// No array index on this collection — R3 tests unindexed array filtering.
// The indexed counterpart is OrdersEWithLineitemsArrayAsTagsIndexed (used in R4).
OrdersEWithLineitemsArrayAsTags.createIndexes = async () => {};
