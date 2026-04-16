package cz.cuni.mff.mongodb_java.springdata_e.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "ordersEOnlyOCommentIndexed")
public class OrdersEOnlyOCommentIndexed {
    @Id
    private int o_orderkey;

    private LocalDate o_orderdate;

    @TextIndexed
    private String o_comment;

    public OrdersEOnlyOCommentIndexed() {}

    public OrdersEOnlyOCommentIndexed(int o_orderkey, LocalDate o_orderdate, String o_comment) {
        this.o_orderkey = o_orderkey;
        this.o_orderdate = o_orderdate;
        this.o_comment = o_comment;
    }
}
