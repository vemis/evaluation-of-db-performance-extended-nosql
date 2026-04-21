using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;

namespace MongoDBEntitiesMicroservice.Model.Relational
{
    public class SupplierR : IEntity
    {
        [BsonId]
        public int s_suppkey { get; set; }
        public string s_name;
        public string s_address;
        public int s_nationkey;
        public string s_phone;
        public double s_acctbal;
        public string s_comment;

        public SupplierR(string[] row) : this(
            Convert.ToInt32(row[0]), row[1], row[2],
            Convert.ToInt32(row[3]), row[4], Convert.ToDouble(row[5]), row[6]) { }

        public SupplierR(int s_suppkey, string s_name, string s_address, int s_nationkey,
            string s_phone, double s_acctbal, string s_comment)
        {
            this.s_suppkey = s_suppkey;
            this.s_name = s_name;
            this.s_address = s_address;
            this.s_nationkey = s_nationkey;
            this.s_phone = s_phone;
            this.s_acctbal = s_acctbal;
            this.s_comment = s_comment;
        }

        public object GenerateNewID() => throw new NotImplementedException();
        public bool HasDefaultID() => false;
    }
}
