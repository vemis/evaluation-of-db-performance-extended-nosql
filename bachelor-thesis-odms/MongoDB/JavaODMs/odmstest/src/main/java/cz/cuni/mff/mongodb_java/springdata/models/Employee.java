package cz.cuni.mff.mongodb_java.springdata.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "employees")
public class Employee {
    @Id
    private ObjectId id;

    private String name;
    private Object age;

    @DBRef
    private Employee manager;

    private List<Object> emails;
    private Double salary;

    private Address address;

    public Employee() {}

    public Employee(String name, Object age, Employee manager, List<Object> emails, Double salary, Address address) {
        this.name = name;
        this.age = age;
        this.manager = manager;
        this.emails = emails;
        this.salary = salary;
        this.address = address;
    }

    public Employee(String name, Object age, Employee manager) {
        this.name = name;
        this.age = age;
        this.manager = manager;
    }


    public String getName() { return name; }

}
