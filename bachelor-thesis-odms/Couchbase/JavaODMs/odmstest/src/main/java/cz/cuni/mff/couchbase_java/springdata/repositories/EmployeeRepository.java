package cz.cuni.mff.couchbase_java.springdata.repositories;

import cz.cuni.mff.couchbase_java.springdata.models.Employee;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

import java.util.List;

@Repository
public interface EmployeeRepository extends CouchbaseRepository<Employee, String> {
    List<Employee> findByAddress_City(String city);

    List<Employee> findByNameIn(List<String> names);

    Employee findTopByOrderByAgeAsc();

    List<Employee> findByAge(Object age);

    @Query("#{#n1ql.selectEntity} WHERE ANY e IN emails SATISFIES e = $1 END")
    List<Employee> findByEmailsContaining(Object email);

}
