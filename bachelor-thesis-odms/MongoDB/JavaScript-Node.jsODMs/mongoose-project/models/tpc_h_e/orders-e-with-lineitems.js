import mongoose from "mongoose";
import LineitemESchema from "./lineitem-e.js";
mongoose.pluralize(null)

const OrdersEWithLineitemsSchema = new mongoose.Schema({
    // o_orderkey stored as _id
    _id: Number,
    o_custkey: {
        type: Number,
        index: true
    },
    o_orderstatus: String,
    o_totalprice: String,
    o_orderdate: Date,
    o_orderpriority: String,
    o_clerk: String,
    o_shippriority: String,
    o_comment: String,

    o_lineitems: [LineitemESchema]
});

//OrdersEWithLineitemsSchema.index({ "o_lineitems.l_id": 1 });
//OrdersEWithLineitemsSchema.index({ "o_lineitems.l_orderkey": 1 });
//OrdersEWithLineitemsSchema.index({ "o_lineitems.l_partkey": 1 });
//OrdersEWithLineitemsSchema.index({ "o_lineitems.l_suppkey": 1 });
//OrdersEWithLineitemsSchema.index({ "o_lineitems.l_ps_id": 1 });

export default mongoose.model("ordersEWithLineitems", OrdersEWithLineitemsSchema);
