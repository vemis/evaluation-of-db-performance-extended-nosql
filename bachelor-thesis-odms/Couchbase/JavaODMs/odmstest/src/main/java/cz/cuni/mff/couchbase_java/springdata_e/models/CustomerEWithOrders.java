package cz.cuni.mff.couchbase_java.springdata_e.models;

import com.couchbase.client.java.Cluster;
import cz.cuni.mff.couchbase_java.SpringDataCouchbaseClusterManagement;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;


import java.util.List;

@Document
@Scope("spring_scope_e")
@Collection("CustomerEWithOrders")
public class CustomerEWithOrders {
    @Id
    private int c_custkey;
    private String c_name;
    private String c_address;
    //@Indexed
    private int c_nationkey;
    private String c_phone;
    private double c_acctbal;
    private String c_mktsegment;
    private String c_commen;

    private List<OrdersE> c_orders;

    public CustomerEWithOrders() {}

    public CustomerEWithOrders(int c_custkey,
                     String c_name,
                     String c_address,
                     int c_nationkey,
                     String c_phone,
                     double c_acctbal,
                     String c_mktsegment,
                     String c_commen,
                               List<OrdersE> c_orders) {
        this.c_custkey = c_custkey;
        this.c_name = c_name;
        this.c_address = c_address;
        this.c_nationkey = c_nationkey;
        this.c_phone = c_phone;
        this.c_acctbal = c_acctbal;
        this.c_mktsegment = c_mktsegment;
        this.c_commen = c_commen;
        this.c_orders = c_orders;
    }

    public static void createIndexes(Cluster cluster){
        SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_e", "spring_scope_e", "CustomerEWithOrders", "s_nationkey");

        String c_orders_o_orderkey_index_query =
                "CREATE INDEX idx_customers_orders IF NOT EXISTS" +
                " ON spring_bucket_e.spring_scope_e.CustomerEWithOrders (" +
                        " DISTINCT ARRAY o.o_orderkey FOR o IN c_orders END" +
                    " )";
        cluster.query(c_orders_o_orderkey_index_query);

    }
}
