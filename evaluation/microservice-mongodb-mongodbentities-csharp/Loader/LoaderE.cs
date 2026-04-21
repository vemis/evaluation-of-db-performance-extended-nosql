using CommonCSharp.Loader;
using MongoDBEntitiesMicroservice.Model.Embedded;
using static CommonCSharp.Loader.TPCHDatasetLoader;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Entities;


namespace MongoDBEntitiesMicroservice.Loader
{
    public static class LoaderE
    {
        private const string SentinelId = "load_e_complete";

        
        
        
        
        
        public static Dictionary<int, List<OrdersE>> MapCustomerOrders(List<OrdersE> orders)
        {
            return orders
                .GroupBy(o => o.o_custkey)
                .ToDictionary(g => g.Key, g => g.ToList());
        }

        public static List<OrdersE> CreateDatasetOrdersE(string filePath)
        {
            List<string[]> dataset = ReadDataFromCustomSeparator(filePath);
            List<OrdersE> entities = new List<OrdersE>(dataset.Count);
            for (int i = 0; i < dataset.Count; i++)
            {
                if (i % 10_000 == 0) Console.WriteLine($"Processed {i} / {dataset.Count}");
                entities.Add(new OrdersE(dataset[i]));
            }
            return entities;
        }

        public static List<LineitemE> CreateLineitemsE(string filePath)
        {
            List<string[]> dataset = ReadDataFromCustomSeparator(filePath);
            List<LineitemE> entities = new List<LineitemE>(dataset.Count);
            for (int i = 0; i < dataset.Count; i++)
            {
                if (i % 10_000 == 0) Console.WriteLine($"Processed {i} / {dataset.Count}");
                entities.Add(new LineitemE(dataset[i]));
            }
            return entities;
        }

        public static async Task LoadOrdersEWithLineitems(string filePathOrders, List<LineitemE> lineitems)
        {
            List<string[]> dataset = ReadDataFromCustomSeparator(filePathOrders);
            Dictionary<int, List<LineitemE>> lineitemsByOrderKey = lineitems
                .GroupBy(l => l.l_orderkey)
                .ToDictionary(g => g.Key, g => g.ToList());

            List<OrdersEWithLineitems> entities = new List<OrdersEWithLineitems>(dataset.Count);
            for (int i = 0; i < dataset.Count; i++)
            {
                if (i % 10_000 == 0) Console.WriteLine($"Processed {i} / {dataset.Count}");
                int orderkey = Convert.ToInt32(dataset[i][0]);
                entities.Add(new OrdersEWithLineitems(dataset[i], lineitemsByOrderKey.GetValueOrDefault(orderkey)));
            }
            await DB.InsertAsync(entities);
        }

        public static async Task LoadOrdersEWithLineitemsArrayAsTagsIndexed(string filePathOrders, string filePathLineitems)
        {
            List<string[]> orders = ReadDataFromCustomSeparator(filePathOrders);
            List<string[]> lineitems = ReadDataFromCustomSeparator(filePathLineitems);
            string[] lineitemsRow2 = lineitems[1];

            List<OrdersEWithLineitemsArrayAsTagsIndexed> entities = new List<OrdersEWithLineitemsArrayAsTagsIndexed>(orders.Count);
            for (int i = 0; i < orders.Count; i++)
            {
                if (i % 10_000 == 0) Console.WriteLine($"Processed {i} / {orders.Count}");
                int orderkey = Convert.ToInt32(orders[i][0]);
                entities.Add(new OrdersEWithLineitemsArrayAsTagsIndexed(
                    orderkey,
                    new Date(DateTime.Parse(orders[i][4])),
                    new List<object>(GetShuffledLineitemsTagsFromRow(lineitemsRow2, orderkey))));
            }
            await DB.InsertAsync(entities);
        }

        public static async Task LoadOrdersEWithLineitemsArrayAsTags(string filePathOrders, string filePathLineitems)
        {
            List<string[]> orders = ReadDataFromCustomSeparator(filePathOrders);
            List<string[]> lineitems = ReadDataFromCustomSeparator(filePathLineitems);
            string[] lineitemsRow2 = lineitems[1];

            List<OrdersEWithLineitemsArrayAsTags> entities = new List<OrdersEWithLineitemsArrayAsTags>(orders.Count);
            for (int i = 0; i < orders.Count; i++)
            {
                if (i % 10_000 == 0) Console.WriteLine($"Processed {i} / {orders.Count}");
                int orderkey = Convert.ToInt32(orders[i][0]);
                entities.Add(new OrdersEWithLineitemsArrayAsTags(
                    orderkey,
                    new Date(DateTime.Parse(orders[i][4])),
                    new List<object>(GetShuffledLineitemsTagsFromRow(lineitemsRow2, orderkey))));
            }
            await DB.InsertAsync(entities);
        }

        public static async Task LoadOrdersEOnlyOCommentIndexed(string filePathOrders)
        {
            List<string[]> orders = ReadDataFromCustomSeparator(filePathOrders);
            List<OrdersEOnlyOCommentIndexed> entities = new List<OrdersEOnlyOCommentIndexed>(orders.Count);
            for (int i = 0; i < orders.Count; i++)
            {
                if (i % 10_000 == 0) Console.WriteLine($"Processed {i} / {orders.Count}");
                entities.Add(new OrdersEOnlyOCommentIndexed(
                    Convert.ToInt32(orders[i][0]),
                    new Date(DateTime.Parse(orders[i][4])),
                    orders[i][8]));
            }
            await DB.InsertAsync(entities);
        }

        public static async Task LoadOrdersEOnlyOComment(string filePathOrders)
        {
            List<string[]> orders = ReadDataFromCustomSeparator(filePathOrders);
            List<OrdersEOnlyOComment> entities = new List<OrdersEOnlyOComment>(orders.Count);
            for (int i = 0; i < orders.Count; i++)
            {
                if (i % 10_000 == 0) Console.WriteLine($"Processed {i} / {orders.Count}");
                entities.Add(new OrdersEOnlyOComment(
                    Convert.ToInt32(orders[i][0]),
                    new Date(DateTime.Parse(orders[i][4])),
                    orders[i][8]));
            }
            await DB.InsertAsync(entities);
        }

        public static async Task LoadOrdersEWithCustomerWithNationWithRegion(
            string filePathOrders, string filePathCustomers,
            string filePathNations, string filePathRegions)
        {
            List<string[]> orders = ReadDataFromCustomSeparator(filePathOrders);

            Dictionary<int, RegionEOnlyName> regionMap = ReadDataFromCustomSeparator(filePathRegions)
                .ToDictionary(row => Convert.ToInt32(row[0]), row => new RegionEOnlyName(Convert.ToInt32(row[0]), row[1]));

            Dictionary<int, NationEOnlyNNameNRegion> nationMap = ReadDataFromCustomSeparator(filePathNations)
                .ToDictionary(row => Convert.ToInt32(row[0]), row =>
                {
                    int key = Convert.ToInt32(row[0]);
                    int regionkey = Convert.ToInt32(row[2]);
                    return new NationEOnlyNNameNRegion(key, row[1], regionkey, regionMap[regionkey]);
                });

            Dictionary<int, CustomerEOnlyCNameCNation> customerMap = ReadDataFromCustomSeparator(filePathCustomers)
                .ToDictionary(row => Convert.ToInt32(row[0]), row =>
                {
                    int key = Convert.ToInt32(row[0]);
                    int nationkey = Convert.ToInt32(row[3]);
                    return new CustomerEOnlyCNameCNation(key, row[1], nationkey, nationMap[nationkey]);
                });

            List<OrdersEWithCustomerWithNationWithRegion> entities = new List<OrdersEWithCustomerWithNationWithRegion>(orders.Count);
            for (int i = 0; i < orders.Count; i++)
            {
                if (i % 10_000 == 0) Console.WriteLine($"Processed {i} / {orders.Count}");
                int orderkey = Convert.ToInt32(orders[i][0]);
                int custkey = Convert.ToInt32(orders[i][1]);
                entities.Add(new OrdersEWithCustomerWithNationWithRegion(
                    orderkey,
                    new Date(DateTime.Parse(orders[i][4])),
                    customerMap.GetValueOrDefault(custkey)));
            }
            await DB.InsertAsync(entities);
        }

        public static async Task LoadDatasetCustomerEWithOrdersAsync(string filePath, List<OrdersE> orders)
        {
            List<string[]> dataset = ReadDataFromCustomSeparator(filePath);
            Dictionary<int, List<OrdersE>> mappingCustomersOrders = MapCustomerOrders(orders);
            List<CustomerEWithOrders> entities = new List<CustomerEWithOrders>(dataset.Count);
            for (int i = 0; i < dataset.Count; i++)
            {
                if (i % 10_000 == 0) Console.WriteLine($"Processed {i} / {dataset.Count}");
                entities.Add(new CustomerEWithOrders(
                    dataset[i],
                    mappingCustomersOrders.GetValueOrDefault(Convert.ToInt32(dataset[i][0]))));
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
            await LoadOrdersEOnlyOCommentIndexed(ordersPath);

            Console.WriteLine("Loading OrdersEOnlyOComment...");
            await LoadOrdersEOnlyOComment(ordersPath);

            Console.WriteLine("Loading OrdersEWithCustomerWithNationWithRegion...");
            await LoadOrdersEWithCustomerWithNationWithRegion(
                ordersPath, customerPath, nationPath, regionPath);

            Console.WriteLine("Loading OrdersEWithLineitemsArrayAsTagsIndexed...");
            await DB.Index<OrdersEWithLineitemsArrayAsTagsIndexed>()
                .Key(c => c.o_lineitems_tags_indexed, KeyType.Ascending)
                .CreateAsync();
            await LoadOrdersEWithLineitemsArrayAsTagsIndexed(ordersPath, lineitemPath);

            Console.WriteLine("Loading OrdersEWithLineitemsArrayAsTags...");
            await LoadOrdersEWithLineitemsArrayAsTags(ordersPath, lineitemPath);

            Console.WriteLine("Loading LineitemsE (in memory for OrdersEWithLineitems)...");
            List<LineitemE> lineitemsE = CreateLineitemsE(lineitemPath);

            Console.WriteLine("Loading OrdersEWithLineitems...");
            await DB.Index<OrdersEWithLineitems>()
                .Key(c => c.o_custkey, KeyType.Ascending)
                .Key(c => c.o_lineitems[0].l_orderkey, KeyType.Ascending)
                .Key(c => c.o_lineitems[0].l_partkey, KeyType.Ascending)
                .CreateAsync();
            await LoadOrdersEWithLineitems(ordersPath, lineitemsE);

            Console.WriteLine("Loading OrdersE (in memory for CustomerEWithOrders)...");
            List<OrdersE> ordersE = CreateDatasetOrdersE(ordersPath);

            Console.WriteLine("Loading CustomerEWithOrders...");
            await DB.Index<CustomerEWithOrders>()
                .Key(c => c.c_nationkey, KeyType.Ascending)
                .CreateAsync();
            await LoadDatasetCustomerEWithOrdersAsync(customerPath, ordersE);

            await meta.InsertOneAsync(new BsonDocument { { "_id", SentinelId } });
            Console.WriteLine("Embedded loading complete.");
            return "loaded";
        }
    }
}
