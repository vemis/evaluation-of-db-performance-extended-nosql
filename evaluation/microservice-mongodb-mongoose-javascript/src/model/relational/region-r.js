import mongoose from 'mongoose';
mongoose.pluralize(null);

const RegionRSchema = new mongoose.Schema({
    _id: Number,
    r_name: String,
    r_comment: String
});

export default mongoose.model('RegionR', RegionRSchema);
