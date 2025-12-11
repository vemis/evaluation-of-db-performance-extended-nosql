package cz.cuni.mff.couchbase_java.springdata.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;

import java.util.List;

@Document
public class Employee {
    @Id
    private String id;

    private String name;

    private Object age;

    // Couchbase does not use @DBRef — CRDT references must be manual
    private String managerId;

    private List<Object> emails;
    private Double salary;

    @Field
    private Address address;

    private Object TestToRecompile;

    public Employee() {}

    public Employee(String id, String name, Object age, String managerId, List<Object> emails, Double salary, Address address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.managerId = managerId;
        this.emails = emails;
        this.salary = salary;
        this.address = address;
    }

    public Employee(String id, String name, Object age, String managerId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.managerId = managerId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
