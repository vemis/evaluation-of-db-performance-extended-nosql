package cz.cuni.mff.mongodb_java.springdata_e.benchmarks;

import cz.cuni.mff.mongodb_java.springdata_e.model.CustomerEWithOrders;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;

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


}
