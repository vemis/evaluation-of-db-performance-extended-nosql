using BenchmarkDotNet.Running;
using MongoDB.Entities;
using MongoDBEntities.Benchmarks;
using MongoDBEntities.Models.TPC_H;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

namespace MongoDBEntities
{
    public class ProgramE
    {
        public static async Task Main(String[] args)
        {
            BenchmarkRunner.Run<MongoDBEntitiesBenchmarksE>();

            Console.WriteLine("Benchmark finished");

            throw new Exception();






            CultureInfo.DefaultThreadCurrentCulture = CultureInfo.InvariantCulture;
            CultureInfo.DefaultThreadCurrentUICulture = CultureInfo.InvariantCulture;


            // Connect to MongoDB
            Console.WriteLine("Connecting to the database:");

            await DB.InitAsync("mongodbentities_database_e", "localhost", 27017);

            Console.WriteLine("MongoDB.Entities initialized!");


            var c2 = await QueriesEMongoDBEntities.C2();
            Console.WriteLine(c2[0]);
            Console.WriteLine(c2.Count);


            /*
            await DB.Index<CustomerEWithOrders>()
                .Key(c => c.c_nationkey, KeyType.Ascending)
                .Key(c => c.c_orders[0].o_custkey, KeyType.Ascending)
                .Key(c => c.c_orders[0].o_orderkey, KeyType.Ascending)
                .CreateAsync();

            var orders = TPCHDatasetLoaderE.CreateDatasetOrdersE("..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl");
            Console.WriteLine(orders.Count);
            Console.WriteLine(orders[0]);



            await TPCHDatasetLoaderE.LoadDatasetCustomerEWithOrdersAsync("..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl", orders);
            */

            /*
            OrdersE order1 = new OrdersE
                (
                    1,
                    1,
                    "test",
                    "test",
                    new Date( DateTime.Now),
                    "test",
                    "test",
                    "test",
                    "test"
                );

            CustomerEWithOrders customer1 = new CustomerEWithOrders
                (
                    1,
                    "test",
                    "test",
                    1,
                    "test",
                    1.0,
                    "test",
                    "test",
                    new List<OrdersE> { order1 }
                );

            await DB.InsertAsync( customer1 );
            */
            
        }
    }
}
