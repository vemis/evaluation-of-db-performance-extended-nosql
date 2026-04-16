package cz.cuni.mff.mongodb_java.springdata_e.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "ordersEWithLineitems")
@CompoundIndexes({
        @CompoundIndex(name = "o_lineitems_l_id", def = "{'o_lineitems.l_id': 1}"),
        @CompoundIndex(name = "o_lineitems_l_orderkey", def = "{'o_lineitems.l_orderkey': 1}"),
        @CompoundIndex(name = "o_lineitems_l_partkey", def = "{'o_lineitems.l_partkey': 1}"),
        @CompoundIndex(name = "o_lineitems_l_suppkey", def = "{'o_lineitems.l_suppkey': 1}"),
        @CompoundIndex(name = "o_lineitems_l_ps_id", def = "{'o_lineitems.l_ps_id': 1}")
})
public class OrdersEWithLineitems {
    @Id
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
