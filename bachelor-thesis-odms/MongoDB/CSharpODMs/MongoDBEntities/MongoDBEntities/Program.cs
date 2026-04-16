using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Entities;
using MongoDBEntities.Models;
using System;
using System.Threading.Tasks;


namespace MongoDBEntities
{


    class Program
    {

        // MainTesting Method
        public static async Task Main2(String[] args)
        {
            // Connect to MongoDB
            Console.WriteLine("Connecting to the database:");

            await DB.InitAsync("mongodbentities_database", "localhost", 27017);

            Console.WriteLine("MongoDB.Entities initialized!");

            // Delete all
            await DB.Collection<Employee>().DeleteManyAsync(Builders<Employee>.Filter.Empty);

            // Insert
            Employee joeDoe = new Employee
            {
                Name = "Joe Doe",
                Age = 45,
                Emails = new List<BsonValue> { "asd@email.com", "asd@email.cz" },
                Salary = 45000,
                Address = new Address
                {
                    Street = "Heatrow 1",
                    City = "NYC"
                }
            };

            await joeDoe.SaveAsync();
            Console.WriteLine("Joe Doe saved!");

            Employee janeDoe = new Employee
            {
                Name = "Jane Doe",
                Age = 31,
                Manager = joeDoe.ToReference(),   // reference
                Emails = new List<BsonValue> { "jane@email.com", "jane2@email.cz" },
                Salary = 41000,
                Address = new Address
                {
                    Street = "Dlouha 25",
                    City = "Praha"
                }
            };

            await janeDoe.SaveAsync();
            Console.WriteLine("Jane Doe saved!");


            // Mixed Types and Null Representation

            Employee joeDoeMixedTypesNullRepresentation = new Employee
            {
                Name = "Joe Doe",
                Age = "forty five",
                Manager = null
            };

            await joeDoeMixedTypesNullRepresentation.SaveAsync();
            Console.WriteLine("joeDoeMixedTypesNullRepresentation saved!");


            
             // Mixed Types and null representation
            Employee joeDoeMixedNull = new Employee{
                Name= "Joe Doe forty five",
                Age= "forty five",
                Manager= null
            };

            await joeDoeMixedNull.SaveAsync();
            Console.WriteLine("joeDoeMixedNull saved!");

            // Mixed Types
            Employee joeDoeMixedNull1 = new Employee{
              Name = "Joe Doe Mixed1",
              Age = "thirty one",
              Manager= null,
            };
            await joeDoeMixedNull1.SaveAsync();

            Employee joeDoeMixedNull2 = new Employee{
              Name= "Joe Doe Mixed2",
              Age= 50,
              Manager= null
            };
            await joeDoeMixedNull2.SaveAsync();

            Employee joeDoeMixedNull3 = new Employee{
              Name= "Joe Doe Mixed3",
              Manager= null
            };
            await joeDoeMixedNull3.SaveAsync();

            Employee joeDoeMixedNull4 = new Employee{
              Name= "Joe Doe Mixed4",
              Manager= null,
              Emails= ["abc@email.com", 123]
            };
            await joeDoeMixedNull4.SaveAsync();
    

            // Querying Mixed Types and Null Representation

            //case 1 & 2: same key, different data types and missing keys 

            var youngestEmployees = await DB.Find<Employee>()
                                   .Sort(e => e.Ascending(x => x.Age))
                                   .ExecuteAsync();
            Console.WriteLine("employees sorted by age: " + string.Join(", ", youngestEmployees.Select(emp => emp.Name))
            );

            

            var age31 = await DB.Find<Employee>()
                                .Match(e => e.Age == "thirty one")
                                .ExecuteAsync();
            Console.WriteLine("age31: " + string.Join(", ", age31.Select(emp => emp.Name))
            );


            // case 3: Heterogeneous array
            var emails = await DB.Find<Employee>()
                              .Match(e => e.Emails.Contains(123))
                              .ExecuteAsync();

            Console.WriteLine("emails: " + string.Join(", ", emails.Select(emp => emp.Name))
            );


            //console.log("emails employee:", emails.rows.map((emp) => emp.name));








            /*

            // Query

            // Youngest employee
            Employee youngest = await DB.Find<Employee>()
                                   .Sort(e => e.Ascending(x => x.Age))
                                   .ExecuteFirstAsync();

            Console.WriteLine("Youngest employee: " + youngest.GetName);

            // Employee living in the NYC
            var employeesInNYC = await DB.Find<Employee>()
                                         .Match(e => e.Address.City == "NYC")
                                         .ExecuteAsync();

            foreach (var emp in employeesInNYC)
            {
                Console.WriteLine("Employees living in NYC: " + emp.Name);
            }

            // Delete employees by name (multi-delete)


            string[] toDeleteEmployees = { "Joe Doe", "Jane Doe" };
            var filter = Builders<Employee>.Filter.In(e => e.Name, toDeleteEmployees);

            await DB.Collection<Employee>().DeleteManyAsync(filter);

            Console.WriteLine("Joe Doe and Jane Doe deleted!");
            */
        }
    }
}
