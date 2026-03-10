package cz.cuni.mff.couchbase_java.springdata_r.repositories;

import com.couchbase.client.java.json.JsonObject;
import cz.cuni.mff.couchbase_java.springdata_r.models.OrdersR;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRRepository extends CouchbaseRepository<OrdersR, String> {
    @Query(
            "SELECT COUNT(META().id) AS order_count," +
            " MILLIS_TO_STR(o.o_orderdate, 'YYYY-MM') AS __id" +

            " FROM spring_bucket_r.spring_scope_r.OrdersR AS o" +
            " GROUP BY MILLIS_TO_STR(o.o_orderdate, 'YYYY-MM')")
    List<com.couchbase.client.java.json.JsonObject> countOrdersByMonthAsJson();
}
