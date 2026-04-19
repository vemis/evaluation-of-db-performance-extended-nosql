package cz.cuni.mff.java.microservice.model.embedded;

import java.time.LocalDate;

public class OrdersE {
    private int o_orderkey;
    private int o_custkey;
    private String o_orderstatus;
    public double o_totalprice;
    private LocalDate o_orderdate;
    private String o_orderpriority;
    private String o_clerk;
    private String o_shippriority;
    private String o_comment;

    public OrdersE() {}

    public OrdersE(int o_orderkey, int o_custkey, String o_orderstatus, double o_totalprice,
                   LocalDate o_orderdate, String o_orderpriority, String o_clerk,
                   String o_shippriority, String o_comment) {
        this.o_orderkey = o_orderkey;
        this.o_custkey = o_custkey;
        this.o_orderstatus = o_orderstatus;
        this.o_totalprice = o_totalprice;
        this.o_orderdate = o_orderdate;
        this.o_orderpriority = o_orderpriority;
        this.o_clerk = o_clerk;
        this.o_shippriority = o_shippriority;
        this.o_comment = o_comment;
    }

    public int get_o_custkey() { return o_custkey; }
}
