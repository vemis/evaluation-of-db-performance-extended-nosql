using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;
using System;

namespace MongoDBEntities.Models.TPC_H
{
    public class OrdersEOnlyOCommentIndexed : IEntity
    {
        [BsonId]
        public int o_orderkey { get; set; }

        public Date o_orderdate { get; set; }

        public string o_comment { get; set; }

        public OrdersEOnlyOCommentIndexed() { }

        public OrdersEOnlyOCommentIndexed(int o_orderkey, Date o_orderdate, string o_comment)
        {
            this.o_orderkey = o_orderkey;
            this.o_orderdate = o_orderdate;
            this.o_comment = o_comment;
        }

        public object GenerateNewID()
        {
            throw new NotImplementedException();
        }

        public bool HasDefaultID()
        {
            return false;
        }
    }
}
