using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MongoDBEntities.Models.TPC_H
{
    public class TestR : IEntity
    {
        [BsonId]
        public int SMT { get; set; }

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
