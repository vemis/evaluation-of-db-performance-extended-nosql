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

    // R1) Embedded lineitems — filter on non-indexed field l_quantity > 5
    public List<JsonObject> r1() {
        return cluster.query(
                "SELECT o.o_orderdate," +
                " ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems" +
                " FROM " + E + ".`OrdersEWithLineitems` AS o" +
                " WHERE ANY l IN o.o_lineitems SATISFIES l.l_quantity > 5 END")
                .rowsAsObject();
    }

    // R2) Embedded lineitems — filter on indexed field l_partkey > 20000
    public List<JsonObject> r2() {
        return cluster.query(
                "SELECT o.o_orderdate," +
                " ARRAY l.l_partkey FOR l IN o.o_lineitems END AS o_lineitems" +
                " FROM " + E + ".`OrdersEWithLineitems` AS o" +
                " WHERE ANY l IN o.o_lineitems SATISFIES l.l_partkey > 20000 END")
                .rowsAsObject();
    }

    // R3) Array tags — non-indexed, find orders tagged with 'MAIL'
    public List<JsonObject> r3() {
        return cluster.query(
                "SELECT o.o_orderdate, o.o_lineitems_tags" +
                " FROM " + E + ".`OrdersEWithLineitemsArrayAsTags` AS o" +
                " WHERE ANY tag IN o.o_lineitems_tags SATISFIES tag = 'MAIL' END")
                .rowsAsObject();
    }

    // R4) Array tags — indexed, find orders tagged with 'MAIL'
    public List<JsonObject> r4() {
        return cluster.query(
                "SELECT o.o_orderdate, o.o_lineitems_tags_indexed" +
                " FROM " + E + ".`OrdersEWithLineitemsArrayAsTagsIndexed` AS o" +
                " WHERE ANY tag IN o.o_lineitems_tags_indexed SATISFIES tag = 'MAIL' END")
                .rowsAsObject();
    }

    // R5) Deeply nested — filter by region name 'AMERICA'
    public List<JsonObject> r5() {
        return cluster.query(
                "SELECT *" +
                " FROM " + E + ".`OrdersEWithCustomerWithNationWithRegion` AS o" +
                " WHERE o.o_customer.c_nation.n_region.r_name = 'AMERICA'")
                .rowsAsObject();
    }

    // R6) Regex text search — no index (REGEXP_CONTAINS)
    public List<JsonObject> r6() {
        return cluster.query(
                "SELECT *" +
                " FROM " + E + ".`OrdersEOnlyOComment` AS o" +
                " WHERE REGEXP_CONTAINS(o.o_comment, '(?i)furiously')")
                .rowsAsObject();
    }

    // R7) Regex text search — B-tree index on o_comment (range-only benefit)
    public List<JsonObject> r7() {
        return cluster.query(
                "SELECT *" +
                " FROM " + E + ".`OrdersEOnlyOCommentIndexed` AS o" +
                " WHERE REGEXP_CONTAINS(o.o_comment, '(?i)furiously')")
                .rowsAsObject();
    }

    // R8) UNNEST — flatten embedded lineitems array (equivalent of MongoDB $unwind)
    public List<JsonObject> r8() {
        return cluster.query(
                "SELECT META(o).id AS o_orderkey, l.l_partkey" +
                " FROM " + E + ".`OrdersEWithLineitems` AS o" +
                " UNNEST o.o_lineitems AS l")
                .rowsAsObject();
    }

    // R9) Aggregation on embedded array — sum revenue per order
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
