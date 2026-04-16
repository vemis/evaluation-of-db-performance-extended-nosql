package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;

import dev.morphia.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class OrdersEWithLineitemsArrayAsTags {
    @Id
    private int o_orderkey;

    private LocalDate o_orderdate;

    List<Object> o_lineitems_tags;

    public OrdersEWithLineitemsArrayAsTags() {}

    public OrdersEWithLineitemsArrayAsTags(int o_orderkey, LocalDate o_orderdate, List<Object> o_lineitems_tags) {
        this.o_orderkey = o_orderkey;

        this.o_orderdate = o_orderdate;

        this.o_lineitems_tags = o_lineitems_tags;

    }
}
