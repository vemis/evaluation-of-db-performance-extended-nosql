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
    [WarmupCount(2)]     // number of warm-up iterations
    [IterationCount(2)] // number of measurement iterations
    //[MaxIterationCount(16)]
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

        //[Benchmark]
        public async Task<List<CustomerR>> A3()
        {
            /*
             Mean = 1.178 s, StdErr = 0.653 s (55.46%), N = 2, StdDev = 0.924 s
             */

            var a3 = await QueriesRMongoDBEntities.A3();
            return a3;
        }

        //[Benchmark]
        public async Task<List<OrdersR>> A4()
        {
            /*
             Mean = 144.466 ms, StdErr = 7.582 ms (5.25%), N = 2, StdDev = 10.722 ms
             */

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

        //[Benchmark]
        public async Task<List<BsonDocument>> B2()
        {
            /*
             Mean = 1.979 s, StdErr = 0.130 s (6.56%), N = 2, StdDev = 0.184 s
             */
            var b2 = await QueriesRMongoDBEntities.B2();
            return b2;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> C1()
        {
            /*
             Mean = 13.873 s, StdErr = 0.120 s (0.86%), N = 2, StdDev = 0.170 s
             */
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
            /*
             Mean = 17.177 s, StdErr = 0.279 s (1.62%), N = 2, StdDev = 0.395 s
             */
            var c3 = await QueriesRMongoDBEntities.C3();
            return c3;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> C4()
        {
            /*
             Mean = 10.737 s, StdErr = 0.123 s (1.14%), N = 2, StdDev = 0.174 s
             */
            var c4 = await QueriesRMongoDBEntities.C4();
            return c4;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> C5()
        {
            /*
             Mean = 6.114 s, StdErr = 0.309 s (5.05%), N = 2, StdDev = 0.436 s
             */
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
            /*
             Mean = 278.013 ms, StdErr = 0.042 ms (0.01%), N = 2, StdDev = 0.059 ms
             */
            var d2 = await QueriesRMongoDBEntities.D2();
            return d2;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> D3()
        {
            /*
             Mean = 306.589 ms, StdErr = 0.819 ms (0.27%), N = 2, StdDev = 1.158 ms
             */
            var d3 = await QueriesRMongoDBEntities.D3();
            return d3;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> E1()
        {
            /*
             Mean = 200.853 ms, StdErr = 34.035 ms (16.95%), N = 2, StdDev = 48.132 ms
             */
            var e1 = await QueriesRMongoDBEntities.E1();
            return e1;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> E2()
        {
            /*
             Mean = 1.777 s, StdErr = 0.037 s (2.10%), N = 2, StdDev = 0.053 s
             */
            var e2 = await QueriesRMongoDBEntities.E2();
            return e2;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> E3()
        {
            /*
             Mean = 21.847 ms, StdErr = 3.269 ms (14.96%), N = 2, StdDev = 4.623 ms
             */
            var e3 = await QueriesRMongoDBEntities.E3();
            return e3;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> Q1()
        {
            /*
             Mean = 2.310 s, StdErr = 0.002 s (0.08%), N = 2, StdDev = 0.003 s
             */
            var q1 = await QueriesRMongoDBEntities.Q1();
            return q1;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> Q2()
        {
            /*
             Mean = 170.968 ms, StdErr = 7.307 ms (4.27%), N = 2, StdDev = 10.334 ms
             */
            var q2 = await QueriesRMongoDBEntities.Q2();
            return q2;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> Q3()
        {
            /*
             Mean = 4.246 s, StdErr = 0.008 s (0.19%), N = 2, StdDev = 0.011 s
             */
            var q3 = await QueriesRMongoDBEntities.Q3();
            return q3;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> Q4()
        {
            /*
             Mean = 495.066 ms, StdErr = 1.681 ms (0.34%), N = 2, StdDev = 2.377 ms
             */
            var q4 = await QueriesRMongoDBEntities.Q4();
            return q4;
        }

        [Benchmark]
        public async Task<List<BsonDocument>> Q5()
        {
            /*
             Mean = 19.559 s, StdErr = 0.026 s (0.13%), N = 2, StdDev = 0.037 s
             */
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
