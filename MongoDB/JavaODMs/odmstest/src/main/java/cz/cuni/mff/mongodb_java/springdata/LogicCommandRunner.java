package cz.cuni.mff.mongodb_java.springdata;

import cz.cuni.mff.mongodb_java.springdata.models.Address;
import cz.cuni.mff.mongodb_java.springdata.models.Employee;
import cz.cuni.mff.mongodb_java.springdata.repositories.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class LogicCommandRunner {
    @Bean
    CommandLineRunner run(EmployeeRepository repo) {
        return args -> {
            // clean start
            repo.deleteAll();

            // Insert
            Employee joeDoe = new Employee(
                    "Joe Doe",
                    45,
                    null,
                    Arrays.asList("asd@email.com", "asd@email.cz"),
                    45000.0,
                    new Address("Heatrow 1", "NYC")
            );
            repo.save(joeDoe);
            System.out.println("Joe Doe saved!");


            // Insert Jane Doe
            Employee janeDoe = new Employee(
                    "Jane Doe",
                    31,
                    joeDoe,
                    Arrays.asList("jane@email.com", "jane2@email.cz"),
                    41000.0,
                    new Address("Dlouha 25", "Praha")
            );
            repo.save(janeDoe);
            System.out.println("Jane Doe saved!");

            // Mixed Types and Null Representation
            Employee joeDoeMixedtypesNull = new Employee(
                    "Joe Doe",
                    "forty five",
                    null
            );
            repo.save(joeDoeMixedtypesNull);
            System.out.println("joeDoeMixedtypesNull saved!");


            // Mixed Types and null representation
            final Employee joeDoeMixedNull = new Employee(
                    "Joe Doe forty five",
                    "forty five",
                    null
            );

            repo.save(joeDoeMixedNull);
            System.out.println("joeDoeMixedNull saved!");

            // Mixed Types
            final Employee joeDoeMixedNull1 = new Employee(
                    "Joe Doe Mixed1",
                    "thirty one",
                    null
            );
            repo.save(joeDoeMixedNull1);

            final Employee joeDoeMixedNull2 = new Employee(
                    "Joe Doe Mixed2",
                    null,
                    null
            );
            repo.save(joeDoeMixedNull2);

            final Employee joeDoeMixedNull4 = new Employee(
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



            List<Employee> age31 = repo.findByAge(null);

            System.out.println("age 31 employees:" + age31.stream()
                    .map(Employee::getName)
                    .collect(Collectors.toList()));

            // case 3: Heterogeneous array
            List<Employee> emails = repo.findByEmailsIn(Arrays.asList("asd@email.com"));

            System.out.println("emails having this data:" + emails.stream()
                    .map(Employee::getName)
                    .collect(Collectors.toList()));


            /*

            // Query: find youngest
            Employee youngest = repo.findTopByOrderByAgeAsc();
            System.out.println("Youngest employee: " + youngest.getName());

            // Find all living in NYC
            List<Employee> inNYC = repo.findByAddress_City("NYC");
            for (Employee emp : inNYC) {
                System.out.println("Employee living in NYC: " + emp.getName());
            };

            // Delete Joe + Jane
            repo.deleteAll(repo.findByNameIn(Arrays.asList("Joe Doe", "Jane Doe")));
            System.out.println("Joe Doe and Jane Doe deleted!");
            */
        };
    }
}
