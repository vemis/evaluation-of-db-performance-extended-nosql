package cz.cuni.mff.mongodb_java.morphia.benchmarks;

import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.CustomerEWithOrders;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.CustomerR;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.OrdersR;
import dev.morphia.Datastore;
import dev.morphia.aggregation.expressions.Expressions;
import dev.morphia.aggregation.stages.Lookup;
import dev.morphia.aggregation.stages.Projection;
import dev.morphia.aggregation.stages.Unwind;
import org.bson.Document;

import java.util.List;

public class QueriesMorphiaE {
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
}
