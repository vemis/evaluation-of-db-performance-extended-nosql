package cz.cuni.mff.java.microservice.repository;

import cz.cuni.mff.java.microservice.models.embedded.*;
import dev.morphia.Datastore;
import dev.morphia.aggregation.expressions.AccumulatorExpressions;
import dev.morphia.aggregation.expressions.Expressions;
import dev.morphia.aggregation.stages.Group;
import dev.morphia.aggregation.stages.Projection;
import dev.morphia.aggregation.stages.Unwind;
import dev.morphia.query.FindOptions;
import dev.morphia.query.filters.Filters;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmbeddedQueryRepository {

    private final Datastore datastore;

    public EmbeddedQueryRepository(Datastore datastore) {
        this.datastore = datastore;
    }

    // R1) Filter on embedded lineitem array — non-indexed field (l_quantity > 5)
    public List<Document> r1() {
        return datastore.aggregate(OrdersEWithLineitems.class)
                .match(Filters.gt("o_lineitems.l_quantity", 5))
                .project(Projection.project()
                        .include("o_orderdate")
                        .include("o_lineitems.l_partkey"))
                .execute(Document.class)
                .toList();
    }

    // R2) Filter on embedded lineitem array — indexed field (l_partkey > 20000)
    public List<Document> r2() {
        return datastore.aggregate(OrdersEWithLineitems.class)
                .match(Filters.gt("o_lineitems.l_partkey", 20000))
                .project(Projection.project()
                        .include("o_orderdate")
                        .include("o_lineitems.l_partkey"))
                .execute(Document.class)
                .toList();
    }

    // R3) Array tags filter — no index (shipmode tag "MAIL")
    public List<OrdersEWithLineitemsArrayAsTags> r3() {
        return datastore.find(OrdersEWithLineitemsArrayAsTags.class)
                .filter(Filters.eq("o_lineitems_tags", "MAIL"))
                .iterator(new FindOptions()
                        .projection().include("o_orderdate", "o_lineitems_tags"))
                .toList();
    }

    // R4) Array tags filter — indexed field
    public List<OrdersEWithLineitemsArrayAsTagsIndexed> r4() {
        return datastore.find(OrdersEWithLineitemsArrayAsTagsIndexed.class)
                .filter(Filters.eq("o_lineitems_tags_indexed", "MAIL"))
                .iterator(new FindOptions()
                        .projection().include("o_orderdate", "o_lineitems_tags_indexed"))
                .toList();
    }

    // R5) Deeply nested document filter — region name "AMERICA"
    public List<OrdersEWithCustomerWithNationWithRegion> r5() {
        return datastore.find(OrdersEWithCustomerWithNationWithRegion.class)
                .filter(Filters.eq("o_customer.c_nation.n_region.r_name", "AMERICA"))
                .iterator()
                .toList();
    }

    // R6) Regex text search — no text index
    public List<OrdersEOnlyOComment> r6() {
        return datastore.find(OrdersEOnlyOComment.class)
                .filter(Filters.regex("o_comment", "furiously").caseInsensitive())
                .iterator()
                .toList();
    }

    // R7) Text index search
    public List<OrdersEOnlyOCommentIndexed> r7() {
        return datastore.find(OrdersEOnlyOCommentIndexed.class)
                .filter(Filters.text("furiously"))
                .iterator()
                .toList();
    }

    // R8) Unwind embedded array (array flattening cost)
    public List<Document> r8() {
        return datastore.aggregate(OrdersEWithLineitems.class)
                .unwind(Unwind.unwind("o_lineitems"))
                .project(Projection.project()
                        .include("o_lineitems.l_partkey"))
                .execute(Document.class)
                .toList();
    }

    // R9) Aggregation on embedded array — sum revenue per order
    public List<Document> r9() {
        return datastore.aggregate(OrdersEWithLineitems.class)
                .unwind(Unwind.unwind("o_lineitems"))
                .group(Group.group(Group.id(Expressions.field("_id")))
                        .field("totalRevenue", AccumulatorExpressions.sum(
                                Expressions.field("o_lineitems.l_extendedprice"))))
                .execute(Document.class)
                .toList();
    }
}
