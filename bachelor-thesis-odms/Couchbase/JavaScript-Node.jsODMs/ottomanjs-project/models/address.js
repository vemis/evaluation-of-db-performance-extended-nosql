import ottoman from 'ottoman';

const AddressSchema = new ottoman.Schema({
    street: String,
    city: String,
});

export default AddressSchema;