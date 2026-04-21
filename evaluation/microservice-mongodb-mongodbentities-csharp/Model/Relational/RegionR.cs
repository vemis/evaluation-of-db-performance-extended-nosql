using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Entities;

namespace MongoDBEntitiesMicroservice.Model.Relational
{
    public class RegionR : IEntity
    {
        [BsonId]
        public int r_regionkey { get; set; }
        public string r_name;
        public string r_comment;

        public RegionR(string[] row) : this(Convert.ToInt32(row[0]), row[1], row[2]) { }

        public RegionR(int r_regionkey, string r_name, string r_comment)
        {
            this.r_regionkey = r_regionkey;
            this.r_name = r_name;
            this.r_comment = r_comment;
        }

        public object GenerateNewID() => throw new NotImplementedException();
        public bool HasDefaultID() => false;
    }
}
