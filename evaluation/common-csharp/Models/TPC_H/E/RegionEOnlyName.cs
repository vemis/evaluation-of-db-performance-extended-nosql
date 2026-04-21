namespace CommonCSharp.Models.TPC_H
{
    public class RegionEOnlyName
    {
        public int r_regionkey { get; set; }
        public string r_name { get; set; }

        public RegionEOnlyName() { }

        public RegionEOnlyName(int r_regionkey, string r_name)
        {
            this.r_regionkey = r_regionkey;
            this.r_name = r_name;
        }
    }
}
