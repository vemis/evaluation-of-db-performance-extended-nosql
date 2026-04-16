using EFCore.Models;
using Microsoft.EntityFrameworkCore;

namespace EFCore
{
    internal class Program
    {
        static async Task Main(string[] args)
        {
            Console.WriteLine("Connecting");

            await using var db = new CouchbaseContext();

            Console.WriteLine("Connected");

            // Insert

            Employee joeDoe = new Employee()
            {
                EmployeeId = Guid.NewGuid().ToString(),
                Name = "Joe Doe",
                Age = 45,
                ManagerId = null,
                Emails = new List<string> { "asd@email.com", "asd@email.cz" },
                Salary = 45000.0
                //,
                //Address = new Address { Street = "Heatrow 1", City = "NYC" }
            };

            db.Add(joeDoe);
            await db.SaveChangesAsync();
            
            Console.WriteLine("Joe Doe saved");

            Employee janeDoe = new Employee()
            {
                EmployeeId = Guid.NewGuid().ToString(),
                Name = "Jane Doe",
                Age = 31,
                ManagerId = joeDoe.EmployeeId,
                Emails = new List<string> { "jane@email.com", "jane2@email.cz" },
                Salary = 41000.0
                //,
                //Address = new Address { Street = "Dlouha 25", City = "Praha" }
            };

            db.Add(janeDoe);
            await db.SaveChangesAsync();
            Console.WriteLine("Jane Doe saved!");

            // Query

            Employee youngest = await db.Employees
                .OrderBy(x => x.Age)
                .FirstOrDefaultAsync();

            Console.WriteLine($"Youngest employee: {youngest?.Name}");
            /*
            List<Employee> employeesInNyc = await db.Employees
                .Where(e => e.Address != null && e.Address.City == "NYC")
                .ToListAsync();

            foreach (var e in employeesInNyc)
                Console.WriteLine($"Employee living in NYC: {e.Name}");*/


        }
    }
}
