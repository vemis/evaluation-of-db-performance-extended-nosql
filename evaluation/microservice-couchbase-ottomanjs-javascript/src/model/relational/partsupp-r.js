import { Schema, model } from 'ottoman';

const PartsuppRSchema = new Schema({
    id: String,
    ps_partkey: String,
    ps_suppkey: String,
    ps_availqty: Number,
    ps_supplycost: Number,
    ps_comment: String
});

PartsuppRSchema.index.findBy_ps_partkey = { by: 'ps_partkey', type: 'n1ql' };
PartsuppRSchema.index.findBy_ps_suppkey = { by: 'ps_suppkey', type: 'n1ql' };

export const PartsuppR = model('PartsuppR', PartsuppRSchema, {
    idKey: 'id',
    collectionName: 'PartsuppR',
    scopeName: 'ottoman_scope_r',
    keyGenerator: ({ metadata }) => '',
    keyGeneratorDelimiter: ''
});
