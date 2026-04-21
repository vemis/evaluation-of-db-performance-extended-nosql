import { connect } from 'ottoman';

const COUCHBASE_HOST     = process.env.COUCHBASE_HOST     || 'localhost';
const COUCHBASE_USER     = process.env.COUCHBASE_USER     || 'Administrator';
const COUCHBASE_PASSWORD = process.env.COUCHBASE_PASSWORD || 'password';

export async function connectDb() {
    await connect({
        connectionString: `couchbase://${COUCHBASE_HOST}`,
        bucketName: 'bucket-main',
        username: COUCHBASE_USER,
        password: COUCHBASE_PASSWORD,
        timeouts: {
            kvTimeout:    20_000,
            queryTimeout: 300_000
        }
    });
    console.log(`Connected to Couchbase: ${COUCHBASE_HOST}/bucket-main`);
}
