import { connect, start, Schema, model } from 'ottoman';

await connect({
    connectionString: 'couchbase://127.0.0.1',
    bucketName: 'ottoman_bucket_r',
    scopeName: 'ottoman_scope_r',
    username: 'Administrator',
    password: 'password',
    timeouts: {
        kvTimeout: 20_000,
        queryTimeout: 300_000
    }
});

console.log("Connected to Couchbase!");

// Initialize Ottoman
await start();
console.log("Ottoman initialized!");