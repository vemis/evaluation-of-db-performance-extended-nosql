package cz.cuni.mff.java.microservice.model.embedded;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;
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
