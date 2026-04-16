import mongoose from "mongoose";
import RegionEOnlyNameSchema from "./region-e-only-name.js";

const NationEOnlyNNameNRegionSchema = new mongoose.Schema({
    n_nationkey: Number,
    n_name: String,
    n_regionkey: Number,
    n_region: RegionEOnlyNameSchema
}, { _id: false });

export default NationEOnlyNNameNRegionSchema;
