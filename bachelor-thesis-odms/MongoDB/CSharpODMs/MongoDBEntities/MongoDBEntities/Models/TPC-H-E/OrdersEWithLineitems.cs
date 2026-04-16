using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;
using System;
using System.Collections.Generic;

namespace MongoDBEntities.Models.TPC_H
{
    public class OrdersEWithLineitems : IEntity
    {
        [BsonId]
        public int o_orderkey { get; set; }
        public int o_custkey { get; set; }
        public string o_orderstatus { get; set; }
        public string o_totalprice { get; set; }
        public Date o_orderdate { get; set; }
        public string o_orderpriority { get; set; }
        public string o_clerk { get; set; }
        public string o_shippriority { get; set; }
        public string o_comment { get; set; }

        public List<LineitemE> o_lineitems { get; set; }

        public OrdersEWithLineitems() { }

        public OrdersEWithLineitems(string[] row, List<LineitemE> lineitems) : this(
            Convert.ToInt32(row[0]),
            Convert.ToInt32(row[1]),
            row[2],
            row[3],
            new Date(DateTime.Parse(row[4])),
            row[5],
            row[6],
            row[7],
            row[8],
            lineitems)
        { }

        public OrdersEWithLineitems(int o_orderkey, int o_custkey, string o_orderstatus, string o_totalprice,
            Date o_orderdate, string o_orderpriority, string o_clerk, string o_shippriority, string o_comment,
            List<LineitemE> o_lineitems)
        {
            this.o_orderkey = o_orderkey;
            this.o_custkey = o_custkey;
            this.o_orderstatus = o_orderstatus;
            this.o_totalprice = o_totalprice;
            this.o_orderdate = o_orderdate;
            this.o_orderpriority = o_orderpriority;
            this.o_clerk = o_clerk;
            this.o_shippriority = o_shippriority;
            this.o_comment = o_comment;
            this.o_lineitems = o_lineitems;
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
