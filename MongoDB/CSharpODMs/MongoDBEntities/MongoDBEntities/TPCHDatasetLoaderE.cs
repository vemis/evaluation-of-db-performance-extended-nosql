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
