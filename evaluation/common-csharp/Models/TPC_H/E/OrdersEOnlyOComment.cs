using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;

namespace CommonCSharp.Models.TPC_H
{
    public class OrdersEOnlyOComment : IEntity
    {
        [BsonId]
        public int o_orderkey { get; set; }
        public Date o_orderdate { get; set; }
        public string o_comment { get; set; }

        public OrdersEOnlyOComment() { }

        public OrdersEOnlyOComment(int o_orderkey, Date o_orderdate, string o_comment)
        {
            this.o_orderkey = o_orderkey;
            this.o_orderdate = o_orderdate;
            this.o_comment = o_comment;
        }

        public object GenerateNewID() => throw new NotImplementedException();
        public bool HasDefaultID() => false;
    }
}
