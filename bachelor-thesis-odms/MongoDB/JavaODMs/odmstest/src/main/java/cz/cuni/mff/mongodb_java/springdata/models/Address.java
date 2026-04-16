package cz.cuni.mff.mongodb_java.springdata.models;

public class Address {
    private String street;
    private String city;

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }
}
