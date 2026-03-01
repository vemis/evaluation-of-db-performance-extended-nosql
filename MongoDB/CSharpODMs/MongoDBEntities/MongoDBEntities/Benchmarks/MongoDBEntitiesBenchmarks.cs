using BenchmarkDotNet.Attributes;
using BenchmarkDotNet.Running;
using MongoDB.Bson;
using MongoDB.Entities;
using MongoDBEntities.Models.TPC_H;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

namespace MongoDBEntities.Benchmarks
{
    [MaxIterationCount(16)]
    public class MongoDBEntitiesBenchmarks
    {
        //[Benchmark]
        public async Task<List<LineitemR>> A1() 
        {
            //WorkloadActual  36: 1 op, 14688927500.00 ns, 14.6889 s/op
            var a1 = await QueriesRMongoDBEntities.A1Async();
            return a1;
        }

        //[Benchmark]
        public async Task<List<OrdersR>> A2()
        {
            /*
            | Method | Mean     | Error    | StdDev   | Median   |
            |------- |---------:|---------:|---------:|---------:|
            | A2     | 646.2 ms | 33.04 ms | 93.74 ms | 619.4 ms |
             */

            var a2 = await QueriesRMongoDBEntities.A2();
            return a2;
        }

        //[Benchmark]
        public async Task<List<BsonDocument>> B1() 
        {
            /*| Method | Mean    | Error    | StdDev   |
            |------- |--------:|---------:|---------:|
            | B1     | 1.022 s | 0.0268 s | 0.0781 s |
            */
            var b1 = await QueriesRMongoDBEntities.B1();
            return b1;
        }

        //[Benchmark]
        public async Task<List<BsonDocument>> C2()
        {
            /*
            | Method | Mean    | Error    | StdDev   |
            |------- |--------:|---------:|---------:|
            | C2     | 6.862 s | 0.1884 s | 0.1573 s |
            */
            var c2 = await QueriesRMongoDBEntities.C2();
            return c2;
        }

        [GlobalSetup]
        public async Task SetupAsync()
        {
            CultureInfo.DefaultThreadCurrentCulture = CultureInfo.InvariantCulture;
            CultureInfo.DefaultThreadCurrentUICulture = CultureInfo.InvariantCulture;

            await DB.InitAsync("mongodbentities_database_r", "localhost", 27017);
        }
    }
}
