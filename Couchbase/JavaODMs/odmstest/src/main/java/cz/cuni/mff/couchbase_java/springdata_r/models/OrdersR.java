package cz.cuni.mff.couchbase_java.springdata_r.models;

import com.couchbase.client.core.util.PreventsGarbageCollection;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

import java.time.LocalDate;

@Document
@Scope("spring_scope_r")
@Collection("OrdersR")
public class OrdersR {
    @Id
    private int o_orderkey;

    private int o_orderkey_field;

    private String o_custkey;
    private String o_orderstatus;
    private double o_totalprice;
    private LocalDate o_orderdate;
    private String o_orderpriority;
    private String o_clerk;
    private String o_shippriority;
    private String o_comment;

    public OrdersR() {}

    public OrdersR(int o_orderkey, String o_custkey, String o_orderstatus, double o_totalprice, LocalDate o_orderdate, String o_orderpriority, String o_clerk, String o_shippriority,  String o_comment) {
        this.o_orderkey = o_orderkey;
        this.o_orderkey_field = o_orderkey;
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
