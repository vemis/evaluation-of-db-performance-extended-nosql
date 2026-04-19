package cz.cuni.mff.java.microservice.repository;

import cz.cuni.mff.java.microservice.model.embedded.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class EmbeddedQueryRepository {

    private final MongoTemplate mongoTemplate;

    public EmbeddedQueryRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * R1) Embedded Orders with Lineitems — Filter by l_quantity
     * <pre>
     * db.ordersEWithLineitems.aggregate([
     *   { $match: { "o_lineitems.l_quantity": { $gt: 5 } } },
     *   { $project: { o_orderdate: 1, "o_lineitems.l_partkey": 1 } }
     * ])
     * </pre>
     */
    public List<Document> r1() {
        MatchOperation match = match(Criteria.where("o_lineitems.l_quantity").gt(5));
        ProjectionOperation project = project("o_orderdate", "o_lineitems.l_partkey");
        Aggregation aggregation = newAggregation(match, project);
        return mongoTemplate.aggregate(aggregation, OrdersEWithLineitems.class, Document.class).getMappedResults();
    }

    /**
     * R2) Embedded Orders with Lineitems — Filter by indexed l_partkey
     * <pre>
     * db.ordersEWithLineitems.aggregate([
     *   { $match: { "o_lineitems.l_partkey": { $gt: 20000 } } },
     *   { $project: { o_orderdate: 1, "o_lineitems.l_partkey": 1 } }
     * ])
     * </pre>
     */
    public List<Document> r2() {
        MatchOperation match = match(Criteria.where("o_lineitems.l_partkey").gt(20000));
        ProjectionOperation project = project("o_orderdate", "o_lineitems.l_partkey");
        Aggregation aggregation = newAggregation(match, project);
        return mongoTemplate.aggregate(aggregation, OrdersEWithLineitems.class, Document.class).getMappedResults();
    }

    /**
     * R3) Array Tags Query — Find Orders by Tag
     * <pre>
     * db.ordersEWithLineitemsArrayAsTags.find(
     *   { o_lineitems_tags: "MAIL" },
     *   { o_orderdate: 1, o_lineitems_tags: 1 }
     * )
     * </pre>
     */
    public List<OrdersEWithLineitemsArrayAsTags> r3() {
        Query query = new Query(Criteria.where("o_lineitems_tags").is("MAIL"));
        query.fields().include("o_orderdate").include("o_lineitems_tags");
        return mongoTemplate.find(query, OrdersEWithLineitemsArrayAsTags.class);
    }

    /**
     * R4) Indexed Array Tags Query — Find Orders by Tag
     * <pre>
     * db.ordersEWithLineitemsArrayAsTagsIndexed.find(
     *   { o_lineitems_tags_indexed: "MAIL" },
     *   { o_orderdate: 1, o_lineitems_tags_indexed: 1 }
     * )
     * </pre>
     */
    public List<OrdersEWithLineitemsArrayAsTagsIndexed> r4() {
        Query query = new Query(Criteria.where("o_lineitems_tags_indexed").is("MAIL"));
        query.fields().include("o_orderdate").include("o_lineitems_tags_indexed");
        return mongoTemplate.find(query, OrdersEWithLineitemsArrayAsTagsIndexed.class);
    }

    /**
     * R5) Embedded Customer with Nation with Region — Filter by Region Name
     * <pre>
     * db.ordersEWithCustomerWithNationWithRegion.find(
     *   { "o_customer.c_nation.n_region.r_name": "AMERICA" }
     * )
     * </pre>
     */
    public List<OrdersEWithCustomerWithNationWithRegion> r5() {
        Query query = new Query(Criteria.where("o_customer.c_nation.n_region.r_name").is("AMERICA"));
        return mongoTemplate.find(query, OrdersEWithCustomerWithNationWithRegion.class);
    }

    /**
     * R6) Regex Text Search on Comment Field (no index)
     * <pre>
     * db.ordersEOnlyOComment.find({ o_comment: /furiously/i })
     * </pre>
     */
    public List<OrdersEOnlyOComment> r6() {
        Query query = new Query(Criteria.where("o_comment").regex("furiously", "i"));
        return mongoTemplate.find(query, OrdersEOnlyOComment.class);
    }

    /**
     * R7) Text Index Search on Comment Field
     * <pre>
     * db.ordersEOnlyOCommentIndexed.find({ $text: { $search: "furiously" } })
     * </pre>
     */
    public List<OrdersEOnlyOCommentIndexed> r7() {
        TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matching("furiously");
        Query query = TextQuery.queryText(textCriteria);
        return mongoTemplate.find(query, OrdersEOnlyOCommentIndexed.class);
    }

    /**
     * R8) Unwind Embedded Lineitems
     * <pre>
     * db.ordersEWithLineitems.aggregate([
     *   { $unwind: "$o_lineitems" },
     *   { $project: { _id: 1, "o_lineitems.l_partkey": 1 } }
     * ])
     * </pre>
     */
    public List<Document> r8() {
        UnwindOperation unwind = unwind("o_lineitems");
        ProjectionOperation project = project("o_lineitems.l_partkey");
        Aggregation aggregation = newAggregation(unwind, project);
        return mongoTemplate.aggregate(aggregation, OrdersEWithLineitems.class, Document.class).getMappedResults();
    }

    /**
     * R9) Aggregation on Embedded Array — Sum Revenue per Order
     * <pre>
     * db.ordersEWithLineitems.aggregate([
     *   { $unwind: "$o_lineitems" },
     *   { $group: { _id: "$_id", totalRevenue: { $sum: "$o_lineitems.l_extendedprice" } } }
     * ])
     * </pre>
     */
    public List<Document> r9() {
        UnwindOperation unwind = unwind("o_lineitems");
        GroupOperation group = group("_id").sum("o_lineitems.l_extendedprice").as("totalRevenue");
        Aggregation aggregation = newAggregation(unwind, group);
        return mongoTemplate.aggregate(aggregation, OrdersEWithLineitems.class, Document.class).getMappedResults();
    }
}
