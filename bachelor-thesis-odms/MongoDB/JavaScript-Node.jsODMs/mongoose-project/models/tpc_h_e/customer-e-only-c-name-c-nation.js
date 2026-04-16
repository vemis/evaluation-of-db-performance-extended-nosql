import mongoose from "mongoose";
import NationEOnlyNNameNRegionSchema from "./nation-e-only-n-name-n-region.js";

const CustomerEOnlyCNameCNationSchema = new mongoose.Schema({
    c_custkey: Number,
    c_name: String,
    c_nationkey: Number,
    c_nation: NationEOnlyNNameNRegionSchema
}, { _id: false });

export default CustomerEOnlyCNameCNationSchema;
