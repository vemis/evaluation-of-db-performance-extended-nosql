package cz.cuni.mff.mongodb_java.morphia;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import cz.cuni.mff.mongodb_java.morphia.benchmarks.QueriesMorphiaR;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;

public class MainR {
    public static void main(String[] args){
        // 1. Create a MongoClient (connects to local MongoDB by default)
        MongoClient client = MongoClients.create("mongodb://localhost:27017");

        // 2. Configure Morphia
        MapperOptions options = MapperOptions.builder()
                .storeNulls(false)     // <-- THIS makes Morphia write null values
                .build();

        // 3. Create a Datastore instance
        Datastore datastore = Morphia.createDatastore(client, "morphia_database_tpch_relational", options);

        // 4. Tell Morphia to discover your entity classes
        datastore.getMapper().mapPackage("cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational");

        datastore.ensureIndexes();
        

        System.out.println("Morphia initialized!");
/*
        // Insert Regions
        TPCHDatasetLoaderMorphiaR.loadRegions("..\\..\\..\\dataset\\TPC-H\\tpch-data\\region.tbl", datastore);
        System.out.println("RegionRs saved!");

        // Insert Nations
        TPCHDatasetLoaderMorphiaR.loadNations("..\\..\\..\\dataset\\TPC-H\\tpch-data\\nation.tbl", datastore);
        System.out.println("NationRs saved!");

        // Insert Customers
        // Slow
        TPCHDatasetLoaderMorphiaR.loadCustomers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl", datastore);
        System.out.println("CustomerRs saved!");

        // Insert Orders
        TPCHDatasetLoaderMorphiaR.loadOrders("..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl", datastore);
        System.out.println("OrderRs saved!");

        // Insert Lineitems
        TPCHDatasetLoaderMorphiaR.loadLineitems("..\\..\\..\\dataset\\TPC-H\\tpch-data\\lineitem.tbl", datastore);
        System.out.println("LineitemRs saved!");


        // Insert Partsupp
        TPCHDatasetLoaderMorphiaR.loadPartsupps("..\\..\\..\\dataset\\TPC-H\\tpch-data\\partsupp.tbl", datastore);
        System.out.println("partsuppRs saved!");

        // Insert Part
        TPCHDatasetLoaderMorphiaR.loadParts("..\\..\\..\\dataset\\TPC-H\\tpch-data\\part.tbl", datastore);
        System.out.println("partRs saved!");

        // Insert Suppliers
        TPCHDatasetLoaderMorphiaR.loadSuppliers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\supplier.tbl", datastore);
        System.out.println("supplierRs saved!");
*/

        // Queries
        /*int runs;
        long total;
        long start;
        long end;
        int lSize = 0;

        // Query A1
        System.out.println("Starting Query A1");

        runs = 2;
        total = 0;
        for (int i = 0; i < runs; i++) {
            start = System.nanoTime();

            List<LineitemR> a1 = QueriesR.A1(datastore);
            lSize = a1.size();

            end = System.nanoTime();
            total += (end - start);
        }

        System.out.println("Average time (ms): " + (total / runs) / 1_000_000);
        System.out.println("A1 size: " + lSize);
        System.out.println("Query A1 finished");*/


        var q5 = QueriesMorphiaR.Q5(datastore);
        //System.out.println(q5.get(0));
        //System.out.println(q5.get(1));
        //System.out.println(q5.get(2));
        q5.forEach(System.out::println);
        System.out.println(q5.size());


    }

}
