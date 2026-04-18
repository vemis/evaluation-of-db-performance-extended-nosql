package cz.cuni.mff.java.microservice.model.embedded;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

import java.time.LocalDate;
import java.util.List;

@Document
@Scope("spring_scope_e")
@Collection("OrdersEWithLineitemsArrayAsTagsIndexed")
public class OrdersEWithLineitemsArrayAsTagsIndexed {

    @Id
    private int o_orderkey;
    private LocalDate o_orderdate;
    private List<Object> o_lineitems_tags_indexed;

    public OrdersEWithLineitemsArrayAsTagsIndexed() {}

    public OrdersEWithLineitemsArrayAsTagsIndexed(int o_orderkey, LocalDate o_orderdate, List<Object> o_lineitems_tags_indexed) {
        this.o_orderkey = o_orderkey;
        this.o_orderdate = o_orderdate;
        this.o_lineitems_tags_indexed = o_lineitems_tags_indexed;
    }
}
