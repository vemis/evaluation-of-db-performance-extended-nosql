import mongoose from "mongoose";
import OrdersESchema from "./orders-e.js";
mongoose.pluralize(null)


const CustomerEWithOrdersSchema = new mongoose.Schema({
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
    c_commen: String,

    c_orders: [OrdersESchema]
});

export default mongoose.model("CustomerEWithOrders", CustomerEWithOrdersSchema);
