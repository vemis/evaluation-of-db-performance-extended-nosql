package cz.cuni.mff.mongodb_java.springdata_r;

import cz.cuni.mff.mongodb_java.springdata_r.benchmarks.QueriesSpringDataR;
import cz.cuni.mff.mongodb_java.springdata_r.benchmarks.SpringDataBenchmarkRunnerR;
import cz.cuni.mff.mongodb_java.springdata_r.model.RegionR;
import cz.cuni.mff.mongodb_java.springdata_r.service.LogicServiceR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class LogicCommandRunnerR {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    CommandLineRunner run(LogicServiceR service) {
        return args -> {
/*
            // Insert Regions
            TPCHDatasetLoaderSpringDataR.loadRegions("..\\..\\..\\dataset\\TPC-H\\tpch-data\\region.tbl", mongoTemplate);
            System.out.println("RegionRs saved!");


            // Insert Nations
            TPCHDatasetLoaderSpringDataR.loadNations("..\\..\\..\\dataset\\TPC-H\\tpch-data\\nation.tbl", mongoTemplate);
            System.out.println("NationRs saved!");

            // Insert Customers
            // Slow
            TPCHDatasetLoaderSpringDataR.loadCustomers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl", mongoTemplate);
            System.out.println("CustomerRs saved!");



            // Insert Orders
            TPCHDatasetLoaderSpringDataR.loadOrders("..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl", mongoTemplate);
            System.out.println("OrderRs saved!");


            // Insert Lineitems
            TPCHDatasetLoaderSpringDataR.loadLineitems("..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl", mongoTemplate);
            System.out.println("LineitemRs saved!");


            // Insert Partsupp
            TPCHDatasetLoaderSpringDataR.loadPartsupps("..\\..\\..\\dataset\\TPC-H\\tpch-data\\partsupp.tbl", mongoTemplate);
            System.out.println("partsuppRs saved!");

            // Insert Part
            TPCHDatasetLoaderSpringDataR.loadParts("..\\..\\..\\dataset\\TPC-H\\tpch-data\\part.tbl", mongoTemplate);
            System.out.println("partRs saved!");

            // Insert Suppliers
            TPCHDatasetLoaderSpringDataR.loadSuppliers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\supplier.tbl", mongoTemplate);
            System.out.println("supplierRs saved!");
*/
            //System.out.println("Executing Query A1");
            //var a1 = QueriesSpringDataR.A1(mongoTemplate);
            //System.out.println(a1.size());

            //System.out.println("Executing Query A2");
            //var a2 = QueriesSpringDataR.A2(mongoTemplate);
            //System.out.println(a2.size());

            //System.out.println("Executing Query B1");
            //var c1 = QueriesSpringDataR.B1(mongoTemplate);
            //System.out.println(c1.size());

            //System.out.println("Executing Query C2");
            //var c2 = QueriesSpringDataR.C2(mongoTemplate);
            //System.out.println(c2.size());
            //System.out.println("Query Executed.");

            //System.out.println("Executing Query D1");
            //var d1 = QueriesSpringDataR.D1(mongoTemplate);
            //System.out.println(d1.size());
            //System.out.println("Query Executed.");

            var q5 = QueriesSpringDataR.E3(mongoTemplate);
            for (int i = 0; i < 3; i++) {
                System.out.println(q5.get(i));
            }
            System.out.println(q5.size());

        };
    }
}
