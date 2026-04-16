package cz.cuni.mff.mongodb_java.springdata_e.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

//@Document(collection = "ordersR")
public class OrdersE {
    //@Id
    @Indexed
    private int o_orderkey;
    @Indexed
    private int o_custkey;
    private String o_orderstatus;
    private double o_totalprice;
    private LocalDate o_orderdate;
    private String o_orderpriority;
    private String o_clerk;
    private String o_shippriority;
    private String o_comment;

    public int getO_custkey() {
        return o_custkey;
    }

    public OrdersE() {}

    public OrdersE(int o_orderkey, int o_custkey, String o_orderstatus, double o_totalprice, LocalDate o_orderdate, String o_orderpriority, String o_clerk, String o_shippriority,  String o_comment) {
        this.o_orderkey = o_orderkey;
        this.o_custkey = o_custkey;
        this.o_orderstatus = o_orderstatus;
        this.o_orderdate = o_orderdate;
        this.o_orderpriority = o_orderpriority;
        this.o_clerk = o_clerk;
        this.o_shippriority = o_shippriority;
        this.o_totalprice = o_totalprice;
        this.o_comment = o_comment;
    }
}
