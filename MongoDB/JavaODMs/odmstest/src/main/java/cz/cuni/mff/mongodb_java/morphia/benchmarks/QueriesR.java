package cz.cuni.mff.mongodb_java.morphia.benchmarks;

import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.CustomerR;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.LineitemR;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.OrdersR;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.SupplierR;
import dev.morphia.Datastore;
import dev.morphia.aggregation.expressions.DateExpressions;
import dev.morphia.aggregation.expressions.Expressions;
import dev.morphia.aggregation.stages.Group;
import dev.morphia.aggregation.expressions.AccumulatorExpressions;
import dev.morphia.aggregation.stages.Lookup;
import dev.morphia.aggregation.stages.Projection;
import dev.morphia.aggregation.stages.Set;


import dev.morphia.aggregation.stages.Unwind;
import org.bson.Document;

import java.time.LocalDate;
import java.util.List;

import static dev.morphia.query.filters.Filters.*;

public class QueriesR {
    /**
     * A1) Non-Indexed Columns
     *
     * This query selects all records from the lineitem table
     * ```sql
     *         SELECT * FROM lineitem;
     * ```
     */
    public static List<LineitemR> A1(Datastore datastore) {
        List<LineitemR> a1 = datastore
                .find(LineitemR.class)
                .iterator()
                .toList();

        return a1;
    }

    /**
     * A2) Non-Indexed Columns — Range Query
     *
     * This query selects all records from the orders table where the order date is between '1996-01-01' and '1996-12-31'
     * ```sql
     * SELECT * FROM orders
     * WHERE o_orderdate
     *     BETWEEN '1996-01-01' AND '1996-12-31';
     * ```
     * @param datastore
     * @return
     */
    public static List<OrdersR> A2(Datastore datastore) {
        List<OrdersR> a2 = datastore
                .find(OrdersR.class)
                .filter(gte("o_orderdate", LocalDate.parse("1996-01-01")), lte("o_orderdate", LocalDate.parse("1996-12-31")))
                .iterator()
                .toList();

        return a2;
    }

    /**
     * ### B1) COUNT
     *
     * This query counts the number of orders grouped by order month
     * ```sql
     * SELECT COUNT(o.o_orderkey) AS order_count,
     *        DATE_FORMAT(o.o_orderdate, '%Y-%m') AS order_month
     * FROM orders o
     * GROUP BY order_month;
     * ```
     */
    public static List<Document> B1(Datastore datastore) {

         List<Document> aggregation = datastore.aggregate(OrdersR.class)
                .group(
                        Group.group(Group.id(

                                        DateExpressions.dateToString()
                                                .format("%Y-%m")
                                                .date(Expressions.field("o_orderdate"))

                                )
                        ).field("order_count", AccumulatorExpressions.sum(Expressions.value(1)))
                )
                 .project(
                         Projection.project()
                                 .suppressId()
                                .include("order_count")
                                .include("order_month", Expressions.field("_id"))
                )
                .execute(Document.class)
                .toList();

         return aggregation;
    }

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
    public static List<Document> C2(Datastore datastore) {
        List<Document> c2 = datastore.aggregate(CustomerR.class)
                .lookup(
                        Lookup.lookup(OrdersR.class)
                                .localField("_id")
                                .foreignField("o_custkey")
                                .as("ordersR")

                )
                .unwind(Unwind.unwind("ordersR"))
                .project(Projection.project()
                        .suppressId()
                        .include("c_name")
                        .include("ordersR.o_orderdate")
                        .include("ordersR.o_totalprice")
                )
                .execute(Document.class)
                .toList();

        return c2;
    }

    /**
     * ### D1) UNION
     *
     * This query combines customer and supplier nation keys
     * ```sql
     * (SELECT c_nationkey FROM customer)
     * UNION
     * (SELECT s_nationkey FROM supplier);
     * ```
     * @param datastore
     */
    public static List<Document> D1(Datastore datastore) {
        List<Document> result = datastore.aggregate(CustomerR.class)
                // SELECT c_nationkey AS nationkey
                .project(
                        Projection.project()
                                .include("nationkey", Expressions.field("c_nationkey"))
                                .exclude("_id")
                ) //size() 30_000

                // UNION supplier
                .unionWith(SupplierR.class,
                        Projection.project()
                                .include("nationkey", Expressions.field("s_nationkey"))
                                .exclude("_id")
                ) //size() 32_000

                // Remove duplicates (SQL UNION behavior)
                .group(
                        Group.group(
                                Group.id(
                                        Expressions.field("nationkey")
                                )

                        )
                ) //25

                // Final reshape
                .project(
                        Projection.project()
                                .include("nationkey", Expressions.field("_id"))
                                .exclude("_id")
                )

                .execute(Document.class)
                .toList();

                return result;
    }
}
