import mongoose from 'mongoose';
import LineitemESchema from './lineitem-e.js';
mongoose.pluralize(null);

const OrdersEWithLineitemsSchema = new mongoose.Schema({
    _id: Number,
    o_custkey:      { type: Number, index: true },
    o_orderstatus:  String,
    o_totalprice:   String,
    o_orderdate:    Date,
    o_orderpriority: String,
    o_clerk:        String,
    o_shippriority: String,
    o_comment:      String,
    o_lineitems:    [LineitemESchema]
});

export default mongoose.model('ordersEWithLineitems', OrdersEWithLineitemsSchema);
