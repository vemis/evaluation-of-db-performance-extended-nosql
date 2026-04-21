using CommonCSharp.Loader;
using static CommonCSharp.Loader.TPCHDatasetLoader;
using MongoDBEntitiesMicroservice.Model.Relational;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Entities;

namespace MongoDBEntitiesMicroservice.Loader
{
    public static class LoaderR
    {
        private const string SentinelId = "load_r_complete";

        
        
        public static async Task LoadDatasetAsync<T>(string filePath) where T : class, IEntity
        {
            List<string[]> dataset = ReadDataFromCustomSeparator(filePath);
            List<T> entities = new List<T>(dataset.Count);

            for (int i = 0; i < dataset.Count; i++)
            {
                if (i % 10_000 == 0)
                    Console.WriteLine($"Processed {i} / {dataset.Count}");

                entities.Add((T)Activator.CreateInstance(typeof(T), new object[] { dataset[i] })!);
            }

            await DB.InsertAsync(entities);
        }
        
        
        
        public static async Task<string> Run(string dataPath)
        {
            var db = DB.Database(null);
            var meta = db.GetCollection<BsonDocument>("_metadata");

            var sentinel = await meta.Find(Builders<BsonDocument>.Filter.Eq("_id", SentinelId))
                .FirstOrDefaultAsync();
            if (sentinel != null)
            {
                Console.WriteLine("Relational data already loaded, skipping.");
                return "already_loaded";
            }

            Console.WriteLine("Dropping relational collections...");
            await db.DropCollectionAsync("RegionR");
            await db.DropCollectionAsync("NationR");
            await db.DropCollectionAsync("CustomerR");
            await db.DropCollectionAsync("OrdersR");
            await db.DropCollectionAsync("LineitemR");
            await db.DropCollectionAsync("PartR");
            await db.DropCollectionAsync("PartsuppR");
            await db.DropCollectionAsync("SupplierR");

            Console.WriteLine("Loading RegionR...");
            await LoadDatasetAsync<RegionR>(Path.Combine(dataPath, "region.tbl"));

            Console.WriteLine("Loading NationR...");
            await DB.Index<NationR>().Key(c => c.n_regionkey, KeyType.Ascending).CreateAsync();
            await LoadDatasetAsync<NationR>(Path.Combine(dataPath, "nation.tbl"));

            Console.WriteLine("Loading CustomerR...");
            await DB.Index<CustomerR>().Key(c => c.c_nationkey, KeyType.Ascending).CreateAsync();
            await LoadDatasetAsync<CustomerR>(Path.Combine(dataPath, "customer.tbl"));

            Console.WriteLine("Loading OrdersR...");
            await DB.Index<OrdersR>().Key(c => c.o_custkey, KeyType.Ascending).CreateAsync();
            await LoadDatasetAsync<OrdersR>(Path.Combine(dataPath, "orders.tbl"));

            Console.WriteLine("Loading LineitemR...");
            await DB.Index<LineitemR>()
                .Key(c => c.l_orderkey, KeyType.Ascending)
                .Key(c => c.l_partkey, KeyType.Ascending)
                .Key(c => c.l_suppkey, KeyType.Ascending)
                .Key(c => c.l_ps_id, KeyType.Ascending)
                .CreateAsync();
            await LoadDatasetAsync<LineitemR>(Path.Combine(dataPath, "lineitem.tbl"));

            Console.WriteLine("Loading PartR...");
            await LoadDatasetAsync<PartR>(Path.Combine(dataPath, "part.tbl"));

            Console.WriteLine("Loading PartsuppR...");
            await DB.Index<PartsuppR>()
                .Key(c => c.ps_partkey, KeyType.Ascending)
                .Key(c => c.ps_suppkey, KeyType.Ascending)
                .CreateAsync();
            await LoadDatasetAsync<PartsuppR>(Path.Combine(dataPath, "partsupp.tbl"));

            Console.WriteLine("Loading SupplierR...");
            await DB.Index<SupplierR>().Key(c => c.s_nationkey, KeyType.Ascending).CreateAsync();
            await LoadDatasetAsync<SupplierR>(Path.Combine(dataPath, "supplier.tbl"));

            await meta.InsertOneAsync(new BsonDocument { { "_id", SentinelId } });
            Console.WriteLine("Relational loading complete.");
            return "loaded";
        }
    }
}
