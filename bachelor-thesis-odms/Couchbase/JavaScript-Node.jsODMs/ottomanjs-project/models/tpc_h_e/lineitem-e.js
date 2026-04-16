import { Schema } from 'ottoman';

const LineitemESchema = new Schema({
    l_id: String,
    l_orderkey: Number,
    l_partkey: Number,
    l_suppkey: Number,
    l_ps_id: String,
    l_linenumber: Number,
    l_quantity: Number,
    l_extendedprice: Number,
    l_discount: Number,
    l_tax: Number,
    l_returnflag: String,
    l_linestatus: String,
    l_shipdate: Date,
    l_commitdate: Date,
    l_receiptdate: Date,
    l_shipinstruct: String,
    l_shipmode: String,
    l_comment: String
});

export default LineitemESchema;
