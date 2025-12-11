using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;
using System;
using System.Collections.Generic;
using System.Text;

namespace MongoDBEntities.Models
{
    internal class Employee : Entity
    {
        public string Name { get; set; }
        public BsonValue Age { get; set; }
        public One<Employee> Manager { get; set; }
        public List<BsonValue> Emails { get; set; }
        public double Salary { get; set; }
        
        [BsonIgnoreIfNull]
        public Address Address { get; set; }

        public string GetName => Name;
    }
}
