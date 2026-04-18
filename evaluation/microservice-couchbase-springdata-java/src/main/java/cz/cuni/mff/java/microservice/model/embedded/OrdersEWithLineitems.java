package cz.cuni.mff.java.microservice.model.embedded;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

import java.time.LocalDate;
import java.util.List;

@Document
@Scope("spring_scope_e")
@Collection("OrdersEWithLineitems")
public class OrdersEWithLineitems {

    @Id
    private int o_orderkey;
    private int o_custkey;
    private String o_orderstatus;
    private double o_totalprice;
    private LocalDate o_orderdate;
    private String o_orderpriority;
    private String o_clerk;
    private String o_shippriority;
    private String o_comment;
    private List<LineitemE> o_lineitems;

    public OrdersEWithLineitems() {}

    public OrdersEWithLineitems(int o_orderkey, int o_custkey, String o_orderstatus, double o_totalprice,
                                LocalDate o_orderdate, String o_orderpriority, String o_clerk,
                                String o_shippriority, String o_comment, List<LineitemE> o_lineitems) {
        this.o_orderkey = o_orderkey;
        this.o_custkey = o_custkey;
        this.o_orderstatus = o_orderstatus;
        this.o_totalprice = o_totalprice;
        this.o_orderdate = o_orderdate;
        this.o_orderpriority = o_orderpriority;
        this.o_clerk = o_clerk;
        this.o_shippriority = o_shippriority;
        this.o_comment = o_comment;
        this.o_lineitems = o_lineitems;
    }
}
