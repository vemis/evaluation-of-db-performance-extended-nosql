package cz.cuni.mff.couchbase_java.springdata_r;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import cz.cuni.mff.couchbase_java.SpringDataCouchbaseClusterManagement;
import cz.cuni.mff.couchbase_java.springdata.models.Address;
import cz.cuni.mff.couchbase_java.springdata.models.Employee;
import cz.cuni.mff.couchbase_java.springdata_r.benchmarks.QueriesSpringDataR;
import cz.cuni.mff.couchbase_java.springdata_r.models.NationR;
import cz.cuni.mff.couchbase_java.springdata_r.models.RegionR;
import cz.cuni.mff.couchbase_java.springdata_r.repositories.CustomerROrdersRRepository;
import cz.cuni.mff.couchbase_java.springdata_r.repositories.OrdersRRepository;
import cz.cuni.mff.couchbase_java.springdata_r.service.LogicServiceR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;

import java.util.Arrays;
import java.util.UUID;

@Configuration
public class CouchbaseSpringDataLogicCommandRunnerR {
    //@Autowired
    //private CouchbaseTemplate couchbaseTemplate;

    @Bean
    CommandLineRunner run(LogicServiceR service,
                          CouchbaseTemplate couchbaseTemplate,
                          Cluster cluster,
                          ReactiveCouchbaseTemplate  reactiveCouchbaseTemplate,
                          OrdersRRepository ordersRRepository,
                          CustomerROrdersRRepository customerROrdersRRepository) {
        return args -> {

            Bucket bucket = cluster.bucket("spring_bucket_r");
/*
            // Create Scope
            SpringDataCouchbaseClusterManagement.createScope("spring_scope_r", bucket);

            // Create Collections
            SpringDataCouchbaseClusterManagement.createCollection("spring_scope_r", "RegionR", bucket);

            SpringDataCouchbaseClusterManagement.createCollection("spring_scope_r", "NationR", bucket);

            SpringDataCouchbaseClusterManagement.createCollection("spring_scope_r", "CustomerR", bucket);

            SpringDataCouchbaseClusterManagement.createCollection("spring_scope_r", "OrdersR", bucket);

            SpringDataCouchbaseClusterManagement.createCollection("spring_scope_r", "LineitemR", bucket);

            SpringDataCouchbaseClusterManagement.createCollection("spring_scope_r", "PartsuppR", bucket);

            SpringDataCouchbaseClusterManagement.createCollection("spring_scope_r", "SupplierR", bucket);

            SpringDataCouchbaseClusterManagement.createCollection("spring_scope_r", "PartR", bucket);


            // Create Indexes

            SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_r", "spring_scope_r", "NationR", "n_regionkey");

            SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_r", "spring_scope_r", "SupplierR", "s_nationkey");

            SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_r", "spring_scope_r", "PartsuppR", "ps_suppKey");
            SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_r", "spring_scope_r", "PartsuppR", "ps_partKey");

            SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_r", "spring_scope_r", "OrdersR", "o_custkey");

            SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_r", "spring_scope_r", "CustomerR", "c_nationkey");

            SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_r", "spring_scope_r", "OrdersR", "o_orderkey_field");

            SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_r", "spring_scope_r", "LineitemR", "l_orderkey");
            SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_r", "spring_scope_r", "LineitemR", "l_ps_id");
            SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_r", "spring_scope_r", "LineitemR", "l_partkey");
            SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_r", "spring_scope_r", "LineitemR", "l_suppkey");


            // Insert Regions
            TPCHDatasetLoaderSpringDataR.loadRegions("..\\..\\..\\dataset\\TPC-H\\tpch-data\\region.tbl", reactiveCouchbaseTemplate);
            System.out.println("RegionRs saved!");


            // Insert Nations
            TPCHDatasetLoaderSpringDataR.loadNations("..\\..\\..\\dataset\\TPC-H\\tpch-data\\nation.tbl", reactiveCouchbaseTemplate);
            System.out.println("NationRs saved!");

            // Insert Customers
            // Slow
            TPCHDatasetLoaderSpringDataR.loadCustomers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl", reactiveCouchbaseTemplate);
            System.out.println("CustomerRs saved!");



            // Insert Orders
            TPCHDatasetLoaderSpringDataR.loadOrders("..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl", reactiveCouchbaseTemplate);
            System.out.println("OrderRs saved!");


            // Insert Lineitems
            TPCHDatasetLoaderSpringDataR.loadLineitems("..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl", reactiveCouchbaseTemplate);
            System.out.println("LineitemRs saved!");


            // Insert Partsupp
            TPCHDatasetLoaderSpringDataR.loadPartsupps("..\\..\\..\\dataset\\TPC-H\\tpch-data\\partsupp.tbl", reactiveCouchbaseTemplate);
            System.out.println("partsuppRs saved!");

            // Insert Part
            TPCHDatasetLoaderSpringDataR.loadParts("..\\..\\..\\dataset\\TPC-H\\tpch-data\\part.tbl", reactiveCouchbaseTemplate);
            System.out.println("partRs saved!");

            // Insert Suppliers
            TPCHDatasetLoaderSpringDataR.loadSuppliers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\supplier.tbl", reactiveCouchbaseTemplate);
            System.out.println("supplierRs saved!");

*/
            // Queries
            /*System.out.println("Query A1:");
            var a1 = QueriesSpringDataR.A1(reactiveCouchbaseTemplate);
            System.out.println(a1.size());
            System.out.println(a1.get(0));
            System.out.println("Query A1 completed");*/

            /*System.out.println("Query A2:");
            var a2 = QueriesSpringDataR.A2(reactiveCouchbaseTemplate);
            System.out.println(a2.size());
            System.out.println(a2.get(0));
            System.out.println("Query A2 completed");*/

            /*
            System.out.println("Query B1:");
            var b1 = QueriesSpringDataR.B1(ordersRRepository);
            System.out.println(b1.size());
            System.out.println(b1.get(0));
            System.out.println("Query B1 completed");*/


            /*System.out.println("Query C2:");
            var c2 = customerROrdersRRepository.findCustomerOrders();
            System.out.println(c2.size());
            System.out.println(c2.get(0).toString());
            System.out.println("Query C2 completed");*/

            /*System.out.println("Query D1:");
            var d1 = QueriesSpringDataR.D1(cluster);
            System.out.println(d1.size());
            System.out.println(d1.get(0).toString());
            System.out.println("Query D1 completed");*/

            System.out.println("Query started:");
            var res = QueriesSpringDataR.Q5(cluster);
            for (int i = 0; i < 3; i++) {
                System.out.println(res.get(i));
            }
            System.out.println(res.size());

        };
    }
}
