package cz.cuni.mff.couchbase_java.springdata_e;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import cz.cuni.mff.couchbase_java.SpringDataCouchbaseClusterManagement;
import cz.cuni.mff.couchbase_java.springdata_e.benchmarks.QueriesSpringDataE;
import cz.cuni.mff.couchbase_java.springdata_e.models.*;
import cz.cuni.mff.couchbase_java.springdata_e.service.LogicServiceE;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import cz.cuni.mff.couchbase_java.springdata_e.TPCHDatasetLoaderSpringDataE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

@Configuration
public class CouchbaseSpringDataLogicCommandRunnerE {
    //@Autowired
    //private CouchbaseTemplate couchbaseTemplate;

    @Bean
    CommandLineRunner run(LogicServiceE service,
                          CouchbaseTemplate couchbaseTemplate,
                          Cluster cluster,
                          ReactiveCouchbaseTemplate  reactiveCouchbaseTemplate) {
        return args -> {



            /*System.out.println("Working dir: " + new java.io.File(".").getAbsolutePath());
            System.out.println("Dataset dir: " + new java.io.File("../../../dataset/TPC-H/tpch-data/").getAbsolutePath());

            if (true)
                return;*/

            Bucket bucket = cluster.bucket("spring_bucket_e");

            // Create Scope
            SpringDataCouchbaseClusterManagement.createScope("spring_scope_e", bucket);

            // Create Collections
            SpringDataCouchbaseClusterManagement.createCollection("spring_scope_e", "CustomerEWithOrders", bucket);
            SpringDataCouchbaseClusterManagement.createCollection("spring_scope_e", "OrdersEWithLineitems", bucket);
            SpringDataCouchbaseClusterManagement.createCollection("spring_scope_e", "OrdersEWithLineitemsArrayAsTags", bucket);
            SpringDataCouchbaseClusterManagement.createCollection("spring_scope_e", "OrdersEWithLineitemsArrayAsTagsIndexed", bucket);



            // Create Indexes - DEPRECATED
            //SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_e", "spring_scope_e", "CustomerEWithOrders", "s_nationkey");
            // Embedded Index
            //SpringDataCouchbaseClusterManagement.createIndex(cluster, "spring_bucket_e", "spring_scope_e", "CustomerEWithOrders",
            //        "c_orders.");

            // Create Indexes
            CustomerEWithOrders.createIndexes(cluster);
            OrdersEWithLineitems.createIndexes(cluster);
            OrdersEWithLineitemsArrayAsTags.createIndexes(cluster);
            OrdersEWithLineitemsArrayAsTagsIndexed.createIndexes(cluster);


            System.out.println("Query:");
            var res = QueriesSpringDataE.R4(cluster);
            System.out.println(res.get(0));
            System.out.println(res.size());


            /*
            TPCHDatasetLoaderSpringDataE.loadOrdersEWithLineitemsArrayAsTagsIndexed(
                    "../../../dataset/TPC-H/tpch-data/orders.tbl",
                    "../../../dataset/TPC-H/tpch-data/lineitem.tbl",
                    reactiveCouchbaseTemplate
            );

            TPCHDatasetLoaderSpringDataE.loadOrdersEWithLineitemsArrayAsTags(
                    "../../../dataset/TPC-H/tpch-data/orders.tbl",
                    "../../../dataset/TPC-H/tpch-data/lineitem.tbl",
                    reactiveCouchbaseTemplate
            );


            var lineitemsE = TPCHDatasetLoaderSpringDataE.createLineitemsE("../../../dataset/TPC-H/tpch-data/lineitem.tbl");
            TPCHDatasetLoaderSpringDataE.loadOrdersEWithLineitems("../../../dataset/TPC-H/tpch-data/orders.tbl", lineitemsE, reactiveCouchbaseTemplate);

            System.out.println("Creating ordersE");
            var orderse = TPCHDatasetLoaderSpringDataE.createOrders("../../../dataset/TPC-H/tpch-data/orders.tbl", reactiveCouchbaseTemplate);
            System.out.println("OrdersE created");

            System.out.println("Creating CustomerEWithOrders");
            TPCHDatasetLoaderSpringDataE.loadCustomers("../../../dataset/TPC-H/tpch-data/customer.tbl", orderse ,reactiveCouchbaseTemplate);
            System.out.println("CustomerEWithOrders created");
            */



        };
    }
}
