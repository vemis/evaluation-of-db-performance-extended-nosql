package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;

import dev.morphia.annotations.*;
import dev.morphia.utils.IndexType;

import java.time.LocalDate;

@Entity
@Indexes(
        @Index(fields = @Field(value = "o_comment", type = IndexType.TEXT))
)
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
