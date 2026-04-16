using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EFCore.Models
{
    internal class Employee
    {
        [Key]
        public string EmployeeId { get; set; } = Guid.NewGuid().ToString();

        public string? Name { get; set; }
        public int Age { get; set; }

        public string? ManagerId { get; set; }

        public List<string>? Emails { get; set; }
        public double Salary { get; set; }

        //public required Address Address { get; set; }
    }
}
