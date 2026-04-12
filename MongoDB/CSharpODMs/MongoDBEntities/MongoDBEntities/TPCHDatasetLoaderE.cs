using MongoDB.Entities;
using MongoDBEntities.Models.TPC_H;
using System;
using System.Collections.Generic;
using System.Text;

namespace MongoDBEntities
{
    public class TPCHDatasetLoaderE : TPCHDatasetLoader
    {
        public static Dictionary<int, List<OrdersE>> MapCustomerOrders(List<OrdersE> orders)
        {
            return orders
                .GroupBy(o => o.o_custkey)
                .ToDictionary(
                    g => g.Key,
                    g => g.ToList()
                );
        }


        public static List<OrdersE> CreateDatasetOrdersE(string filePath)
        {
            List<string[]> dataset = ReadDataFromCustomSeparator(filePath);

            //dataset = dataset.Take(1_000_000).ToList();

            List<OrdersE> entities = new List<OrdersE>();


            for (int i = 0; i < dataset.Count; i++)
            {
                if (i % 10_000 == 0)
                {
                    Console.WriteLine("Processed " + i + " / " + dataset.Count);
                }


                entities.Add(new OrdersE(dataset[i]));
            }

            return entities;
        }

        public static List<LineitemE> CreateLineitemsE(string filePath)
        {
            List<string[]> dataset = ReadDataFromCustomSeparator(filePath);

            List<LineitemE> entities = new List<LineitemE>();

            for (int i = 0; i < dataset.Count; i++)
            {
                if (i % 10_000 == 0)
                {
                    Console.WriteLine("Processed " + i + " / " + dataset.Count);
                }

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

            List<OrdersEWithLineitems> entities = new List<OrdersEWithLineitems>();

            for (int i = 0; i < dataset.Count; i++)
            {
                if (i % 10_000 == 0)
                {
                    Console.WriteLine("Processed " + i + " / " + dataset.Count);
                }

                int orderkey = Convert.ToInt32(dataset[i][0]);
                entities.Add(new OrdersEWithLineitems(
                    dataset[i],
                    lineitemsByOrderKey.GetValueOrDefault(orderkey, null)
                ));
            }

            await DB.InsertAsync(entities);
        }

        public static async Task LoadOrdersEWithLineitemsArrayAsTagsIndexed(string filePathOrders, string filePathLineitems)
        {
            List<string[]> orders = ReadDataFromCustomSeparator(filePathOrders);
            List<string[]> lineitems = ReadDataFromCustomSeparator(filePathLineitems);

            string[] lineitemsRow2 = lineitems[1]; // 2nd row — unique elements (same as Java lineitems.get(1))

            List<OrdersEWithLineitemsArrayAsTagsIndexed> entities = new List<OrdersEWithLineitemsArrayAsTagsIndexed>();

            for (int i = 0; i < orders.Count; i++)
            {
                if (i % 10_000 == 0)
                {
                    Console.WriteLine("Processed " + i + " / " + orders.Count);
                }

                int orderkey = Convert.ToInt32(orders[i][0]);
                entities.Add(new OrdersEWithLineitemsArrayAsTagsIndexed(
                    orderkey,
                    new Date(DateTime.Parse(orders[i][4])),
                    new List<object>(GetShuffledLineitemsTagsFromRow(lineitemsRow2, orderkey))
                ));
            }

            await DB.InsertAsync(entities);
        }

        public static async Task LoadOrdersEWithLineitemsArrayAsTags(string filePathOrders, string filePathLineitems)
        {
            List<string[]> orders = ReadDataFromCustomSeparator(filePathOrders);
            List<string[]> lineitems = ReadDataFromCustomSeparator(filePathLineitems);

            string[] lineitemsRow2 = lineitems[1]; // 2nd row — unique elements (same as Java lineitems.get(1))

            List<OrdersEWithLineitemsArrayAsTags> entities = new List<OrdersEWithLineitemsArrayAsTags>();

            for (int i = 0; i < orders.Count; i++)
            {
                if (i % 10_000 == 0)
                {
                    Console.WriteLine("Processed " + i + " / " + orders.Count);
                }

                int orderkey = Convert.ToInt32(orders[i][0]);
                entities.Add(new OrdersEWithLineitemsArrayAsTags(
                    orderkey,
                    new Date(DateTime.Parse(orders[i][4])),
                    new List<object>(GetShuffledLineitemsTagsFromRow(lineitemsRow2, orderkey))
                ));
            }

            await DB.InsertAsync(entities);
        }

        public static async Task LoadOrdersEWithCustomerWithNationWithRegion(
            string filePathOrders,
            string filePathCustomers,
            string filePathNations,
            string filePathRegions)
        {
            List<string[]> orders = ReadDataFromCustomSeparator(filePathOrders);

            // Build RegionEOnlyName map keyed by r_regionkey
            List<string[]> regionRows = ReadDataFromCustomSeparator(filePathRegions);
            Dictionary<int, RegionEOnlyName> regionMap = new Dictionary<int, RegionEOnlyName>();
            foreach (string[] row in regionRows)
            {
                int key = Convert.ToInt32(row[0]);
                regionMap[key] = new RegionEOnlyName(key, row[1]);
            }

            // Build NationEOnlyNNameNRegion map keyed by n_nationkey
            List<string[]> nationRows = ReadDataFromCustomSeparator(filePathNations);
            Dictionary<int, NationEOnlyNNameNRegion> nationMap = new Dictionary<int, NationEOnlyNNameNRegion>();
            foreach (string[] row in nationRows)
            {
                int key = Convert.ToInt32(row[0]);
                int regionkey = Convert.ToInt32(row[2]);
                nationMap[key] = new NationEOnlyNNameNRegion(key, row[1], regionkey, regionMap[regionkey]);
            }

            // Build CustomerEOnlyCNameCNation map keyed by c_custkey
            List<string[]> customerRows = ReadDataFromCustomSeparator(filePathCustomers);
            Dictionary<int, CustomerEOnlyCNameCNation> customerMap = new Dictionary<int, CustomerEOnlyCNameCNation>();
            foreach (string[] row in customerRows)
            {
                int key = Convert.ToInt32(row[0]);
                int nationkey = Convert.ToInt32(row[3]);
                customerMap[key] = new CustomerEOnlyCNameCNation(key, row[1], nationkey, nationMap[nationkey]);
            }

            List<OrdersEWithCustomerWithNationWithRegion> entities = new List<OrdersEWithCustomerWithNationWithRegion>();

            for (int i = 0; i < orders.Count; i++)
            {
                if (i % 10_000 == 0)
                {
                    Console.WriteLine("Processed " + i + " / " + orders.Count);
                }

                int orderkey = Convert.ToInt32(orders[i][0]);
                int custkey = Convert.ToInt32(orders[i][1]);
                entities.Add(new OrdersEWithCustomerWithNationWithRegion(
                    orderkey,
                    new Date(DateTime.Parse(orders[i][4])),
                    customerMap.GetValueOrDefault(custkey, null)
                ));
            }

            await DB.InsertAsync(entities);
        }

        public static async Task LoadDatasetCustomerEWithOrdersAsync(string filePath, List<OrdersE> orders)
        {
            List<string[]> dataset = ReadDataFromCustomSeparator(filePath);

            //dataset = dataset.Take(1_000_000).ToList();

            List<CustomerEWithOrders> entities = new List<CustomerEWithOrders>();

            Dictionary<int, List<OrdersE>> mappingCustomersÓrders = MapCustomerOrders(orders);

            for (int i = 0; i < dataset.Count; i++)
            {
                if (i % 10_000 == 0)
                {
                    Console.WriteLine("Processed " + i + " / " + dataset.Count);
                }


                entities.Add(
                    new CustomerEWithOrders(
                        dataset[i],
                        mappingCustomersÓrders.GetValueOrDefault(Convert.ToInt32(dataset[i][0]), null)
                    )
                );
                /*
                entities.Add
                    (
                        //new T(dataset[i])//maybe all entities are the same? check it!!!
                    );
                */
            }

            //Console.WriteLine($"entities.Count:{entities.Count}");

            //entities.ForEach(x => Console.WriteLine(x));

            await DB.InsertAsync(entities);
        }
    }
}
