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


            /**
             * ● Done. Here's the summary of everything created:
             *
             *   4 new model classes in springdata_e/model/:
             *   - RegionEOnlyName — plain POJO, r_regionkey + r_name
             *   - NationEOnlyNNameNRegion — plain POJO, embeds RegionEOnlyName
             *   - CustomerEOnlyCNameCNation — plain POJO, embeds NationEOnlyNNameNRegion
             *   - OrdersEWithCustomerWithNationWithRegion — @Document(collection = "ordersEWithCustomerWithNationWithRegion"), embeds CustomerEOnlyCNameCNation
             *
             *   (Embedded classes are plain POJOs — no @Embedded annotation needed in Spring Data, unlike Morphia.)
             *
             *   4 new loader methods in TPCHDatasetLoaderSpringDataE:
             *   - createRegionEOnlyName(filePath) — builds region list from region.tbl
             *   - createNationEOnlyNNameNRegion(filePath, region) — builds nation list with single embedded region
             *   - createCustomerEOnlyCNameCNation(filePath, nation) — builds customer list with single embedded nation
             *   - loadOrdersEWithCustomerWithNationWithRegion(...) — builds regionMap → nationMap → customerMap, then creates and batch-inserts orders with fully embedded customer/nation/region
             *
             *   Query R5 in QueriesSpringDataE:
             *   - Criteria.where("o_customer.c_nation.n_region.r_name").is("AMERICA") — deep nested field filter, returns List<OrdersEWithCustomerWithNationWithRegion>
             *
             * ✻ Crunched for 1m 40s
             *
             * ─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
             * ❯ <
             * Resume this session with:────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
             * claude --resume 31304cc5-4214-40fb-be3e-3f856e2edf4
             */


            System.out.println("Query started:");
            var res = QueriesSpringDataE.R5(mongoTemplate);
            System.out.println(res.get(0));
            System.out.println(res.size());




            /*
            TPCHDatasetLoaderSpringDataE.loadOrdersEWithCustomerWithNationWithRegion(
                    "..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl",
                    "..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl",
                    "..\\..\\..\\dataset\\TPC-H\\tpch-data\\nation.tbl",
                    "..\\..\\..\\dataset\\TPC-H\\tpch-data\\region.tbl",
                    mongoTemplate
            );

            TPCHDatasetLoaderSpringDataE.loadOrdersEWithLineitemsArrayAsTagsIndexed(
                    "..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl",
                    "..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl",
                    mongoTemplate
            );

            TPCHDatasetLoaderSpringDataE.loadOrdersEWithLineitemsArrayAsTags(
                    "..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl",
                    "..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl",
                    mongoTemplate
            );

            var lineitemsE = TPCHDatasetLoaderSpringDataE.createLineitemsE("..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl");
            System.out.println(lineitemsE.size());

            TPCHDatasetLoaderSpringDataE.loadOrdersEWithLineitems("..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl", lineitemsE, mongoTemplate );
            System.out.println("OrdersEWithLineitems loaded");

            // Create RegionsE
            var ordersE = TPCHDatasetLoaderSpringDataE.loadOrders("..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl");
            System.out.println("OrdersE created");

            // Create Customers
            TPCHDatasetLoaderSpringDataE.loadCustomers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl", ordersE ,mongoTemplate);
            System.out.println("CustomersE inserted");
*/



        };
    }
}
