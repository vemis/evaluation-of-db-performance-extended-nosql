namespace MongoDBEntitiesMicroservice.Model.Embedded 
{
    public class NationEOnlyNNameNRegion
    {
        public int n_nationkey { get; set; }
        public string n_name { get; set; }
        public int n_regionkey { get; set; }
        public RegionEOnlyName n_region { get; set; }

        public NationEOnlyNNameNRegion() { }

        public NationEOnlyNNameNRegion(int n_nationkey, string n_name, int n_regionkey, RegionEOnlyName n_region)
        {
            this.n_nationkey = n_nationkey;
            this.n_name = n_name;
            this.n_regionkey = n_regionkey;
            this.n_region = n_region;
        }
    }
}
