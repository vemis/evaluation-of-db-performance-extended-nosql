package cz.cuni.mff.mongodb_java.springdata_e.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "ordersEWithLineitemsArrayAsTagsIndexed")
public class OrdersEWithLineitemsArrayAsTagsIndexed {
    @Id
    private int o_orderkey;

    private LocalDate o_orderdate;

    @Indexed
    private List<Object> o_lineitems_tags_indexed;

    public OrdersEWithLineitemsArrayAsTagsIndexed() {}

    public OrdersEWithLineitemsArrayAsTagsIndexed(int o_orderkey, LocalDate o_orderdate, List<Object> o_lineitems_tags_indexed) {
        this.o_orderkey = o_orderkey;
        this.o_orderdate = o_orderdate;
        this.o_lineitems_tags_indexed = o_lineitems_tags_indexed;
    }
}
