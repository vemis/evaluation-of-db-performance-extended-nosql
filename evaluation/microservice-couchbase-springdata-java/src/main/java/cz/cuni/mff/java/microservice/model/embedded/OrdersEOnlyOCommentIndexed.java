package cz.cuni.mff.java.microservice.model.embedded;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

import java.time.LocalDate;

@Document
@Scope("spring_scope_e")
@Collection("OrdersEOnlyOCommentIndexed")
public class OrdersEOnlyOCommentIndexed {

    @Id
    private int o_orderkey;
    private LocalDate o_orderdate;
    private String o_comment;

    public OrdersEOnlyOCommentIndexed() {}

    public OrdersEOnlyOCommentIndexed(int o_orderkey, LocalDate o_orderdate, String o_comment) {
        this.o_orderkey = o_orderkey;
        this.o_orderdate = o_orderdate;
        this.o_comment = o_comment;
    }
}
