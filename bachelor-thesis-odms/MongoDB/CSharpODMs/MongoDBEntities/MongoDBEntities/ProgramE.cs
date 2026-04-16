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
            /*
            BenchmarkRunner.Run<MongoDBEntitiesBenchmarksE>();

            Console.WriteLine("Benchmark finished");

            throw new Exception();
            */



            CultureInfo.DefaultThreadCurrentCulture = CultureInfo.InvariantCulture;
            CultureInfo.DefaultThreadCurrentUICulture = CultureInfo.InvariantCulture;


            // Connect to MongoDB
            Console.WriteLine("Connecting to the database:");

            await DB.InitAsync("mongodbentities_database_e", "localhost", 27017);

            Console.WriteLine("MongoDB.Entities initialized!");


            var res = await QueriesEMongoDBEntities.R9();
            Console.WriteLine(res[0]);
            Console.WriteLine(res.Count);


            /*
            await DB.Index<OrdersEOnlyOCommentIndexed>()
                .Key(c => c.o_comment, KeyType.Text)
                .CreateAsync();
            await TPCHDatasetLoaderE.LoadOrdersEOnlyOCommentIndexed(
                "..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl");
            
            await TPCHDatasetLoaderE.LoadOrdersEOnlyOComment(
                "..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl");
            

            await TPCHDatasetLoaderE.LoadOrdersEWithCustomerWithNationWithRegion(
                "..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl",
                "..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl",
                "..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\nation.tbl",
                "..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\region.tbl");
            
            await DB.Index<OrdersEWithLineitemsArrayAsTagsIndexed>()
                .Key(c => c.o_lineitems_tags_indexed, KeyType.Ascending)
                .CreateAsync();

            await TPCHDatasetLoaderE.LoadOrdersEWithLineitemsArrayAsTagsIndexed(
                "..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl",
                "..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl");
            
            await TPCHDatasetLoaderE.LoadOrdersEWithLineitemsArrayAsTags(
                "..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl",
                "..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl");
            
            await DB.Index<OrdersEWithLineitems>()
                .Key(c => c.o_custkey, KeyType.Ascending)
                .Key(c => c.o_lineitems[0].l_id, KeyType.Ascending)
                .Key(c => c.o_lineitems[0].l_orderkey, KeyType.Ascending)
                .Key(c => c.o_lineitems[0].l_partkey, KeyType.Ascending)
                .Key(c => c.o_lineitems[0].l_suppkey, KeyType.Ascending)
                .Key(c => c.o_lineitems[0].l_ps_id, KeyType.Ascending)
                .CreateAsync();

            List<LineitemE> lineitemsE = TPCHDatasetLoaderE.CreateLineitemsE("..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl");
            await TPCHDatasetLoaderE.LoadOrdersEWithLineitems("..\\..\\..\\..\\..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl", lineitemsE);

            
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
