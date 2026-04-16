import { Schema, model } from 'ottoman';


const RegionRSchema = new Schema({
    //r_region key
    id: String,
    r_name: String,
    r_comment: String
});

export const RegionR = model("RegionR", RegionRSchema,
    {
        idKey: "id",
        scopeName:'ottoman_scope_r',
        collectionName:'RegionR',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });
