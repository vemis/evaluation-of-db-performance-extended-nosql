import * as benchmarksR from "./benchmarks/benchmarks-r.js";

async function t1() {
    let sum = 0
    for (let i = 0; i < 100; i++) {
        sum += 1
    }

    return sum
}
async function t2() {
    let sum = 0
    for (let i = 0; i < 1000; i++) {
        sum += 1
    }
    const t = [1,2,3]
    return sum
}

//benchmarksR.benchmarkQuery(t1, t2)

const a = [1,2,3]
const b = a.map(x => ({c:2}))
console.log(b)

