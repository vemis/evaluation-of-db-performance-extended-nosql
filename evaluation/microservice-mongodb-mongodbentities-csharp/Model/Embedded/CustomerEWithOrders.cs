using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;

namespace MongoDBEntitiesMicroservice.Model.Embedded 
{
    public class CustomerEWithOrders : IEntity
    {
        [BsonId]
        public int c_custkey { get; set; }
        public string c_name;
        public string c_address;
        public int c_nationkey;
        public string c_phone;
        public double c_acctbal;
        public string c_mktsegment;
        public string c_commen;
        public List<OrdersE> c_orders;

        public CustomerEWithOrders(string[] row, List<OrdersE> orders) : this(
            Convert.ToInt32(row[0]), row[1], row[2], Convert.ToInt32(row[3]),
            row[4], Convert.ToDouble(row[5]), row[6], row[7], orders) { }

        public CustomerEWithOrders(int c_custkey, string c_name, string c_address, int c_nationkey,
            string c_phone, double c_acctbal, string c_mktsegment, string c_commen, List<OrdersE> c_orders)
        {
            this.c_custkey = c_custkey;
            this.c_name = c_name;
            this.c_address = c_address;
            this.c_nationkey = c_nationkey;
            this.c_phone = c_phone;
            this.c_acctbal = c_acctbal;
            this.c_mktsegment = c_mktsegment;
            this.c_commen = c_commen;
            this.c_orders = c_orders;
        }

        public object GenerateNewID() => throw new NotImplementedException();
        public bool HasDefaultID() => false;
    }
}
