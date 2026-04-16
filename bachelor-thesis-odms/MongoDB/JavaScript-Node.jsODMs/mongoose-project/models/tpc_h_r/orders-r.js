import mongoose from "mongoose";
mongoose.pluralize(null)


const OrdersRSchema = new mongoose.Schema({
    //o_orderkey
    _id: Number,
    o_custkey: {
        type: Number,
        index: true
    },
    o_orderstatus:String,
    o_totalprice:String,
    o_orderdate: Date,
    o_orderpriority:String,
    o_clerk:String,
    o_shippriority:String,
    o_comment:String
});

export default mongoose.model("OrdersR", OrdersRSchema);
