package cz.cuni.mff.mongodb_java.springdata_e;

import cz.cuni.mff.mongodb_java.springdata_e.benchmarks.QueriesSpringDataE;
import cz.cuni.mff.mongodb_java.springdata_e.service.LogicServiceE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class LogicCommandRunnerE {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    CommandLineRunner run(LogicServiceE service) {
        return args -> {
/*
            // Create RegionsE
            var ordersE = TPCHDatasetLoaderSpringDataE.loadOrders("..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl");
            System.out.println("OrdersE created");

            // Create Customers
            TPCHDatasetLoaderSpringDataE.loadCustomers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl", ordersE ,mongoTemplate);
            System.out.println("CustomersE inserted");
*/
            /*var c2 = QueriesSpringDataE.C2(mongoTemplate);
            System.out.println(c2.size());
            System.out.println(c2.get(0));*/


        };
    }
}
