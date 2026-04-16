import { Schema, model } from 'ottoman';
import CustomerEOnlyCNameCNationSchema from './customer-e-only-c-name-c-nation.js';

const OrdersEWithCustomerWithNationWithRegionSchema = new Schema({
    // o_orderkey is used as the document id
    id: String,
    o_orderdate: Date,
    o_customer: CustomerEOnlyCNameCNationSchema
});

export const OrdersEWithCustomerWithNationWithRegion = model(
    "OrdersEWithCustomerWithNationWithRegion",
    OrdersEWithCustomerWithNationWithRegionSchema,
    {
        idKey: "id",
        collectionName: 'OrdersEWithCustomerWithNationWithRegion',
        scopeName: 'ottoman_scope_e',
        keyGenerator: ({ metadata }) => "",
        keyGeneratorDelimiter: ""
    });

// No index needed for R5 — the query filters on a nested field without a dedicated index.
OrdersEWithCustomerWithNationWithRegion.createIndexes = async () => {};
