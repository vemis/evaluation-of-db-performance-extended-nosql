using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;

namespace MongoDBEntitiesMicroservice.Model.Embedded 
{
    public class OrdersEWithLineitemsArrayAsTagsIndexed : IEntity
    {
        [BsonId]
        public int o_orderkey { get; set; }
        public Date o_orderdate { get; set; }
        public List<object> o_lineitems_tags_indexed { get; set; }

        public OrdersEWithLineitemsArrayAsTagsIndexed() { }

        public OrdersEWithLineitemsArrayAsTagsIndexed(int o_orderkey, Date o_orderdate, List<object> o_lineitems_tags_indexed)
        {
            this.o_orderkey = o_orderkey;
            this.o_orderdate = o_orderdate;
            this.o_lineitems_tags_indexed = o_lineitems_tags_indexed;
        }

        public object GenerateNewID() => throw new NotImplementedException();
        public bool HasDefaultID() => false;
    }
}
