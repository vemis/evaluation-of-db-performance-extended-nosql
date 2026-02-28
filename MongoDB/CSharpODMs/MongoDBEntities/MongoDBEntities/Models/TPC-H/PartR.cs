using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;
using System;
using System.Collections.Generic;
using System.Text;

namespace MongoDBEntities.Models.TPC_H
{
    public class PartR : IEntity
    {
        [BsonId]
        public int p_partkey { get; set; }
        public string p_name;
        public string p_mfgr;
        public string p_brand;
        public string p_type;
        public int p_size;
        public string p_container;
        public double p_retailprice;
        public string p_commen;

        public PartR(string[] row) : this
            (
                Convert.ToInt32(row[0]),
                row[1],
                row[2],
                row[3],
                row[4],
                Convert.ToInt32(row[5]),
                row[6],
                Convert.ToDouble(row[7]),
                row[8]
            )
        { }

        public PartR(int p_partkey, string p_name, string p_mfgr, string p_brand, string p_type, int p_size, string p_container, double p_retailprice, string p_commen)
        {
            this.p_partkey = p_partkey;
            this.p_name = p_name;
            this.p_mfgr = p_mfgr;
            this.p_brand = p_brand;
            this.p_type = p_type;
            this.p_size = p_size;
            this.p_container = p_container;
            this.p_retailprice = p_retailprice;
            this.p_commen = p_commen;
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
