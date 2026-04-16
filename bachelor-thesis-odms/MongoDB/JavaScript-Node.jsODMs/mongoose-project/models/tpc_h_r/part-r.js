import mongoose from "mongoose";
mongoose.pluralize(null)


const PartRSchema = new mongoose.Schema({
        
    //@Id private int p_partkey;
    _id: Number,
    
    p_name: String,
    p_mfgr: String,
    p_brand: String,
    p_type: String,
    p_size: Number,
    p_container: String,
    p_retailprice: Number,
    p_commen: String
});

export default mongoose.model("PartR", PartRSchema);
