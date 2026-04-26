import { Schema, model } from 'ottoman';
import CustomerEOnlyCNameCNationSchema from './customer-e-only-c-name-c-nation.js';

const OrdersEWithCustomerWithNationWithRegionSchema = new Schema({
    id: String,
    o_orderdate: String,
    o_customer: CustomerEOnlyCNameCNationSchema
});

export const OrdersEWithCustomerWithNationWithRegion = model('OrdersEWithCustomerWithNationWithRegion', OrdersEWithCustomerWithNationWithRegionSchema, {
    idKey: 'id',
    collectionName: 'OrdersEWithCustomerWithNationWithRegion',
    scopeName: 'ottoman_scope_e',
    keyGenerator: ({ metadata }) => '',
    keyGeneratorDelimiter: ''
});

OrdersEWithCustomerWithNationWithRegion.createIndexes = async () => {};
