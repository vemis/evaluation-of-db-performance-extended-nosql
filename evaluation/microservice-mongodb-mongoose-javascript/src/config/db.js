import mongoose from 'mongoose';

const MONGODB_URI      = process.env.MONGODB_URI      || 'mongodb://localhost:27017';
const MONGODB_DATABASE = process.env.MONGODB_DATABASE || 'mongoose_tpch';

export async function connectDb() {
    await mongoose.connect(`${MONGODB_URI}/${MONGODB_DATABASE}`);
    console.log(`Connected to MongoDB: ${MONGODB_URI}/${MONGODB_DATABASE}`);
}
