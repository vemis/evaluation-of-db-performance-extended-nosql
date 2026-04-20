// Returns the three loader ops bound to a specific MongoDB db handle.
// Callers must pass mongoose.connection.db so the connected singleton is used,
// not a separate mongoose instance that may be installed in a symlinked package.
export function createMongoOps(db) {
    return {
        async isAlreadyLoaded(sentinelId) {
            return (await db.collection('_metadata').findOne({ _id: sentinelId })) !== null;
        },
        async dropCollections(collections) {
            for (const col of collections) {
                try { await db.collection(col).drop(); } catch (_) {}
            }
        },
        async insertSentinel(sentinelId) {
            await db.collection('_metadata').insertOne({ _id: sentinelId });
        }
    };
}
