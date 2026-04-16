package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;


import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Indexed;

import java.util.ArrayList;
import java.util.List;

@Embedded
public class CustomerE {
    @Indexed
    private int c_custkey;
    private String c_name;
    private String c_address;
    @Indexed
    private int c_nationkey;
    private String c_phone;
    private double c_acctbal;
    private String c_mktsegment;
    private String c_commen;

    List<OrdersE> orders;

    public int getC_nationkey() {
        return c_nationkey;
    }

    public CustomerE() {}

    public CustomerE(int c_custkey,
                     String c_name,
                     String c_address,
                     int c_nationkey,
                     String c_phone,
                     double c_acctbal,
                     String c_mktsegment,
                     String c_commen,
                     List<OrdersE> orders) {
        this.c_custkey = c_custkey;
        this.c_name = c_name;
        this.c_address = c_address;
        this.c_nationkey = c_nationkey;
        this.c_phone = c_phone;
        this.c_acctbal = c_acctbal;
        this.c_mktsegment = c_mktsegment;
        this.c_commen = c_commen;
        this.orders = orders;
    }
}
