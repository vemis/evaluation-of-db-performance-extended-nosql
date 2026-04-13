package cz.cuni.mff.couchbase_java.springdata_e.models;

import com.couchbase.client.java.Cluster;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

import java.time.LocalDate;
import java.util.List;

@Document
@Scope("spring_scope_e")
@Collection("OrdersEWithLineitemsArrayAsTags")
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

    public static void createIndexes(Cluster cluster) {
        return;
    }
}
