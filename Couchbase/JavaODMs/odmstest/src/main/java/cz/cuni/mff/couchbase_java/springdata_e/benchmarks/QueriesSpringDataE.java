package cz.cuni.mff.couchbase_java.springdata_e.benchmarks;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;

import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.QueryCriteria;

import java.time.LocalDate;
import java.util.List;

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
    public static List<JsonObject> C2(Cluster cluster) {
        String query =
         "SELECT" +
                " c.c_name," +
                " o.o_orderdate," +
                " o.o_totalprice" +
        " FROM ottoman_bucket_e.ottoman_scope_e.CustomerEWithOrders AS c" +
        " UNNEST c.c_orders AS o;";

        return cluster
                .query(query)
                .rowsAsObject();
    }

    /**
     * ### R1) Embedded Orders with Lineitems Query
     *
     * Test performance of fetching nested documents (1:N relationship embedded).
     * ```sql
     * SELECT o.o_orderdate,
     *        ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems
     * FROM spring_bucket_e.spring_scope_e.OrdersEWithLineitems AS o
     * WHERE ANY l IN o.o_lineitems SATISFIES l.l_quantity > 5 END
     * ```
     */
    public static List<JsonObject> R1(Cluster cluster) {
        String query =
                "SELECT o.o_orderdate," +
                " ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems" +
                " FROM spring_bucket_e.spring_scope_e.OrdersEWithLineitems AS o" +
                " WHERE ANY l IN o.o_lineitems SATISFIES l.l_quantity > 5 END";

        return cluster
                .query(query)
                .rowsAsObject();
    }

    /**
     * ### R2) Embedded Orders with Lineitems Query — Indexed Field
     *
     * Test performance of fetching nested documents (1:N relationship embedded) on indexed field.
     * Uses the array index idx_OrdersEWithLineitems_l_partkey on o_lineitems[].l_partkey.
     * ```sql
     * SELECT o.o_orderdate,
     *        ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems
     * FROM spring_bucket_e.spring_scope_e.OrdersEWithLineitems AS o
     * WHERE ANY l IN o.o_lineitems SATISFIES l.l_partkey > 20000 END
     * ```
     */
    public static List<JsonObject> R2(Cluster cluster) {
        String query =
                "SELECT o.o_orderdate," +
                " ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems" +
                " FROM spring_bucket_e.spring_scope_e.OrdersEWithLineitems AS o" +
                " WHERE ANY l IN o.o_lineitems SATISFIES l.l_partkey > 20000 END";

        return cluster
                .query(query)
                .rowsAsObject();
    }

    /**
     * ### R3) Array Tags Query — Find Orders by Tag
     *
     * Test array indexing and filtering. Finds orders whose o_lineitems_tags array contains the value "MAIL".
     * Uses the array index idx_OrdersEWithLineitemsArrayAsTags_tags on o_lineitems_tags[].
     * ```sql
     * SELECT o.o_orderdate, o.o_lineitems_tags
     * FROM spring_bucket_e.spring_scope_e.OrdersEWithLineitemsArrayAsTags AS o
     * WHERE ANY tag IN o.o_lineitems_tags SATISFIES tag = 'MAIL' END
     * ```
     */
    public static List<JsonObject> R3(Cluster cluster) {
        String query =
                "SELECT o.o_orderdate, o.o_lineitems_tags" +
                " FROM spring_bucket_e.spring_scope_e.OrdersEWithLineitemsArrayAsTags AS o" +
                " WHERE ANY tag IN o.o_lineitems_tags SATISFIES tag = 'MAIL' END";

        return cluster
                .query(query)
                .rowsAsObject();
    }

    /**
     * ### R4) Indexed Array Tags Query — Find Orders by Tag
     *
     * Test array indexing and filtering on an indexed field. Finds orders whose o_lineitems_tags_indexed array contains the value "MAIL".
     * Uses the array index idx_OrdersEWithLineitemsArrayAsTagsIndexed_tags on o_lineitems_tags_indexed[].
     * ```sql
     * SELECT o.o_orderdate, o.o_lineitems_tags_indexed
     * FROM spring_bucket_e.spring_scope_e.OrdersEWithLineitemsArrayAsTagsIndexed AS o
     * WHERE ANY tag IN o.o_lineitems_tags_indexed SATISFIES tag = 'MAIL' END
     * ```
     */
    public static List<JsonObject> R4(Cluster cluster) {
        String query =
                "SELECT o.o_orderdate, o.o_lineitems_tags_indexed" +
                " FROM spring_bucket_e.spring_scope_e.OrdersEWithLineitemsArrayAsTagsIndexed AS o" +
                " WHERE ANY tag IN o.o_lineitems_tags_indexed SATISFIES tag = 'MAIL' END";

        return cluster
                .query(query)
                .rowsAsObject();
    }
}
