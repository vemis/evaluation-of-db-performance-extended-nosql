import { Schema } from 'ottoman';

const RegionEOnlyNameSchema = new Schema({
    r_regionkey: Number,
    r_name: String
});

export default RegionEOnlyNameSchema;
