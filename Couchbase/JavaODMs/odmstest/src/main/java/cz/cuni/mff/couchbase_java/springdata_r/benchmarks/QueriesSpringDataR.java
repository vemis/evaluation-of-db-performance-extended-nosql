package cz.cuni.mff.couchbase_java.springdata_r.benchmarks;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import cz.cuni.mff.couchbase_java.springdata_r.models.CustomerROrdersR;
import cz.cuni.mff.couchbase_java.springdata_r.models.LineitemR;
import cz.cuni.mff.couchbase_java.springdata_r.models.OrdersR;
import cz.cuni.mff.couchbase_java.springdata_r.repositories.CustomerROrdersRRepository;
import cz.cuni.mff.couchbase_java.springdata_r.repositories.OrdersRRepository;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.ExecutableFindByQueryOperation;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import org.springframework.data.couchbase.core.ReactiveFindByQueryOperation;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.QueryCriteria;


import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class QueriesSpringDataR {
    /**
     * A1) Non-Indexed Columns
     *
     * This query selects all records from the lineitem table
     * ```sql
     *         SELECT * FROM lineitem;
     * ```
     */
    public static List<LineitemR> A1(ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        //QueryOptions queryOptions = QueryOptions.queryOptions().timeout(Duration.ofMinutes(5));

        List<LineitemR> a1 = reactiveCouchbaseTemplate
                .findByQuery(LineitemR.class)
                //.withOptions(queryOptions) // Because it takes too long
                .all().toStream().toList();
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
     */
    public static List<OrdersR> A2(ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        Query a2 = Query.query(
                QueryCriteria
                        .where("o_orderdate")
                        .between( LocalDate.parse("1996-01-01"), LocalDate.parse("1996-12-31"))
        );


        return reactiveCouchbaseTemplate
                .findByQuery(OrdersR.class)
                .matching(a2)
                .all()
                .toStream().toList();
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
    public static List<JsonObject> B1(OrdersRRepository  ordersRRepository) {
        return ordersRRepository.countOrdersByMonthAsJson();
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
    public static List<CustomerROrdersR> C2(CustomerROrdersRRepository customerROrdersRRepository) {
        return customerROrdersRRepository.findCustomerOrders();
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
     */
    public static List<JsonObject> D1(Cluster cluster){
        String query =
         "SELECT c.c_nationkey AS nationkey" +
        " FROM spring_bucket_r.spring_scope_r.CustomerR c" +
        " UNION" +
        " SELECT s.s_nationkey AS nationkey" +
        " FROM spring_bucket_r.spring_scope_r.SupplierR s";

        return cluster
                .query(query)
                .rowsAsObject();
    }
}
