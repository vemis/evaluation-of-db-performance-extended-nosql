import { Schema } from 'ottoman';

const LineitemESchema = new Schema({
    l_id: String,
    l_orderkey: String,
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
    l_shipdate: String,
    l_commitdate: String,
    l_receiptdate: String,
    l_shipinstruct: String,
    l_shipmode: String,
    l_comment: String
});

export default LineitemESchema;
