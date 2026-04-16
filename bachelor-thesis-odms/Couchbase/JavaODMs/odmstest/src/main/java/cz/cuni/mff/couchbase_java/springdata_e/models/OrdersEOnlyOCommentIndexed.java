package cz.cuni.mff.couchbase_java.springdata_e.models;

import com.couchbase.client.java.Cluster;
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

    public static void createIndexes(Cluster cluster) {
        // Couchbase does not have a MongoDB-style text index for N1QL.
        // A regular index on o_comment is created here; it accelerates equality/range lookups
        // but not REGEXP_CONTAINS with a non-anchored pattern.
        // For true full-text search acceleration an FTS (Full Text Search) index would be
        // required alongside the SEARCH() N1QL function, but that is a separate Couchbase service.
        String o_comment_index_query =
                "CREATE INDEX idx_OrdersEOnlyOCommentIndexed_o_comment IF NOT EXISTS" +
                " ON spring_bucket_e.spring_scope_e.OrdersEOnlyOCommentIndexed (o_comment)";
        cluster.query(o_comment_index_query);
    }
}
