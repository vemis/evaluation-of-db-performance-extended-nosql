import fs from 'fs';
import seedrandom from 'seedrandom';

export async function readDataFromCustomSeparator(filePath) {
    const data = await fs.promises.readFile(filePath, 'utf8');
    const rows = data.split('\n');
    if (rows.length > 1 && rows[rows.length - 1] === '') rows.pop();
    return rows.map(r => r.split('|'));
}

export function createLineitemsTags(row) {
    return [
        Number(row[0]), Number(row[1]), Number(row[2]), Number(row[3]),
        Number(row[4]), Number(row[5]), Number(row[6]), Number(row[7]),
        row[8], row[9],
        new Date(row[10]), new Date(row[11]), new Date(row[12]),
        row[13], row[14], row[15]
    ];
}

// Seeded Fisher-Yates shuffle + random prefix length — must match Java implementation exactly
export function shuffleAndTruncate(tags, seed) {
    const rng  = seedrandom(seed);
    const list = [...tags];
    for (let i = list.length - 1; i > 0; i--) {
        const j = Math.floor(rng() * (i + 1));
        [list[i], list[j]] = [list[j], list[i]];
    }
    const size = 1 + Math.floor(rng() * list.length);
    return list.slice(0, size);
}

export async function runLoader(sentinelId, collections, loadDataFn, dataLabel, { isAlreadyLoaded, dropCollections, insertSentinel }) {
    const dataPath = process.env.TPCH_DATA_PATH || '/data/tpch-data-small';
    if (await isAlreadyLoaded(sentinelId)) {
        console.log(`${dataLabel} already loaded, skipping.`);
        return `${dataLabel} already loaded, skipping.`;
    }
    await dropCollections(collections);
    await loadDataFn(dataPath);
    await insertSentinel(sentinelId);
    const msg = `${dataLabel} loaded from: ${dataPath}`;
    console.log(msg);
    return msg;
}
