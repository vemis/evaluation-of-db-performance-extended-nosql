import mongoose from "mongoose";
mongoose.pluralize(null)

const OrdersEOnlyOCommentIndexedSchema = new mongoose.Schema({
    // o_orderkey stored as _id
    _id: Number,
    o_orderdate: Date,
    o_comment: { type: String, index: 'text' }
});

export default mongoose.model("ordersEOnlyOCommentIndexed", OrdersEOnlyOCommentIndexedSchema);
