import mongoose from "mongoose";
import CustomerEOnlyCNameCNationSchema from "./customer-e-only-c-name-c-nation.js";
mongoose.pluralize(null)

const OrdersEWithCustomerWithNationWithRegionSchema = new mongoose.Schema({
    // o_orderkey stored as _id
    _id: Number,
    o_orderdate: Date,
    o_customer: CustomerEOnlyCNameCNationSchema
});

export default mongoose.model("ordersEWithCustomerWithNationWithRegion", OrdersEWithCustomerWithNationWithRegionSchema);
