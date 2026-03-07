using System;
using System.Collections.Generic;
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
    }
}
