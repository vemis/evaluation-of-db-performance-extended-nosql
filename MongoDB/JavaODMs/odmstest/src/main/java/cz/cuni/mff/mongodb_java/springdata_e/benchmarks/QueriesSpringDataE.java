package cz.cuni.mff.mongodb_java.springdata_e.benchmarks;

import cz.cuni.mff.mongodb_java.springdata_e.model.CustomerEWithOrders;
import cz.cuni.mff.mongodb_java.springdata_e.model.OrdersEOnlyOComment;
import cz.cuni.mff.mongodb_java.springdata_e.model.OrdersEOnlyOCommentIndexed;
import cz.cuni.mff.mongodb_java.springdata_e.model.OrdersEWithLineitems;
import cz.cuni.mff.mongodb_java.springdata_e.model.OrdersEWithLineitemsArrayAsTags;
import cz.cuni.mff.mongodb_java.springdata_e.model.OrdersEWithLineitemsArrayAsTagsIndexed;
import cz.cuni.mff.mongodb_java.springdata_e.model.OrdersEWithCustomerWithNationWithRegion;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class QueriesSpringDataE {


    /**
     * ### C2) Indexed Columns
     *
     * This query gives customer names, order dates, and total prices for all customers
     * ```sql
     * SELECT c.c_name, o.o_orderdate, o.o_totalprice
     * FROM customer c
     * JOIN orders o ON c.c_custkey = o.o_custkey;
     * ```
     */
    public static List<Document> C2(MongoTemplate mongoTemplate) {
        UnwindOperation unwindOrders = unwind("orders");

        ProjectionOperation projectFields = project()
                .and("c_name").as("c_name")
                .and("orders.o_orderdate").as("o_orderdate")
                .and("orders.o_totalprice").as("o_totalprice");

        Aggregation aggregation = newAggregation(
                unwindOrders,
                projectFields
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, CustomerEWithOrders.class, Document.class);

        return results.getMappedResults();
    }

    /**
     * ### R1) Embedded Orders with Lineitems Query
     *
     * Test performance of fetching nested documents (1:N relationship embedded).
     * ```MongoDB
     * db.ordersEWithLineitems.aggregate([
     *   { $match: { "o_lineitems.l_quantity": { $gt: 5 } } },
     *   { $project: { o_orderdate: 1, "o_lineitems.l_partkey": 1 } }
     * ])
     * ```
     */
    public static List<Document> R1(MongoTemplate mongoTemplate) {
        MatchOperation match = match(Criteria.where("o_lineitems.l_quantity").gt(5));

        ProjectionOperation project = project("o_orderdate", "o_lineitems.l_partkey");

        Aggregation aggregation = newAggregation(match, project);

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, OrdersEWithLineitems.class, Document.class);

        return results.getMappedResults();
    }

    /**
     * ### R2) Embedded Orders with Lineitems Query — Indexed Field
     *
     * Test performance of fetching nested documents (1:N relationship embedded) on indexed field.
     * ```MongoDB
     * db.ordersEWithLineitems.aggregate([
     *   { $match: { "o_lineitems.l_partkey": { $gt: 20000 } } },
     *   { $project: { o_orderdate: 1, "o_lineitems.l_partkey": 1 } }
     * ])
     * ```
     */
    public static List<Document> R2(MongoTemplate mongoTemplate) {
        MatchOperation match = match(Criteria.where("o_lineitems.l_partkey").gt(20000));

        ProjectionOperation project = project("o_orderdate", "o_lineitems.l_partkey");

        Aggregation aggregation = newAggregation(match, project);

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, OrdersEWithLineitems.class, Document.class);

        return results.getMappedResults();
    }

    /**
     * ### R3) Array Tags Query — Find Orders by Tag
     *
     * Test array indexing and filtering. Finds orders whose o_lineitems_tags array contains the value "MAIL".
     * ```MongoDB
     * db.ordersEWithLineitemsArrayAsTags.find(
     *   { o_lineitems_tags: "MAIL" },
     *   { o_orderdate: 1, o_lineitems_tags: 1 }
     * )
     * ```
     */
    public static List<OrdersEWithLineitemsArrayAsTags> R3(MongoTemplate mongoTemplate) {
        Query query = new Query(Criteria.where("o_lineitems_tags").is("MAIL"));
        query.fields().include("o_orderdate").include("o_lineitems_tags");

        return mongoTemplate.find(query, OrdersEWithLineitemsArrayAsTags.class);
    }

    /**
     * ### R4) Indexed Array Tags Query — Find Orders by Tag
     *
     * Test array indexing and filtering on an indexed field. Finds orders whose o_lineitems_tags_indexed array contains the value "MAIL".
     * ```MongoDB
     * db.ordersEWithLineitemsArrayAsTagsIndexed.find(
     *   { o_lineitems_tags_indexed: "MAIL" },
     *   { o_orderdate: 1, o_lineitems_tags_indexed: 1 }
     * )
     * ```
     */
    public static List<OrdersEWithLineitemsArrayAsTagsIndexed> R4(MongoTemplate mongoTemplate) {
        Query query = new Query(Criteria.where("o_lineitems_tags_indexed").is("MAIL"));
        query.fields().include("o_orderdate").include("o_lineitems_tags_indexed");

        return mongoTemplate.find(query, OrdersEWithLineitemsArrayAsTagsIndexed.class);
    }

    /**
     * ### R5) Embedded Customer with Nation with Region — Filter by Region Name
     *
     * Test denormalization vs join simulation in documents.
     * Find all orders from customers in "AMERICA".
     * ```MongoDB
     * db.ordersEWithCustomerWithNationWithRegion.find(
     *   { "o_customer.c_nation.n_region.r_name": "AMERICA" }
     * )
     * ```
     */
    public static List<OrdersEWithCustomerWithNationWithRegion> R5(MongoTemplate mongoTemplate) {
        Query query = new Query(Criteria.where("o_customer.c_nation.n_region.r_name").is("AMERICA"));

        return mongoTemplate.find(query, OrdersEWithCustomerWithNationWithRegion.class);
    }

    /**
     * ### R6) Regex Text Search on Comment Field
     *
     * Simulate text search without an index.
     * ```MongoDB
     * db.ordersEOnlyOComment.find({ o_comment: /furiously/i })
     * ```
     */
    public static List<OrdersEOnlyOComment> R6(MongoTemplate mongoTemplate) {
        Query query = new Query(Criteria.where("o_comment").regex("furiously", "i"));

        return mongoTemplate.find(query, OrdersEOnlyOComment.class);
    }

    /**
     * ### R7) Text Index Search on Comment Field
     *
     * Simulate text search with a text index.
     * ```MongoDB
     * db.ordersEOnlyOCommentIndexed.find({ $text: { $search: "furiously" } })
     * ```
     */
    public static List<OrdersEOnlyOCommentIndexed> R7(MongoTemplate mongoTemplate) {
        TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matching("furiously");
        Query query = TextQuery.queryText(textCriteria);

        return mongoTemplate.find(query, OrdersEOnlyOCommentIndexed.class);
    }

    /**
     * ### R8) Unwind Embedded Lineitems
     *
     * Test unwind of embedded objects (array flattening cost).
     * ```MongoDB
     * db.ordersEWithLineitems.aggregate([
     *   { $unwind: "$o_lineitems" },
     *   { $project: { _id: 1, "o_lineitems.l_partkey": 1 } }
     * ])
     * ```
     */
    public static List<Document> R8(MongoTemplate mongoTemplate) {
        UnwindOperation unwind = unwind("o_lineitems");

        ProjectionOperation project = project("o_lineitems.l_partkey");

        Aggregation aggregation = newAggregation(unwind, project);

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, OrdersEWithLineitems.class, Document.class);

        return results.getMappedResults();
    }

    /**
     * ### R9) Aggregation on Embedded Array — Sum Revenue per Order
     *
     * Test aggregation on embedded arrays ($unwind + $group interaction).
     * ```MongoDB
     * db.ordersEWithLineitems.aggregate([
     *   { $unwind: "$o_lineitems" },
     *   {
     *     $group: {
     *       _id: "$_id",
     *       totalRevenue: { $sum: "$o_lineitems.l_extendedprice" }
     *     }
     *   }
     * ])
     * ```
     */
    public static List<Document> R9(MongoTemplate mongoTemplate) {
        UnwindOperation unwind = unwind("o_lineitems");

        GroupOperation group = group("_id")
                .sum("o_lineitems.l_extendedprice").as("totalRevenue");

        Aggregation aggregation = newAggregation(unwind, group);

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, OrdersEWithLineitems.class, Document.class);

        return results.getMappedResults();
    }


}
