import { Schema, model } from 'ottoman';

const NationRSchema = new Schema({
    id: String,
    n_name: String,
    n_regionkey: String,
    n_comment: String
});

NationRSchema.index.findBy_n_regionkey = { by: 'n_regionkey', type: 'n1ql' };

export const NationR = model('NationR', NationRSchema, {
    idKey: 'id',
    collectionName: 'NationR',
    scopeName: 'ottoman_scope_r',
    keyGenerator: ({ metadata }) => '',
    keyGeneratorDelimiter: ''
});
