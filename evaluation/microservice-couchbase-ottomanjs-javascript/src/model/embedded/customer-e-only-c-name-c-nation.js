import { Schema } from 'ottoman';
import NationEOnlyNNameNRegionSchema from './nation-e-only-n-name-n-region.js';

const CustomerEOnlyCNameCNationSchema = new Schema({
    c_custkey: Number,
    c_name: String,
    c_nationkey: Number,
    c_nation: NationEOnlyNNameNRegionSchema
});

export default CustomerEOnlyCNameCNationSchema;
