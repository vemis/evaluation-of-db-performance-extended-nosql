package cz.cuni.mff.couchbase_java.springdata_e.models;

import com.couchbase.client.java.Cluster;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

import java.time.LocalDate;

@Document
@Scope("spring_scope_e")
@Collection("OrdersEOnlyOComment")
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

    public static void createIndexes(Cluster cluster) {
        return;
    }
}
