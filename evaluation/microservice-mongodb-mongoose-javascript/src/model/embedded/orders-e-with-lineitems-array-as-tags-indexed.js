import mongoose from 'mongoose';
mongoose.pluralize(null);

const OrdersEWithLineitemsArrayAsTagsIndexedSchema = new mongoose.Schema({
    _id: Number,
    o_orderdate: Date,
    o_lineitems_tags_indexed: {
        type: [mongoose.Schema.Types.Mixed],
        index: true
    }
});

export default mongoose.model('ordersEWithLineitemsArrayAsTagsIndexed', OrdersEWithLineitemsArrayAsTagsIndexedSchema);
