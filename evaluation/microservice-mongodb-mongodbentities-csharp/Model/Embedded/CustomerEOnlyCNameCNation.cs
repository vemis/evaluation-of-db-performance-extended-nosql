namespace MongoDBEntitiesMicroservice.Model.Embedded 
{
    public class CustomerEOnlyCNameCNation
    {
        public int c_custkey { get; set; }
        public string c_name { get; set; }
        public int c_nationkey { get; set; }
        public NationEOnlyNNameNRegion c_nation { get; set; }

        public CustomerEOnlyCNameCNation() { }

        public CustomerEOnlyCNameCNation(int c_custkey, string c_name, int c_nationkey, NationEOnlyNNameNRegion c_nation)
        {
            this.c_custkey = c_custkey;
            this.c_name = c_name;
            this.c_nationkey = c_nationkey;
            this.c_nation = c_nation;
        }
    }
}
