package cz.cuni.mff.java.microservice.repository;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

/**
 * Embedded-model TPC-H queries implemented via Couchbase N1QL against
 * bucket-main / spring_scope_e.
 */
@Repository
public class EmbeddedQueryRepository {

    private static final String E = "`bucket-main`.`spring_scope_e`";

    private final Cluster cluster;

    public EmbeddedQueryRepository(Cluster cluster) {
        this.cluster = cluster;
    }

    /**
     * R1) Embedded Orders with Lineitems Query
     * <p>
     * Tests performance of fetching nested documents (1:N relationship embedded).
     * <pre>
     * SELECT o.o_orderdate, ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems
     * FROM OrdersEWithLineitems AS o
     * WHERE ANY l IN o.o_lineitems SATISFIES l.l_quantity &gt; 5 END
     * </pre>
     */
    public List<JsonObject> r1() {
        return cluster.query(
                "SELECT o.o_orderdate," +
                " ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems" +
                " FROM " + E + ".`OrdersEWithLineitems` AS o" +
                " WHERE ANY l IN o.o_lineitems SATISFIES l.l_quantity > 5 END")
                .rowsAsObject();
    }

    /**
     * R2) Embedded Orders with Lineitems Query — Indexed Field
     * <p>
     * Tests performance of fetching nested documents (1:N relationship embedded) on an indexed field.
     * <pre>
     * SELECT o.o_orderdate, ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems
     * FROM OrdersEWithLineitems AS o
     * WHERE ANY l IN o.o_lineitems SATISFIES l.l_partkey &gt; 20000 END
     * </pre>
     */
    public List<JsonObject> r2() {
        return cluster.query(
                "SELECT o.o_orderdate," +
                " ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems" +
                " FROM " + E + ".`OrdersEWithLineitems` AS o" +
                " WHERE ANY l IN o.o_lineitems SATISFIES l.l_partkey > 20000 END")
                .rowsAsObject();
    }

    /**
     * R3) Array Tags Query — Find Orders by Tag
     * <p>
     * Tests array indexing and filtering. Finds orders whose {@code o_lineitems_tags} array contains "MAIL".
     * <pre>
     * SELECT o.o_orderdate, o.o_lineitems_tags
     * FROM OrdersEWithLineitemsArrayAsTags AS o
     * WHERE ANY tag IN o.o_lineitems_tags SATISFIES tag = 'MAIL' END
     * </pre>
     */
    public List<JsonObject> r3() {
        return cluster.query(
                "SELECT o.o_orderdate, o.o_lineitems_tags" +
                " FROM " + E + ".`OrdersEWithLineitemsArrayAsTags` AS o" +
                " WHERE ANY tag IN o.o_lineitems_tags SATISFIES tag = 'MAIL' END")
                .rowsAsObject();
    }

    /**
     * R4) Indexed Array Tags Query — Find Orders by Tag
     * <p>
     * Tests indexed array filtering. Finds orders whose {@code o_lineitems_tags_indexed} array contains "MAIL".
     * <pre>
     * SELECT o.o_orderdate, o.o_lineitems_tags_indexed
     * FROM OrdersEWithLineitemsArrayAsTagsIndexed AS o
     * WHERE ANY tag IN o.o_lineitems_tags_indexed SATISFIES tag = 'MAIL' END
     * </pre>
     */
    public List<JsonObject> r4() {
        return cluster.query(
                "SELECT o.o_orderdate, o.o_lineitems_tags_indexed" +
                " FROM " + E + ".`OrdersEWithLineitemsArrayAsTagsIndexed` AS o" +
                " WHERE ANY tag IN o.o_lineitems_tags_indexed SATISFIES tag = 'MAIL' END")
                .rowsAsObject();
    }

    /**
     * R5) Embedded Customer with Nation with Region — Filter by Region Name
     * <p>
     * Tests denormalization vs join simulation in documents. Finds all orders from customers in "AMERICA".
     * <pre>
     * SELECT *
     * FROM OrdersEWithCustomerWithNationWithRegion AS o
     * WHERE o.o_customer.c_nation.n_region.r_name = 'AMERICA'
     * </pre>
     */
    public List<JsonObject> r5() {
        return cluster.query(
                "SELECT *" +
                " FROM " + E + ".`OrdersEWithCustomerWithNationWithRegion` AS o" +
                " WHERE o.o_customer.c_nation.n_region.r_name = 'AMERICA'")
                .rowsAsObject();
    }

    /**
     * R6) Regex Text Search on Comment Field
     * <p>
     * Simulates text search without an index.
     * <pre>SELECT * FROM OrdersEOnlyOComment AS o WHERE REGEXP_CONTAINS(o.o_comment, '(?i)furiously')</pre>
     */
    public List<JsonObject> r6() {
        return cluster.query(
                "SELECT *" +
                " FROM " + E + ".`OrdersEOnlyOComment` AS o" +
                " WHERE REGEXP_CONTAINS(o.o_comment, '(?i)furiously')")
                .rowsAsObject();
    }

    /**
     * R7) Text Index Search on Comment Field
     * <p>
     * Simulates text search with a B-tree index on {@code o_comment} (range-only benefit).
     * <pre>SELECT * FROM OrdersEOnlyOCommentIndexed AS o WHERE REGEXP_CONTAINS(o.o_comment, '(?i)furiously')</pre>
     */
    public List<JsonObject> r7() {
        return cluster.query(
                "SELECT *" +
                " FROM " + E + ".`OrdersEOnlyOCommentIndexed` AS o" +
                " WHERE REGEXP_CONTAINS(o.o_comment, '(?i)furiously')")
                .rowsAsObject();
    }

    /**
     * R8) Unwind Embedded Lineitems
     * <p>
     * Tests unwind of embedded objects (array flattening cost). Equivalent of MongoDB {@code $unwind}.
     * <pre>
     * SELECT META(o).id AS o_orderkey, l.l_partkey
     * FROM OrdersEWithLineitems AS o
     * UNNEST o.o_lineitems AS l
     * </pre>
     */
    public List<JsonObject> r8() {
        return cluster.query(
                "SELECT META(o).id AS o_orderkey, l.l_partkey" +
                " FROM " + E + ".`OrdersEWithLineitems` AS o" +
                " UNNEST o.o_lineitems AS l")
                .rowsAsObject();
    }

    /**
     * R9) Aggregation on Embedded Array — Sum Revenue per Order
     * <p>
     * Tests aggregation on embedded arrays (UNNEST + GROUP BY interaction).
     * <pre>
     * SELECT META(o).id AS o_orderkey, SUM(l.l_extendedprice) AS totalRevenue
     * FROM OrdersEWithLineitems AS o
     * UNNEST o.o_lineitems AS l
     * GROUP BY META(o).id
     * </pre>
     */
    public List<JsonObject> r9() {
        return cluster.query(
                "SELECT META(o).id AS o_orderkey, SUM(l.l_extendedprice) AS totalRevenue" +
                " FROM " + E + ".`OrdersEWithLineitems` AS o" +
                " UNNEST o.o_lineitems AS l" +
                " GROUP BY META(o).id",
                QueryOptions.queryOptions().timeout(Duration.ofMinutes(10)))
                .rowsAsObject();
    }
}
