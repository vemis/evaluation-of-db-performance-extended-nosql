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
    public class MongoDBEntitiesBenchmarksR
    {
        //[Benchmark]
        public async Task<List<LineitemR>> A1() 
        {
            //WorkloadActual  36: 1 op, 14688927500.00 ns, 14.6889 s/op
            var a1 = await QueriesRMongoDBEntities.A1();
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

        [Benchmark]
        public async Task<List<CustomerR>> A3()
        {

            var a3 = await QueriesRMongoDBEntities.A3();
            return a3;
        }

        [Benchmark]
        public async Task<List<OrdersR>> A4()
        {

            var a4 = await QueriesRMongoDBEntities.A4();
            return a4;
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

        [Benchmark]
        public async Task<List<BsonDocument>> B2()
        {
            var b2 = await QueriesRMongoDBEntities.B2();
            return b2;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> C1()
        {
            var c1 = await QueriesRMongoDBEntities.C1();
            return c1;
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

        [Benchmark]
        public async Task<List<BsonDocument>> C3()
        {
            var c3 = await QueriesRMongoDBEntities.C3();
            return c3;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> C4()
        {
            var c4 = await QueriesRMongoDBEntities.C4();
            return c4;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> C5()
        {
            var c5 = await QueriesRMongoDBEntities.C5();
            return c5;
        }

        //[Benchmark]
        public async Task<List<BsonDocument>> D1()
        {
            /*
            | Method | Mean     | Error    | StdDev   |
            |------- |---------:|---------:|---------:|
            | D1     | 15.10 ms | 1.530 ms | 1.431 ms |
            */
            var d1 = await QueriesRMongoDBEntities.D1();
            return d1;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> D2()
        {
            var d2 = await QueriesRMongoDBEntities.D2();
            return d2;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> D3()
        {
            var d3 = await QueriesRMongoDBEntities.D3();
            return d3;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> E1()
        {
            var e1 = await QueriesRMongoDBEntities.E1();
            return e1;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> E2()
        {
            var e2 = await QueriesRMongoDBEntities.E2();
            return e2;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> E3()
        {
            var e3 = await QueriesRMongoDBEntities.E3();
            return e3;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> Q1()
        {
            var q1 = await QueriesRMongoDBEntities.Q1();
            return q1;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> Q2()
        {
            var q2 = await QueriesRMongoDBEntities.Q2();
            return q2;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> Q3()
        {
            var q3 = await QueriesRMongoDBEntities.Q3();
            return q3;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> Q4()
        {
            var q4 = await QueriesRMongoDBEntities.Q4();
            return q4;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> Q5()
        {
            var q5 = await QueriesRMongoDBEntities.Q5();
            return q5;
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
