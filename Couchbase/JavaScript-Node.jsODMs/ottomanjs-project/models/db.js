import { connect, start, Schema, model } from 'ottoman';

await connect({
  connectionString: 'couchbase://127.0.0.1',
  bucketName: 'ottoman_bucket',
  scopeName: 'myScope',
  username: 'Administrator',
  password: 'password',
});

console.log("Connected to Couchbase!");

// Initialize Ottoman
await start();
console.log("Ottoman initialized!");