import Benchmark from 'benchmark';
import * as queriesR from "./queries-r.js"

function benchmarkQuery(...funcs) {
    const suite = new Benchmark.Suite();

    funcs.forEach(func => {
        suite
            .add(func.name, {
                defer: true,
                fn: function (deferred) {
                    func().then(() => {
                        deferred.resolve();
                    });
                }
            })
    })

    suite.on('cycle', function (event) {
        const bench = event.target;

        const funcName = bench.name;
        const opsPerSec = bench.hz;
        const avgSeconds = bench.stats.mean;
        const avgMs = avgSeconds * 1000;

        console.log(`${funcName}:`)
        console.log(`Ops/sec: ${opsPerSec.toFixed(2)}`);
        console.log(`Average time per op: ${avgMs.toFixed(3)} ms`);
        console.log("---------------")
    })
    .on('complete', function () {
        console.log('Fastest is ' + this.filter('fastest').map('name'));
    })
    .run({ async: true });
}

export {
    benchmarkQuery
}

