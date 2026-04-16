import { Schema } from 'ottoman';
import RegionEOnlyNameSchema from './region-e-only-name.js';

const NationEOnlyNNameNRegionSchema = new Schema({
    n_nationkey: Number,
    n_name: String,
    n_regionkey: Number,
    n_region: RegionEOnlyNameSchema
});

export default NationEOnlyNNameNRegionSchema;
