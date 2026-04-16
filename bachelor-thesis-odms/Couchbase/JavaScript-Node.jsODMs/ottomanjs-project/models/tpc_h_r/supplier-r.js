import { Schema, model } from 'ottoman';



const SupplierRSchema = new Schema({
        
    //@Id private int s_suppkey;
    id: String,
    
    s_name: String,
    s_address: String,
    
    s_nationkey: String,
    s_phone: String,
    s_acctbal: Number,

    s_comment: String
});

SupplierRSchema.index.findBy_s_nationkey = {
    by: "s_nationkey",
    type: 'n1ql',
};

export const SupplierR = model("SupplierR", SupplierRSchema,
    {
        idKey: "id",
        scopeName:'ottoman_scope_r',
        collectionName:'SupplierR',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });
