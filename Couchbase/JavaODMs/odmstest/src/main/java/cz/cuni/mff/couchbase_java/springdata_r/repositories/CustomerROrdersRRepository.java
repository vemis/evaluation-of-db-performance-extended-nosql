package cz.cuni.mff.couchbase_java.springdata_r.repositories;

import com.couchbase.client.java.json.JsonObject;
import cz.cuni.mff.couchbase_java.springdata_r.models.CustomerROrdersR;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import java.util.List;


public interface CustomerROrdersRRepository extends CouchbaseRepository<CustomerROrdersR, String> {

    @Query( "SELECT c.c_name AS cName, o.o_orderdate AS oOrderDate, o.o_totalprice AS oTotalPrice, TO_STRING( META(o).id ) AS __id" +
            " FROM spring_bucket_r.spring_scope_r.CustomerR c" +
            " JOIN spring_bucket_r.spring_scope_r.OrdersR o ON META(c).id = o.o_custkey"
                    )
    List<CustomerROrdersR> findCustomerOrders();
}