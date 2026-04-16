package cz.cuni.mff.mongodb_java.morphia.models;

import dev.morphia.annotations.Embedded;

@Embedded
public class Address {
    private String street;
    private String city;

    public Address() {}

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }
}