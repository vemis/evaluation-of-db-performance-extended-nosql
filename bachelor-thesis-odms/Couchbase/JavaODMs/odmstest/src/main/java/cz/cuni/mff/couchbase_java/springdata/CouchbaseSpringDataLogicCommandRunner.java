package cz.cuni.mff.couchbase_java.springdata;

import cz.cuni.mff.couchbase_java.springdata.models.Address;
import cz.cuni.mff.couchbase_java.springdata.models.Employee;
import cz.cuni.mff.couchbase_java.springdata.repositories.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Configuration
public class CouchbaseSpringDataLogicCommandRunner {
    @Bean
    CommandLineRunner run(EmployeeRepository repo) {
        return args -> {

            repo.deleteAll();
            System.out.println("Delete all employees, starting with clean repo");

            // Create Joe
            Employee joeDoe = new Employee(
                    UUID.randomUUID().toString(),
                    "Joe Doe",
                    45,
                    null,
                    Arrays.asList("asd@email.com", "asd@email.cz"),
                    45000.0,
                    new Address("Heatrow 1", "NYC")
            );

            repo.save(joeDoe);
            System.out.println("Joe saved!");


            // Create Jane
            Employee janeDoe = new Employee(
                    UUID.randomUUID().toString(),
                    "Jane Doe",
                    31,
                    joeDoe.getId(),
                    Arrays.asList("jane@email.com", "jane2@email.cz"),
                    41000.0,
                    new Address("Dlouha 25", "Praha")
            );

            repo.save(janeDoe);
            System.out.println("Jane saved!");

            // Mixed Types and Null Representation

            Employee joeDoeMixedTypesNull = new Employee(
                    UUID.randomUUID().toString(),
                    "Joe Doe Null",
                    null,
                    null
            );

            repo.save(joeDoeMixedTypesNull);
            System.out.println("joeDoeMixedTypesNull saved!");


            final Employee joeDoeMixedNull = new Employee(
                    UUID.randomUUID().toString(),
                    "Joe Doe forty five",
                    "forty five",
                    null
            );

            repo.save(joeDoeMixedNull);
            System.out.println("joeDoeMixedNull saved!");

            // Mixed Types
            final Employee joeDoeMixedNull1 = new Employee(
                    UUID.randomUUID().toString(),
                    "Joe Doe Mixed1",
                    "thirty one",
                    null
            );
            repo.save(joeDoeMixedNull1);

            final Employee joeDoeMixedNull2 = new Employee(
                    UUID.randomUUID().toString(),
                    "Joe Doe Mixed2",
                    null,
                    null
            );
            repo.save(joeDoeMixedNull2);

            final Employee joeDoeMixedNull4 = new Employee(
                    UUID.randomUUID().toString(),
                    "Joe Doe Mixed4",
                    46,
                    null,
                    Arrays.asList("asd@email.com", 123),
                    45000.0,
                    null
            );
            repo.save(joeDoeMixedNull4);

            // Querying Mixed Types and Null Representation

            //case 1 & 2: same key, different data types and missing keys

            List<Employee> youngestEmployees =
                    repo.findAll(Sort.by(Sort.Direction.ASC, "age"));
            System.out.println("Employees sorted by age:" + youngestEmployees.stream()
                    .map(Employee::getName)
                    .collect(Collectors.toList()));


            List<Employee> age31 = repo.findByAge("thirty one");

            System.out.println("age 31 employees:" + age31.stream()
                    .map(Employee::getName)
                    .collect(Collectors.toList()));


            // case 3: Heterogeneous array
            List<Employee> emails = repo.findByEmailsContaining(Arrays.asList("asd@email.com"));

            System.out.println("emails having this data:" + emails.stream()
                    .map(Employee::getName)
                    .collect(Collectors.toList()));






            /*
            // Youngest employee
            Employee youngest = repo.findTopByOrderByAgeAsc();
            System.out.println("Youngest employee: " + youngest.getName());

            // Employees in NYC
            List<Employee> inNY = repo.findByAddress_City("NYC");
            inNY.forEach(e ->
                    System.out.println("Employees living in NYC: " + e.getName())
            );


            // Delete
            repo.deleteAll(
                    repo.findByNameIn(Arrays.asList("Joe Doe", "Jane Doe"))
            );

            System.out.println("All Doe employees deleted!");

            */
        };
    }

}