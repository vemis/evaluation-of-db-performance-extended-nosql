import mongoose from "mongoose";
mongoose.pluralize(null)


const CustomerRSchema = new mongoose.Schema({
    //c_custkey
    _id: Number,
    c_name: String,
    c_address: String,

    c_nationkey: {
        type: Number,
        index: true
    },
    c_phone: String,
    c_acctbal: Number,
    c_mktsegment: String,
    c_commen: String
});

export default mongoose.model("CustomerR", CustomerRSchema);
