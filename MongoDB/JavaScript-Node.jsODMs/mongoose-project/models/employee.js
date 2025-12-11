import mongoose from "mongoose";
import AddressSchema from "./address.js";

const EmployeeSchema = new mongoose.Schema({
    name: String,
    age: mongoose.Schema.Types.Mixed,
    manager: { type: mongoose.Schema.Types.ObjectId, ref: "Employee" },
    emails: mongoose.Schema.Types.Mixed, //if [String] -> can't be without val
    salary: Number,
    address: AddressSchema,
});

export default mongoose.model("Employee", EmployeeSchema);