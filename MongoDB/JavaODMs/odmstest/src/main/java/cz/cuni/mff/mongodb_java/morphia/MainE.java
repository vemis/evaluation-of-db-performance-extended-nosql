package cz.cuni.mff.mongodb_java.morphia;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import cz.cuni.mff.mongodb_java.morphia.models.Address;
import cz.cuni.mff.mongodb_java.morphia.models.Employee;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.NationE;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.RegionE;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.SupplierE;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MainE {
    public static void main(String[] args){
        // 1. Create a MongoClient (connects to local MongoDB by default)
        MongoClient client = MongoClients.create("mongodb://localhost:27017");

        // 2. Configure Morphia
        MapperOptions options = MapperOptions.builder()
                .storeNulls(false)     // <-- THIS makes Morphia write null values
                .build();

        // 3. Create a Datastore instance
        Datastore datastore = Morphia.createDatastore(client, "morphia_database_tpch_embedded", options);

        // 4. Tell Morphia to discover your entity classes
        datastore.getMapper().mapPackage("cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded");

        datastore.ensureIndexes();

        System.out.println("Morphia initialized!");


        // Create Orders
        var orders = TPCHDatasetLoaderMorphiaE.loadOrders("..\\..\\..\\dataset\\TPC-H\\tpch-data\\orders.tbl", datastore);
        System.out.println("OrderEs created!");

        // Create Customers
        var customers = TPCHDatasetLoaderMorphiaE.loadCustomers("..\\..\\..\\dataset\\TPC-H\\tpch-data\\customer.tbl", orders ,datastore);
        System.out.println("CustomerEs created!");

        // Create Nations
        var nations = TPCHDatasetLoaderMorphiaE.loadNations("..\\..\\..\\dataset\\TPC-H\\tpch-data\\nation.tbl", customers,  datastore);
        System.out.println("NationEs created!");

        // Create Regions
        // WARNING: I AM LOADING ALL THE DATA TO ALL THE ENTITIES
        // -> EVERY CUSTOMER HAVE EVERY ORDER AND SO ON....
        // INSTEAD OF EVERY CUSTOMER HAVE HIS ORDERS
        //TPCHDatasetLoaderMorphiaE.loadRegions("..\\..\\..\\dataset\\TPC-H\\tpch-data\\region.tbl", nations, datastore);
        //System.out.println("RegionEs saved!");
    }
}
