import { Schema, model } from 'ottoman';


const CustomerRSchema = new Schema({
    //c_custkey
    id: String,
    c_name: String,
    c_address: String,

    c_nationkey: String,
    c_phone: String,
    c_acctbal: Number,
    c_mktsegment: String,
    c_commen: String
});

CustomerRSchema.index.findBy_c_nationkey = {
    by: "c_nationkey",
    type: 'n1ql',
};

export const CustomerR =  model("CustomerR", CustomerRSchema,
    {
        idKey: "id",
        collectionName: 'CustomerR',
        scopeName: 'ottoman_scope_r',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });
