using Couchbase.EntityFrameworkCore;
using Couchbase.EntityFrameworkCore.Extensions;
using EFCore.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection.Metadata;
using System.Text;
using System.Threading.Tasks;

namespace EFCore
{
    internal class CouchbaseContext : DbContext
    {
        public DbSet<Employee> Employees { get; set; } = null;

        protected override void OnConfiguring(DbContextOptionsBuilder options)
        {
            // Configure the Couchbase EFCore provider.
            // Replace connection string / credentials with your cluster/bucket values.
            options.UseCouchbase(
                new Couchbase.ClusterOptions()
                    .WithCredentials("Administrator", "password")
                    .WithConnectionString("couchbases://127.0.0.1"),
                couchbaseOptions =>
                {
                    //couchbaseOptions.Bucket = "_default";
                    //couchbaseOptions.Scope = "_default";
                }
            );

            options.LogTo(Console.WriteLine);
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // Map the Employee entity to the Couchbase collection "employees"
            // (DbContext instance is passed because the provider may need it)
            modelBuilder.Entity<Employee>().ToCouchbaseCollection(this, "efcore_bucket");

            //modelBuilder.Entity<Address>().ToCouchbaseCollection(this, "addresses");

            /*modelBuilder.Entity<Employee>(eb =>
            {
                //eb.ToCouchbaseCollection(this, "efcore_bucket");

                // EF Core 8 complex property (if supported by the provider)
                eb.ComplexProperty(e => e.Address);

            });*/

            // Configure owned / embedded Address as an owned type
            //modelBuilder.Entity<Employee>().OwnsOne(e => e.Address);
            // Mark Address as owned (embedded JSON object)
            /*
            modelBuilder.Entity<Employee>()
                .OwnsOne(e => e.Address, a =>
                {
                    a.Property(p => p.Street);
                    a.Property(p => p.City);
                });
            */
        }
    }
}
