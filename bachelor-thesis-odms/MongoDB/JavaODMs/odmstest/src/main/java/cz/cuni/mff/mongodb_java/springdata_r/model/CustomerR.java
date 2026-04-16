package cz.cuni.mff.mongodb_java.springdata_r.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "customerR")
public class CustomerR {
    @Id
    private int c_custkey;
    private String c_name;
    private String c_address;
    @Indexed
    private int c_nationkey;
    private String c_phone;
    private double c_acctbal;
    private String c_mktsegment;
    private String c_commen;

    public CustomerR() {}

    public CustomerR(int c_custkey,
                     String c_name,
                     String c_address,
                     int c_nationkey,
                     String c_phone,
                     double c_acctbal,
                     String c_mktsegment,
                     String c_commen) {
        this.c_custkey = c_custkey;
        this.c_name = c_name;
        this.c_address = c_address;
        this.c_nationkey = c_nationkey;
        this.c_phone = c_phone;
        this.c_acctbal = c_acctbal;
        this.c_mktsegment = c_mktsegment;
        this.c_commen = c_commen;
    }
}
