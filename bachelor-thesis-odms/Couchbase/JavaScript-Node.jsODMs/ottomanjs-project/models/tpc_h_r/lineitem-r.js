import { Schema, model } from 'ottoman';



const LineitemRSchema = new Schema({
    //l_id
    id: String,

    l_orderkey: String,
    l_partkey: String,
    l_suppkey: String,
    l_ps_id: String,

    l_linenumber:Number,
    l_quantity:Number,

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

LineitemRSchema.index.findBy_l_orderkey = {
    by: "l_orderkey",
    type: "n1ql"
};

LineitemRSchema.index.findBy_l_partkey = {
    by: "l_partkey",
    type: "n1ql"
};

LineitemRSchema.index.findBy_l_suppkey = {
    by: "l_suppkey",
    type: "n1ql"
};

LineitemRSchema.index.findBy_l_ps_id = {
  by: "l_ps_id",
  type: "n1ql"
};

export const LineitemR =  model("LineitemR", LineitemRSchema,
    {
        idKey: "id",
        scopeName:'ottoman_scope_r',
        collectionName:'LineitemR',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });
