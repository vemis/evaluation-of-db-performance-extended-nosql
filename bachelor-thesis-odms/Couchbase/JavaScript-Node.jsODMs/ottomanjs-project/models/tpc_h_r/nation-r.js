import { Schema, model } from 'ottoman';



const NationRSchema = new Schema({
    //n_nationkey key
    id: String,
   
    n_name:String,

    n_regionkey: String, //foreign key

    n_comment: String
});

NationRSchema.index.findBy_n_regionkey = {
    by: "n_regionkey",
    type: 'n1ql',
};
export const NationR = model("NationR", NationRSchema,
    {
        idKey: "id",
        scopeName:'ottoman_scope_r',
        collectionName:'NationR',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });
