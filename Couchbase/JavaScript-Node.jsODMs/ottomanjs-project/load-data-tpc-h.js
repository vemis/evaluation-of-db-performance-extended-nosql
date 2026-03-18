import fs from 'fs';

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

export {
    readDataFromCustomSeparator,
    insertAll,
    partition
}