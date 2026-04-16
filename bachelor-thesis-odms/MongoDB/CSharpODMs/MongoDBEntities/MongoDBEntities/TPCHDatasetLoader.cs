using MongoDB.Entities;
using System;
using System.Collections.Generic;
using System.Text;

namespace MongoDBEntities
{
    public class TPCHDatasetLoader
    {
        /// <summary>
        /// Parses a lineitem row into an array of typed tag values (ints, doubles, strings).
        /// Equivalent to Java's TPCHDatasetLoader.createLineitemsTags().
        /// </summary>
        public static object[] CreateLineitemsTags(string[] lineitemsRow)
        {
            return new object[]
            {
                Convert.ToInt32(lineitemsRow[0]),
                Convert.ToInt32(lineitemsRow[1]),
                Convert.ToInt32(lineitemsRow[2]),
                Convert.ToInt32(lineitemsRow[3]),
                Convert.ToInt32(lineitemsRow[4]),
                Convert.ToDouble(lineitemsRow[5]),
                Convert.ToDouble(lineitemsRow[6]),
                Convert.ToDouble(lineitemsRow[7]),
                lineitemsRow[8],
                lineitemsRow[9],
                DateTime.Parse(lineitemsRow[10]),
                DateTime.Parse(lineitemsRow[11]),
                DateTime.Parse(lineitemsRow[12]),
                lineitemsRow[13],
                lineitemsRow[14],
                lineitemsRow[15]
            };
        }

        /// <summary>
        /// Shuffles the tags array using a seeded Random and returns a random-length prefix (1..Count).
        /// Equivalent to Java's TPCHDatasetLoader.shuffleArrayItemsAndLenght().
        /// Uses the same Fisher-Yates algorithm as Java's Collections.shuffle().
        /// Numbers differ from Java (different PRNG) but are deterministic for a given seed.
        /// </summary>
        public static object[] ShuffleArrayItemsAndLength(object[] tags, long shuffleSeed)
        {
            Random random = new Random((int)shuffleSeed);

            List<object> list = new List<object>(tags);

            // Fisher-Yates shuffle — same algorithm as Java's Collections.shuffle()
            for (int i = list.Count; i > 1; i--)
            {
                int j = random.Next(i);
                (list[i - 1], list[j]) = (list[j], list[i - 1]);
            }

            int size = 1 + random.Next(list.Count); // 1 to list.Count
            return list.GetRange(0, size).ToArray();
        }

        /// <summary>
        /// Returns a shuffled, random-length subset of tag values from a lineitem row,
        /// seeded by the given ID so the result is deterministic per document.
        /// Equivalent to Java's TPCHDatasetLoader.getShuffledLineitemsTagsFromRow().
        /// </summary>
        public static object[] GetShuffledLineitemsTagsFromRow(string[] lineitemsRow, long shuffleSeed)
        {
            return ShuffleArrayItemsAndLength(CreateLineitemsTags(lineitemsRow), shuffleSeed);
        }


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
