export async function insertBatched(Model, docs, batchSize = 2_500) {
    for (let i = 0; i < docs.length; i += batchSize) {
        await Promise.all(docs.slice(i, i + batchSize).map(doc => Model.create(doc)));
        console.log(`  batch ${Math.floor(i / batchSize) + 1}/${Math.ceil(docs.length / batchSize)}`);
    }
}

export async function upsertBatch(collection, docs) {
    await Promise.all(docs.map(doc => collection.upsert(String(doc.id), doc)));

}

export async function upsertAll(collection, docs, batchSize = 2_500) {
    for (let i = 0; i < docs.length; i += batchSize) {
        await upsertBatch(collection, docs.slice(i, i + batchSize));
        console.log(`  batch ${Math.floor(i / batchSize) + 1}/${Math.ceil(docs.length / batchSize)}`);
    }
}

export async function ensureCouchbaseScopeAndCollections(cluster, bucket, scope, collections) {
    const cm = cluster.bucket(bucket).collections();
    try { await cm.createScope(scope); } catch (_) {}
    for (const col of collections) {
        try { await cm.createCollection({ name: col, scopeName: scope }); } catch (_) {}
    }
    await new Promise(r => setTimeout(r, 2_000));

    for (const col of collections) {
        for (let attempt = 0; attempt < 10; attempt++) {
            try {
                await cluster.query(
                    `CREATE PRIMARY INDEX IF NOT EXISTS ON \`${bucket}\`.\`${scope}\`.\`${col}\``
                );
                break;
            } catch (_) {
                await new Promise(r => setTimeout(r, 2_000));
            }
        }
    }
}

// Returns the three loader ops bound to a specific Couchbase cluster/bucket/scope.
export function createCouchbaseOps(cluster, bucket, scope) {
    const defaultColl = cluster.bucket(bucket).defaultCollection();
    return {
        async isAlreadyLoaded(sentinelId) {
            try { await defaultColl.get(sentinelId); return true; } catch (_) { return false; }
        },
        async dropCollections(collections) {
            for (const col of collections) {
                try {
                    await cluster.query(`DELETE FROM \`${bucket}\`.\`${scope}\`.\`${col}\``);
                } catch (e) {
                    console.warn(`Drop warning for ${col}: ${e.message}`);
                }
            }
        },
        async insertSentinel(sentinelId) {
            await defaultColl.upsert(sentinelId, { type: 'sentinel', created: new Date().toISOString() });
        }
    };
}
