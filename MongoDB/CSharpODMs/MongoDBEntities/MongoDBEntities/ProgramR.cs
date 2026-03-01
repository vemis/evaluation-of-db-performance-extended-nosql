using BenchmarkDotNet.Running;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;
using MongoDBEntities.Benchmarks;
using MongoDBEntities.Models.TPC_H;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

namespace MongoDBEntities
{
    class Book : IEntity
    {
        [BsonId]
        public string Title { get; set; }

        public object GenerateNewID()
        {
            throw new NotImplementedException();
        }

        public bool HasDefaultID()
        {
            return false;
        }

    }

    class ProgramR
    {
        // MainTesting Method
        public static async Task Main(String[] args)
        {

            
            BenchmarkRunner.Run<MongoDBEntitiesBenchmarks>();

            Console.WriteLine("Benchmark finished");

            throw new Exception();



            CultureInfo.DefaultThreadCurrentCulture = CultureInfo.InvariantCulture;
            CultureInfo.DefaultThreadCurrentUICulture = CultureInfo.InvariantCulture;

            
            // Connect to MongoDB
            Console.WriteLine("Connecting to the database:");

            await DB.InitAsync("mongodbentities_database_r", "localhost", 27017);

            Console.WriteLine("MongoDB.Entities initialized!");

            /*var lineitems = new[]
            {
                new LineitemR{ l_id = "1" },
                new LineitemR{ l_id = "2" },
                new LineitemR{ l_id = "3" }
            };

            await DB.SaveAsync(lineitems);*/

            /*await DB.Index<TestR>()
                .Key(b => b.r_indexed, KeyType.Ascending)
                .CreateAsync();
            */

            //var a2 = await QueriesRMongoDBEntities.A2();
            //Console.WriteLine(a2.Count);

            //var b1 = await QueriesRMongoDBEntities.B1();
            //Console.WriteLine(b1.Count);
            //b1.ForEach(x => Console.WriteLine(x));

            //var c2 = await QueriesRMongoDBEntities.C2();
            //Console.WriteLine(c2.Count);

            var d1 = await QueriesRMongoDBEntities.D1();
            Console.WriteLine(d1.Count);
            Console.WriteLine(d1[0]);


            /*
            await TPCHDatasetLoader.LoadDatasetAsync<PartR>("..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\part.tbl");
            Console.WriteLine("Parts loaded!");
            
            await DB.Index<SupplierR>()
                .Key(c => c.s_nationkey, KeyType.Ascending)
                .CreateAsync();
            await TPCHDatasetLoader.LoadDatasetAsync<SupplierR>("..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\supplier.tbl");
            Console.WriteLine("Suppliers loaded!");

            
            await DB.Index<PartsuppR>()
                .Key(c => c.ps_partKey, KeyType.Ascending)
                .Key(c => c.ps_suppKey, KeyType.Ascending)
                .CreateAsync();
            await TPCHDatasetLoader.LoadDatasetAsync<PartsuppR>("..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\partsupp.tbl");
            Console.WriteLine("Partsupps loaded!");

            
            await DB.Index<OrdersR>()
                .Key(c => c.o_custkey, KeyType.Ascending)
                .CreateAsync();
            await TPCHDatasetLoader.LoadDatasetAsync<OrdersR>("..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl");
            Console.WriteLine("Orders loaded!");


            await DB.Index<CustomerR>()
                .Key(c => c.c_nationkey, KeyType.Ascending)
                .CreateAsync();
            await TPCHDatasetLoader.LoadDatasetAsync<CustomerR>("..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl");
            Console.WriteLine("Customers loaded!");

            
            // Nation Indexes
            await DB.Index<NationR>()
                .Key(c => c.n_regionkey, KeyType.Ascending)
                .CreateAsync();

            await TPCHDatasetLoader.LoadDatasetAsync<NationR>("..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\nation.tbl");
            Console.WriteLine("Nations loaded!");


            await TPCHDatasetLoader.LoadDatasetAsync<RegionR>("..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\region.tbl");
            Console.WriteLine("Regions loaded!");

  
            // LineitemsR indexes
            await DB.Index<LineitemR>()
                .Key(c => c.l_orderkey, KeyType.Ascending)
                .Key(c => c.l_partkey, KeyType.Ascending)
                .Key(c => c.l_suppkey, KeyType.Ascending)
                .Key(c => c.l_ps_id, KeyType.Ascending)
                .CreateAsync();

            
            await TPCHDatasetLoader.LoadDatasetAsync<LineitemR>("..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl");
            Console.WriteLine("Lineitems loaded!");
            */
        }
    }
}
