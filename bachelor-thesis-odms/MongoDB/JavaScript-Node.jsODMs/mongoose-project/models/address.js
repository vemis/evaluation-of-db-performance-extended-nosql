import mongoose from "mongoose";

const AddressSchema = new mongoose.Schema( {
    street: String,
    city: String,
});

// embedded schema
export default AddressSchema;