package cz.cuni.mff.mongodb_java.springdata.repositories;

import cz.cuni.mff.mongodb_java.springdata.models.Employee;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public interface EmployeeRepository extends MongoRepository<Employee, ObjectId> {
    List<Employee> findByAddress_City(String city);

    List<Employee> findByNameIn(List<String> names);

    Employee findTopByOrderByAgeAsc();

    List<Employee> findByAge(Object age);

    List<Employee> findByEmailsIn(List<Object> emails);
}
