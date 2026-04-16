package cz.cuni.mff.mongodb_java.springdata_e.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "ordersEWithLineitemsArrayAsTags")
public class OrdersEWithLineitemsArrayAsTags {
    @Id
    private int o_orderkey;

    private LocalDate o_orderdate;

    private List<Object> o_lineitems_tags;

    public OrdersEWithLineitemsArrayAsTags() {}

    public OrdersEWithLineitemsArrayAsTags(int o_orderkey, LocalDate o_orderdate, List<Object> o_lineitems_tags) {
        this.o_orderkey = o_orderkey;
        this.o_orderdate = o_orderdate;
        this.o_lineitems_tags = o_lineitems_tags;
    }
}
