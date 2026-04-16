package cz.cuni.mff.mongodb_java.morphia;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import cz.cuni.mff.mongodb_java.morphia.models.Address;
import cz.cuni.mff.mongodb_java.morphia.models.Employee;
import dev.morphia.Datastore;
import dev.morphia.DeleteOptions;
import dev.morphia.Morphia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dev.morphia.mapping.MapperOptions;
import dev.morphia.query.filters.Filters.*;
import dev.morphia.query.Sort.*;
import dev.morphia.query.FindOptions;

import static dev.morphia.query.Sort.ascending;
import static dev.morphia.query.filters.Filters.*;
import static dev.morphia.query.Sort.*;
import static java.nio.file.Files.delete;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        // 1. Create a MongoClient (connects to local MongoDB by default)
        MongoClient client = MongoClients.create("mongodb://localhost:27017");

        // 2. Configure Morphia
        MapperOptions options = MapperOptions.builder()
                .storeNulls(false)     // <-- THIS makes Morphia write null values
                .build();

        // 3. Create a Datastore instance
        Datastore datastore = Morphia.createDatastore(client, "morphia_database", options);

        // 4. Tell Morphia to discover your entity classes
        datastore.getMapper().mapPackage("cz.cuni.mff.mongodb_java.morphia.models");

        datastore.ensureIndexes();

        System.out.println("Morphia initialized!");

        // Insert
        final Employee joeDoe = new Employee(    "Joe Doe",
                                                45,
                                                null,
                                                new ArrayList<Object>( Arrays.asList("asd@email.com", "asd@email.cz")), 45000.0,
                                                new Address("Heatrow 1", "NYC")
        );
        datastore.save(joeDoe);

        System.out.println("Joe Doe saved!");

        final Employee janeDoe = new Employee(    "Jane Doe",
                31,
                joeDoe,
                new ArrayList<Object>( Arrays.asList("jane@email.com", "jane2@email.cz")), 41000.0,
                new Address("Dlouha 25", "Praha")
        );
        datastore.save(janeDoe);

        System.out.println("Jane Doe saved!");


        // Mixed Types and Null Representation

        final Employee joeDoeMixedTypesNull = new Employee(
                "John Doe",
                null,
                null
        );

        datastore.save(joeDoeMixedTypesNull);
        System.out.println("joeDoeMixedTypesNull saved!");



        // Mixed Types and null representation
        final Employee joeDoeMixedNull = new Employee(
                "Joe Doe forty five",
                "forty five",
                null
        );

        datastore.save(joeDoeMixedNull);
        System.out.println("joeDoeMixedNull saved!");

        // Mixed Types
        final Employee joeDoeMixedNull1 = new Employee(
                "Joe Doe Mixed1",
                "thirty one",
                null
        );
        datastore.save(joeDoeMixedNull1);

        final Employee joeDoeMixedNull2 = new Employee(
                "Joe Doe Mixed2",
                null,
                null
        );
        datastore.save(joeDoeMixedNull2);

        final Employee joeDoeMixedNull4 = new Employee(
                "Joe Doe Mixed4",
                45,
                null,
                new ArrayList<Object>( Arrays.asList("asd@email.com", 123)),
                45000.0,
                null
        );
        datastore.save(joeDoeMixedNull4);


        // Querying Mixed Types and Null Representation

        //case 1 & 2: same key, different data types and missing keys

        List<Employee> youngestEmployees = datastore.find(Employee.class, new FindOptions()
                        .sort(ascending("age")))
                .iterator().toList();
        System.out.println("Employees sorted by age:");
        for (Employee employee : youngestEmployees) {
            System.out.println( employee.getName());
        }

        List<Employee> age31 = datastore.find(Employee.class)
                .filter(eq("age", 31))
                .iterator()
                .toList();
        System.out.println("age 31 employees:" + age31.stream()
                .map(Employee::getName)
                .collect(Collectors.toList()));

        // case 3: Heterogeneous array
        List<Employee> emails = datastore.find(Employee.class)
                .filter(in("emails", Arrays.asList(123)))
                .iterator()
                .toList();
        System.out.println("emails having this data:" + emails.stream()
                .map(Employee::getName)
                .collect(Collectors.toList()));


        /*
        // Query
        // Find the youngest employee
        Employee youngestEmployee = datastore.find(Employee.class, new FindOptions()
                        .sort(ascending("age")))
                        .first();
        System.out.println("Youngest employee:" + youngestEmployee.getName());

        // Find all employees living in NYC
        List<Employee> employeesInPrague = datastore.find(Employee.class)
                .filter(eq("address.city", "NYC"))
                .iterator()
                .toList();

        for (Employee employee : employeesInPrague) {
            System.out.println( "Employees living in NYC:" + employee.getName());
        }

        // Delete
        datastore.find(Employee.class)
                .filter(in("name", Arrays.asList("Joe Doe","Jane Doe")))
                .delete(new DeleteOptions()
                        .multi(true));

        System.out.println("Joe Doe and Jane Doe deleted!");

        */
    }
}