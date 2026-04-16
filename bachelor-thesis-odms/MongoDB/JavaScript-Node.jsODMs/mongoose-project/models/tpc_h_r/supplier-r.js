import mongoose from "mongoose";
mongoose.pluralize(null)


const SupplierRSchema = new mongoose.Schema({
        
    //@Id private int s_suppkey;
    _id: Number,
    
    s_name: String,
    s_address: String,
    
    s_nationkey: {
        type: Number,
        index: true
    },
    s_phone: String,
    s_acctbal: Number,

    s_comment: String
});

export default mongoose.model("SupplierR", SupplierRSchema);
