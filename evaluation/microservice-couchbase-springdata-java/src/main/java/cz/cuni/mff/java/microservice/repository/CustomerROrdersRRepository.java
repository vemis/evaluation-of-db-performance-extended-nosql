package cz.cuni.mff.java.microservice.repository;

import cz.cuni.mff.java.microservice.model.relational.CustomerROrdersR;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.Query;

import java.util.List;

public interface CustomerROrdersRRepository extends CouchbaseRepository<CustomerROrdersR, String> {

    @Query("SELECT c.c_name AS cName, o.o_orderdate AS oOrderDate, o.o_totalprice AS oTotalPrice," +
           " TO_STRING(META(o).id) AS __id" +
           " FROM `bucket-main`.`spring_scope_r`.`CustomerR` AS c" +
           " JOIN `bucket-main`.`spring_scope_r`.`OrdersR` AS o ON META(c).id = o.o_custkey")
    List<CustomerROrdersR> findCustomerOrders();
}
