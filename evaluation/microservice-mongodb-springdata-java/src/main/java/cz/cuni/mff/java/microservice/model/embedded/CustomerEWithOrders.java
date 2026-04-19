package cz.cuni.mff.java.microservice.model.embedded;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "customerEWithOrders")
@CompoundIndexes({
        @CompoundIndex(name = "idx_orders_o_orderkey", def = "{'orders.o_orderkey': 1}"),
        @CompoundIndex(name = "idx_orders_o_custkey",  def = "{'orders.o_custkey': 1}")
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
    private List<OrdersE> orders;

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
