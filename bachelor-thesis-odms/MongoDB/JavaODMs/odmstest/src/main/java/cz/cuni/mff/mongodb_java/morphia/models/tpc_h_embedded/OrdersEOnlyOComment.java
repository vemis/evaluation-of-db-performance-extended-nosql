package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

import java.time.LocalDate;

@Entity
public class OrdersEOnlyOComment {
    @Id
    private int o_orderkey;

    private LocalDate o_orderdate;

    private String o_comment;

    public OrdersEOnlyOComment() {}

    public OrdersEOnlyOComment(int o_orderkey, LocalDate o_orderdate, String o_comment) {
        this.o_orderkey = o_orderkey;
        this.o_orderdate = o_orderdate;
        this.o_comment = o_comment;
    }
}
