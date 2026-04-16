import fs from 'fs';
import seedrandom from 'seedrandom';

async function readDataFromCustomSeparator(filePath){
    const data = await fs.promises.readFile(filePath, 'utf8');
    var rows = data.split('\n');

    // the last one is just empty string
    if (rows.length > 1 && rows[rows.length - 1] == '') {
        rows.pop()
    }
    return rows.map(row => row.split('|'));
}

async function insertAll(batch, model) {
    await Promise.all(
        batch.map(n => model.create(n))
    )
}

function partition(array, batchSize) {
    const result = [];

    for (let i = 0; i < array.length; i += batchSize) {
        result.push(array.slice(i, i + batchSize));
    }

    return result;
}

/**
 * Seeded Fisher-Yates shuffle that also picks a random prefix length (1–16).
 * Using seedrandom so the same o_orderkey always produces the same tags array.
 */
function shuffleArrayItemsAndLength(tags, shuffleSeed) {
    const rng = seedrandom(shuffleSeed);
    const list = [...tags];

    for (let i = list.length - 1; i > 0; i--) {
        const j = Math.floor(rng() * (i + 1));
        [list[i], list[j]] = [list[j], list[i]];
    }

    const size = 1 + Math.floor(rng() * list.length);
    return list.slice(0, size);
}

function getShuffledLineitemsTagsFromRow(lineitemsRow, shuffleSeed) {
    return shuffleArrayItemsAndLength(lineitemsRow, shuffleSeed);
}

export {
    readDataFromCustomSeparator,
    insertAll,
    partition,
    shuffleArrayItemsAndLength,
    getShuffledLineitemsTagsFromRow
}
