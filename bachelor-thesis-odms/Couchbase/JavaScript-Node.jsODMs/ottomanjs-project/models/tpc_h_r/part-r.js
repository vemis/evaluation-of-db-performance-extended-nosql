import { Schema, model } from 'ottoman';


const PartRSchema = new Schema({
        
    //@Id private int p_partkey;
    id: String,
    
    p_name: String,
    p_mfgr: String,
    p_brand: String,
    p_type: String,
    p_size: Number,
    p_container: String,
    p_retailprice: Number,
    p_commen: String
});

export const PartR = model("PartR", PartRSchema,
    {
        idKey: "id",
        scopeName:'ottoman_scope_r',
        collectionName:'PartR',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });
