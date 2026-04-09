package cz.cuni.mff.mongodb_java.morphia.benchmarks;

import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.CustomerEWithOrders;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.OrdersEOnlyOComment;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.OrdersEOnlyOCommentIndexed;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.OrdersEWithCustomerWithNationWithRegion;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.OrdersEWithLineitems;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.OrdersEWithLineitemsArrayAsTags;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.OrdersEWithLineitemsArrayAsTagsIndexed;
import dev.morphia.Datastore;
import dev.morphia.aggregation.expressions.AccumulatorExpressions;
import dev.morphia.aggregation.expressions.Expressions;
import dev.morphia.aggregation.stages.Group;
import dev.morphia.aggregation.stages.Projection;
import dev.morphia.aggregation.stages.Unwind;
import dev.morphia.query.FindOptions;
import dev.morphia.query.filters.Filters;
import org.bson.Document;

import java.util.List;

public class QueriesMorphiaE {
    /**
     * ### C2)
     *
     * Retrieve fields from an embedded document inside an array.
     * ```MongoDB
     * db.customerEWithOrders.aggregate([
     *   {
     *     $unwind: "$orders"
     *   },
     *   {
     *     $project: {
     *       c_name: 1,
     *       o_orderdate: "$orders.o_orderdate",
     *       o_totalprice: "$orders.o_totalprice"
     *     }
     *   }
     * ])
     * ```
     */
    public static List<Document> C2(Datastore datastore) {
        List<Document> results = datastore.aggregate(CustomerEWithOrders.class)
                .unwind(Unwind.unwind("orders") )
                .project(Projection.project()
                        .include("c_name")
                        .include("o_orderdate", Expressions.field("orders.o_orderdate"))
                        .include("o_totalprice", Expressions.field("orders.o_totalprice"))
                )
                .execute(Document.class)
                .toList();

        return results;
    }

    /**
     * ### R1) Embedded Customer with Orders Query
     *
     * Test performance of fetching nested documents (1:N relationship embedded).
     * ```MongoDB
     * db.customerEWithOrders.aggregate([
     *   { $match: { "orders.o_totalprice": { $gt: 259276 } } },
     *   { $project: { c_name: 1, "orders.o_totalprice": 1 } }
     * ])
     * ```
     */
    public static List<Document> R1_Customer_Deprecated(Datastore datastore) {
        List<Document> results = datastore.aggregate(CustomerEWithOrders.class)
                .match(Filters.gt("orders.o_totalprice", 259276))
                .project(Projection.project()
                        .include("c_name")
                        .include("orders.o_totalprice")
                )
                .execute(Document.class)
                .toList();

        return results;
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
    public static List<Document> R1(Datastore datastore) {
        List<Document> results = datastore.aggregate(OrdersEWithLineitems.class)
                .match(Filters.gt("o_lineitems.l_quantity", 5))
                .project(Projection.project()
                        .include("o_orderdate")
                        .include("o_lineitems.l_partkey")
                )
                .execute(Document.class)
                .toList();

        return results;
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
    public static List<Document> R2(Datastore datastore) {
        List<Document> results = datastore.aggregate(OrdersEWithLineitems.class)
                .match(Filters.gt("o_lineitems.l_partkey", 20000))
                .project(Projection.project()
                        .include("o_orderdate")
                        .include("o_lineitems.l_partkey")
                )
                .execute(Document.class)
                .toList();

        return results;
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
    public static List<OrdersEWithLineitemsArrayAsTags> R3(Datastore datastore) {
        List<OrdersEWithLineitemsArrayAsTags> results = datastore.find(OrdersEWithLineitemsArrayAsTags.class)
                .filter(Filters.eq("o_lineitems_tags", "MAIL"))
                .iterator(new dev.morphia.query.FindOptions()
                        .projection().include("o_orderdate", "o_lineitems_tags"))
                .toList();

        return results;
    }

    /**
     * ### R4) Indexed Array Tags Query — Find Orders by Tag
     *
     * Test array indexing and filtering. Finds orders whose o_lineitems_tags array contains the value "MAIL".
     * ```MongoDB
     * db.ordersEWithLineitemsArrayAsTags.find(
     *   { o_lineitems_tags: "MAIL" },
     *   { o_orderdate: 1, o_lineitems_tags: 1 }
     * )
     * ```
     */
    public static List<OrdersEWithLineitemsArrayAsTagsIndexed> R4(Datastore datastore) {
        List<OrdersEWithLineitemsArrayAsTagsIndexed> results = datastore.find(OrdersEWithLineitemsArrayAsTagsIndexed.class)
                .filter(Filters.eq("o_lineitems_tags_indexed", "MAIL"))
                .iterator(new FindOptions()
                        .projection().include("o_orderdate", "o_lineitems_tags_indexed"))
                .toList();

        return results;
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
    public static List<OrdersEWithCustomerWithNationWithRegion> R5(Datastore datastore) {
        List<OrdersEWithCustomerWithNationWithRegion> results = datastore
                .find(OrdersEWithCustomerWithNationWithRegion.class)
                .filter(Filters.eq("o_customer.c_nation.n_region.r_name", "AMERICA"))
                .iterator()
                .toList();

        return results;
    }

    /**
     * ### R6) Regex Text Search on Comment Field
     *
     * Simulate text search without an index.
     * ```MongoDB
     * db.ordersEOnlyOComment.find({ o_comment: /furiously/i })
     * ```
     */
    public static List<OrdersEOnlyOComment> R6(Datastore datastore) {
        List<OrdersEOnlyOComment> results = datastore
                .find(OrdersEOnlyOComment.class)
                .filter(Filters.regex("o_comment", "furiously").caseInsensitive())
                .iterator()
                .toList();

        return results;
    }

    /**
     * ### R7) Text Index Search on Comment Field
     *
     * Simulate text search with a text index.
     * ```MongoDB
     * db.ordersEOnlyOCommentIndexed.find({ $text: { $search: "furiously" } })
     * ```
     */
    public static List<OrdersEOnlyOCommentIndexed> R7(Datastore datastore) {
        List<OrdersEOnlyOCommentIndexed> results = datastore
                .find(OrdersEOnlyOCommentIndexed.class)
                .filter(Filters.text("furiously"))
                .iterator()
                .toList();

        return results;
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
    public static List<Document> R8(Datastore datastore) {
        List<Document> results = datastore.aggregate(OrdersEWithLineitems.class)
                .unwind(Unwind.unwind("o_lineitems"))
                .project(Projection.project()
                        .include("o_lineitems.l_partkey")
                )
                .execute(Document.class)
                .toList();

        return results;
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
    public static List<Document> R9(Datastore datastore) {
        List<Document> results = datastore.aggregate(OrdersEWithLineitems.class)
                .unwind(Unwind.unwind("o_lineitems"))
                .group(
                        Group.group(Group.id(Expressions.field("_id")))
                                .field("totalRevenue", AccumulatorExpressions.sum(Expressions.field("o_lineitems.l_extendedprice")))
                )
                .execute(Document.class)
                .toList();

        return results;
    }
}
