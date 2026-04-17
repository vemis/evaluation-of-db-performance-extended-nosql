package cz.cuni.mff.java.microservice.model.embedded;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Property;

import java.util.List;

@Entity
@Indexes({
        @Index(fields = @Field(value = "orders.o_orderkey")),
        @Index(fields = @Field(value = "orders.o_custkey"))
})
public class CustomerEWithOrders {
    @Id
    private int c_custkey;
    private String c_name;
    private String c_address;
    @Indexed
    private int c_nationkey;
    private String c_phone;
    private double c_acctbal;
    private String c_mktsegment;
    private String c_comment;

    @Property
    List<OrdersE> orders;

    public CustomerEWithOrders() {}

    public CustomerEWithOrders(int c_custkey, String c_name, String c_address, int c_nationkey,
                               String c_phone, double c_acctbal, String c_mktsegment,
                               String c_comment, List<OrdersE> orders) {
        this.c_custkey = c_custkey;
        this.c_name = c_name;
        this.c_address = c_address;
        this.c_nationkey = c_nationkey;
        this.c_phone = c_phone;
        this.c_acctbal = c_acctbal;
        this.c_mktsegment = c_mktsegment;
        this.c_comment = c_comment;
        this.orders = orders;
    }
}
