import { Schema, model } from 'ottoman';
import AddressSchema from './address.js';

const EmployeeSchema = new Schema({
    name: String,
    age: Schema.Types.Mixed,
    manager: { type: Schema.Types.Reference, ref: 'Employee' },
    emails: [Schema.Types.Mixed],
    salary: Number,
    address: AddressSchema,
});

//export const Employee = model('Employee', EmployeeSchema);
export const Employee = model('Employee', EmployeeSchema, {
  collectionName: 'Employee'
});
