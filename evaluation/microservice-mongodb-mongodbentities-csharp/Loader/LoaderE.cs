using CommonCSharp.Loader;
using CommonCSharp.Models.TPC_H;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Entities;

namespace MongoDBEntitiesMicroservice.Loader
{
    public static class LoaderE
    {
        private const string SentinelId = "load_e_complete";

        public static async Task<string> Run(string dataPath)
        {
            var db = DB.Database(null);
            var meta = db.GetCollection<BsonDocument>("_metadata");

            var sentinel = await meta.Find(Builders<BsonDocument>.Filter.Eq("_id", SentinelId))
                .FirstOrDefaultAsync();
            if (sentinel != null)
            {
                Console.WriteLine("Embedded data already loaded, skipping.");
                return "already_loaded";
            }

            Console.WriteLine("Dropping embedded collections...");
            await db.DropCollectionAsync("OrdersEOnlyOCommentIndexed");
            await db.DropCollectionAsync("OrdersEOnlyOComment");
            await db.DropCollectionAsync("OrdersEWithCustomerWithNationWithRegion");
            await db.DropCollectionAsync("OrdersEWithLineitemsArrayAsTagsIndexed");
            await db.DropCollectionAsync("OrdersEWithLineitemsArrayAsTags");
            await db.DropCollectionAsync("OrdersEWithLineitems");
            await db.DropCollectionAsync("CustomerEWithOrders");

            string ordersPath = Path.Combine(dataPath, "orders.tbl");
            string lineitemPath = Path.Combine(dataPath, "lineitem.tbl");
            string customerPath = Path.Combine(dataPath, "customer.tbl");
            string nationPath = Path.Combine(dataPath, "nation.tbl");
            string regionPath = Path.Combine(dataPath, "region.tbl");

            Console.WriteLine("Loading OrdersEOnlyOCommentIndexed...");
            await DB.Index<OrdersEOnlyOCommentIndexed>()
                .Key(c => c.o_comment, KeyType.Text)
                .CreateAsync();
            await TPCHDatasetLoaderE.LoadOrdersEOnlyOCommentIndexed(ordersPath);

            Console.WriteLine("Loading OrdersEOnlyOComment...");
            await TPCHDatasetLoaderE.LoadOrdersEOnlyOComment(ordersPath);

            Console.WriteLine("Loading OrdersEWithCustomerWithNationWithRegion...");
            await TPCHDatasetLoaderE.LoadOrdersEWithCustomerWithNationWithRegion(
                ordersPath, customerPath, nationPath, regionPath);

            Console.WriteLine("Loading OrdersEWithLineitemsArrayAsTagsIndexed...");
            await DB.Index<OrdersEWithLineitemsArrayAsTagsIndexed>()
                .Key(c => c.o_lineitems_tags_indexed, KeyType.Ascending)
                .CreateAsync();
            await TPCHDatasetLoaderE.LoadOrdersEWithLineitemsArrayAsTagsIndexed(ordersPath, lineitemPath);

            Console.WriteLine("Loading OrdersEWithLineitemsArrayAsTags...");
            await TPCHDatasetLoaderE.LoadOrdersEWithLineitemsArrayAsTags(ordersPath, lineitemPath);

            Console.WriteLine("Loading LineitemsE (in memory for OrdersEWithLineitems)...");
            List<LineitemE> lineitemsE = TPCHDatasetLoaderE.CreateLineitemsE(lineitemPath);

            Console.WriteLine("Loading OrdersEWithLineitems...");
            await DB.Index<OrdersEWithLineitems>()
                .Key(c => c.o_custkey, KeyType.Ascending)
                .Key(c => c.o_lineitems[0].l_orderkey, KeyType.Ascending)
                .Key(c => c.o_lineitems[0].l_partkey, KeyType.Ascending)
                .CreateAsync();
            await TPCHDatasetLoaderE.LoadOrdersEWithLineitems(ordersPath, lineitemsE);

            Console.WriteLine("Loading OrdersE (in memory for CustomerEWithOrders)...");
            List<OrdersE> ordersE = TPCHDatasetLoaderE.CreateDatasetOrdersE(ordersPath);

            Console.WriteLine("Loading CustomerEWithOrders...");
            await DB.Index<CustomerEWithOrders>()
                .Key(c => c.c_nationkey, KeyType.Ascending)
                .CreateAsync();
            await TPCHDatasetLoaderE.LoadDatasetCustomerEWithOrdersAsync(customerPath, ordersE);

            await meta.InsertOneAsync(new BsonDocument { { "_id", SentinelId } });
            Console.WriteLine("Embedded loading complete.");
            return "loaded";
        }
    }
}
