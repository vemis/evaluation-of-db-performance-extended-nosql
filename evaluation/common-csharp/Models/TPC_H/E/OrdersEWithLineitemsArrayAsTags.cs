using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;

namespace CommonCSharp.Models.TPC_H
{
    public class OrdersEWithLineitemsArrayAsTags : IEntity
    {
        [BsonId]
        public int o_orderkey { get; set; }
        public Date o_orderdate { get; set; }
        public List<object> o_lineitems_tags { get; set; }

        public OrdersEWithLineitemsArrayAsTags() { }

        public OrdersEWithLineitemsArrayAsTags(int o_orderkey, Date o_orderdate, List<object> o_lineitems_tags)
        {
            this.o_orderkey = o_orderkey;
            this.o_orderdate = o_orderdate;
            this.o_lineitems_tags = o_lineitems_tags;
        }

        public object GenerateNewID() => throw new NotImplementedException();
        public bool HasDefaultID() => false;
    }
}
