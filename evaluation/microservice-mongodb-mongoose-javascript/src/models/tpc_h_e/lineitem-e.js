import mongoose from 'mongoose';
mongoose.pluralize(null);

const LineitemESchema = new mongoose.Schema({
    l_id:        { type: String, index: true },
    l_orderkey:  { type: Number, index: true },
    l_partkey:   { type: Number, index: true },
    l_suppkey:   { type: Number, index: true },
    l_ps_id:     { type: String, index: true },
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
