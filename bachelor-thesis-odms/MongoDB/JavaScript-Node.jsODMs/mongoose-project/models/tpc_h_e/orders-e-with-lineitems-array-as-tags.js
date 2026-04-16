import mongoose from "mongoose";
mongoose.pluralize(null)

const OrdersEWithLineitemsArrayAsTagsSchema = new mongoose.Schema({
    // o_orderkey stored as _id
    _id: Number,
    o_orderdate: Date,
    o_lineitems_tags: [mongoose.Schema.Types.Mixed]
});

export default mongoose.model("ordersEWithLineitemsArrayAsTags", OrdersEWithLineitemsArrayAsTagsSchema);
