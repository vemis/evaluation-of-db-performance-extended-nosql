import mongoose from "mongoose";
mongoose.pluralize(null)


const NationRSchema = new mongoose.Schema({
    //n_nationkey key
    _id: Number,
   
    n_name:String,

    n_regionkey: {
        type: Number,
        index: true
    }, //foreign key

    n_comment: String
});

export default mongoose.model("NationR", NationRSchema);
