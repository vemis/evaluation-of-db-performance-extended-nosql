package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import java.time.LocalDate;
import java.util.List;

@Entity
public class OrdersEWithLineitemsArrayAsTagsIndexed {
    @Id
    private int o_orderkey;

    private LocalDate o_orderdate;

    @Indexed
    List<Object> o_lineitems_tags_indexed;

    public OrdersEWithLineitemsArrayAsTagsIndexed() {}

    public OrdersEWithLineitemsArrayAsTagsIndexed(int o_orderkey, LocalDate o_orderdate, List<Object> o_lineitems_tags_indexed) {
        this.o_orderkey = o_orderkey;

        this.o_orderdate = o_orderdate;

        this.o_lineitems_tags_indexed = o_lineitems_tags_indexed;

    }
}
