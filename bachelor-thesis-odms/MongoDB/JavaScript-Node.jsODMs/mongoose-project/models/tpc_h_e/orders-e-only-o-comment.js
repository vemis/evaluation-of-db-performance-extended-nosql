import mongoose from "mongoose";
mongoose.pluralize(null)

const OrdersEOnlyOCommentSchema = new mongoose.Schema({
    // o_orderkey stored as _id
    _id: Number,
    o_orderdate: Date,
    o_comment: String
});

export default mongoose.model("ordersEOnlyOComment", OrdersEOnlyOCommentSchema);
