import { Schema, model } from 'ottoman';

const RegionRSchema = new Schema({
    id: String,
    r_name: String,
    r_comment: String
});

export const RegionR = model('RegionR', RegionRSchema, {
    idKey: 'id',
    collectionName: 'RegionR',
    scopeName: 'ottoman_scope_r',
    keyGenerator: ({ metadata }) => '',
    keyGeneratorDelimiter: ''
});
