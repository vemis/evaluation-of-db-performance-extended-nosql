using CommonCSharp.Models.TPC_H;
using MongoDB.Entities;

namespace CommonCSharp.Loader
{
    public class TPCHDatasetLoaderR : TPCHDatasetLoader
    {
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
    }
}
