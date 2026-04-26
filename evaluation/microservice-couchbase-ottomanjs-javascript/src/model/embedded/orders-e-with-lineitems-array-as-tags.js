import { Schema, model } from 'ottoman';

const OrdersEWithLineitemsArrayAsTagsSchema = new Schema({
    id: String,
    o_orderdate: String,
    o_lineitems_tags: [Schema.Types.Mixed]
});

export const OrdersEWithLineitemsArrayAsTags = model('OrdersEWithLineitemsArrayAsTags', OrdersEWithLineitemsArrayAsTagsSchema, {
    idKey: 'id',
    collectionName: 'OrdersEWithLineitemsArrayAsTags',
    scopeName: 'ottoman_scope_e',
    keyGenerator: ({ metadata }) => '',
    keyGeneratorDelimiter: ''
});

OrdersEWithLineitemsArrayAsTags.createIndexes = async () => {};
