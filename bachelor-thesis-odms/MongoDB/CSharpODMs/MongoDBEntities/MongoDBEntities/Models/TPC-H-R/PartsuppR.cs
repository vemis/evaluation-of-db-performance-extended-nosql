using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;
using System;
using System.Collections.Generic;
using System.Text;

namespace MongoDBEntities.Models.TPC_H
{
    public class PartsuppR : IEntity
    {
        [BsonId]
        public string ps_id { get; set; }
        
        public int ps_partkey;
        public int ps_suppkey;

        public int ps_availqty;
        public double ps_supplycost;
        public string ps_comment;

        public PartsuppR(string[] row) : this
            (
                Convert.ToInt32(row[0]),
                Convert.ToInt32(row[1]),
                Convert.ToInt32(row[2]),
                Convert.ToDouble(row[3]),
                row[4]
            )
        { }

        public PartsuppR(int ps_partkey, int ps_suppkey, int ps_availqty, double ps_supplycost, string ps_comment)
        {
            this.ps_id = ps_partkey.ToString() + "|" + ps_suppkey.ToString();
            this.ps_partkey = ps_partkey;
            this.ps_suppkey = ps_suppkey;
            this.ps_availqty = ps_availqty;
            this.ps_supplycost = ps_supplycost;
            this.ps_comment = ps_comment;
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

