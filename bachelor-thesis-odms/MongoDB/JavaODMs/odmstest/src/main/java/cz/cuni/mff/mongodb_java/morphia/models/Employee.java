package cz.cuni.mff.mongodb_java.morphia.models;

import dev.morphia.annotations.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Entity("employees")
public class Employee {
    @Id
    private ObjectId id;
    private String name;
    private Object age;
    @Reference
    private Employee manager;
    private ArrayList<Object> emails;
    private Double salary;
    private Address address;

    // Morphia needs this no-arg constructor
    public Employee() {}

    public Employee(String name, Object age, Employee manager, ArrayList<Object> emails, Double salary,  Address address) {
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
