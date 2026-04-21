namespace CommonCSharp.Loader
{
    public class TPCHDatasetLoader
    {
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

        public static object[] ShuffleArrayItemsAndLength(object[] tags, long shuffleSeed)
        {
            Random random = new Random((int)shuffleSeed);
            List<object> list = new List<object>(tags);
            for (int i = list.Count; i > 1; i--)
            {
                int j = random.Next(i);
                (list[i - 1], list[j]) = (list[j], list[i - 1]);
            }
            int size = 1 + random.Next(list.Count);
            return list.GetRange(0, size).ToArray();
        }

        public static object[] GetShuffledLineitemsTagsFromRow(string[] lineitemsRow, long shuffleSeed)
        {
            return ShuffleArrayItemsAndLength(CreateLineitemsTags(lineitemsRow), shuffleSeed);
        }

        public static List<string[]> ReadDataFromCustomSeparator(string filePath)
        {
            var rows = new List<string[]>();
            using var reader = new StreamReader(filePath);
            while (!reader.EndOfStream)
            {
                var line = reader.ReadLine();
                if (line != null)
                    rows.Add(line.Split('|'));
            }
            return rows;
        }
    }
}
