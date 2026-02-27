using MongoDB.Entities;
using MongoDBEntities.Models.TPC_H;
using System;
using System.Collections.Generic;
using System.Formats.Asn1;
using System.IO.Pipelines;
using System.Net.ServerSentEvents;
using System.Text;

namespace MongoDBEntities
{
    public class TPCHDatasetLoader
    {
        public static List<string[]> ReadDataFromCustomSeparator(string filePath)
        {
            try
            {
                var rows = new List<string[]>();

                using (var reader = new StreamReader(filePath))
                {
                    while (!reader.EndOfStream)
                    {
                        var line = reader.ReadLine();
                        var values = line.Split('|');

                        rows.Add(values);   // Store row
                    }
                }

                return rows;
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
            }

            return null;
        }

        public static async Task LoadDatasetAsync<T>(string filePath) where T : class, IEntity
        {
            List<string[]> dataset = ReadDataFromCustomSeparator(filePath);

            //dataset = dataset.Take(1_000_000).ToList();

            List<T> entities = new List<T>();


            for (int i = 0; i < dataset.Count; i++)
            {
                if (i % 10_000 == 0)
                {
                    Console.WriteLine("Processed " + i + " / " + dataset.Count);
                }


                entities.Add((T)Activator.CreateInstance(
                    typeof(T),
                    new object[] { dataset[i] }
                )!);
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
