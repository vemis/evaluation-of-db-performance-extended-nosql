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
    public class MongoDBEntitiesBenchmarksE
    {
        [Benchmark]
        public async Task<List<BsonDocument>> C2()
        {
            /*
            | Method | Mean    | Error    | StdDev   |
            |------- |--------:|---------:|---------:|
            | C2     | 2.407 s | 0.0707 s | 0.0627 s |
            */
            var c2 = await QueriesEMongoDBEntities.C2();
            return c2;
        }

        [GlobalSetup]
        public async Task SetupAsync()
        {
            CultureInfo.DefaultThreadCurrentCulture = CultureInfo.InvariantCulture;
            CultureInfo.DefaultThreadCurrentUICulture = CultureInfo.InvariantCulture;

            await DB.InitAsync("mongodbentities_database_e", "localhost", 27017);
        }
    }
}
