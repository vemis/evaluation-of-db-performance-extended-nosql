package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;

import dev.morphia.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Indexes({
        @Index(fields = @Field(value = "o_lineitems.l_id")),
        @Index(fields = @Field(value = "o_lineitems.l_orderkey")),
        @Index(fields = @Field(value = "o_lineitems.l_partkey")),
        @Index(fields = @Field(value = "o_lineitems.l_suppkey")),
        @Index(fields = @Field(value = "o_lineitems.l_ps_id"))
})
public class OrdersEWithLineitems {
    @Id
    private int o_orderkey;
    @Indexed
    private int o_custkey;
    private String o_orderstatus;
    private String o_totalprice;
    private LocalDate o_orderdate;
    private String o_orderpriority;
    private String o_clerk;
    private String o_shippriority;
    private String o_comment;

    @Property
    List<LineitemE> o_lineitems;

    public OrdersEWithLineitems() {}

    public OrdersEWithLineitems(int o_orderkey, int o_custkey, String o_orderstatus, String o_totalprice, LocalDate o_orderdate, String o_orderpriority, String o_clerk, String o_shippriority, String o_comment, List<LineitemE> o_lineitems) {
        this.o_orderkey = o_orderkey;
        this.o_custkey = o_custkey;
        this.o_orderstatus = o_orderstatus;
        this.o_orderdate = o_orderdate;
        this.o_orderpriority = o_orderpriority;
        this.o_clerk = o_clerk;
        this.o_shippriority = o_shippriority;
        this.o_totalprice = o_totalprice;
        this.o_comment = o_comment;
        this.o_lineitems = o_lineitems;

    }
}

