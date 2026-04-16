import mongoose from "mongoose";
mongoose.pluralize(null)


const OrdersESchema = new mongoose.Schema({
    o_orderkey: {
        type: Number,
        index: true
    },
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

export default OrdersESchema;
