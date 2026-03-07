using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;
using System;
using System.Collections.Generic;
using System.Text;

namespace MongoDBEntities.Models.TPC_H
{
    public class NationR : IEntity
    {
        [BsonId]
        public int n_nationkey {  get; set; }

        public string n_name;

        public int n_regionkey; //foreign key

        public string n_comment;

        public NationR(string[] row) : this
            (
                Convert.ToInt32(row[0]),
                row[1],
                Convert.ToInt32(row[2]),
                row[3]
            ) 
        { }

        public NationR(int n_nationkey, string n_name, int n_regionkey, string n_comment)
        {
            this.n_nationkey = n_nationkey;
            this.n_name = n_name;
            this.n_regionkey = n_regionkey;
            this.n_comment = n_comment;
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
