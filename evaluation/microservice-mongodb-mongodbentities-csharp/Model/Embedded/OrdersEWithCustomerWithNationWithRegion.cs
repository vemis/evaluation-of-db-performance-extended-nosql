using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;

namespace MongoDBEntitiesMicroservice.Model.Embedded 
{
    public class OrdersEWithCustomerWithNationWithRegion : IEntity
    {
        [BsonId]
        public int o_orderkey { get; set; }
        public Date o_orderdate { get; set; }
        public CustomerEOnlyCNameCNation o_customer { get; set; }

        public OrdersEWithCustomerWithNationWithRegion() { }

        public OrdersEWithCustomerWithNationWithRegion(int o_orderkey, Date o_orderdate, CustomerEOnlyCNameCNation? o_customer)
        {
            this.o_orderkey = o_orderkey;
            this.o_orderdate = o_orderdate;
            this.o_customer = o_customer;
        }

        public object GenerateNewID() => throw new NotImplementedException();
        public bool HasDefaultID() => false;
    }
}
