import { Schema, model } from 'ottoman';

const OrdersESchema = new Schema({
    //primary key embedded
    o_orderkey: String,
    o_custkey: String,
    o_orderstatus:String,
    o_totalprice:Number,
    o_orderdate: Date,
    o_orderpriority:String,
    o_clerk:String,
    o_shippriority:String,
    o_comment:String
});



export default OrdersESchema;
