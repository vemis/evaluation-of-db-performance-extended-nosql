import mongoose from "mongoose";

const RegionEOnlyNameSchema = new mongoose.Schema({
    r_regionkey: Number,
    r_name: String
}, { _id: false });

export default RegionEOnlyNameSchema;
