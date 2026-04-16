package cz.cuni.mff.mongodb_java.morphia;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import cz.cuni.mff.mongodb_java.TPCHDatasetLoader;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.*;
import dev.morphia.Datastore;
import org.springframework.core.annotation.Order;


import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.atomic.LongAdder;

/**
 * Loading could be done using constructor, but having doubts abour the performance
 */
public class TPCHDatasetLoaderMorphiaR extends TPCHDatasetLoader {


    public static <T> T loadTable(Class<T> clazz, String[] row) {

        try {
            Constructor<T> constructor = clazz.getConstructor(String[].class);
            return constructor.newInstance((Object) row);
        } catch (Exception e) {
            throw new RuntimeException("Could not create instance of " + clazz.getName(), e);
        }
    }

    public static void loadRegions(String filePath, Datastore datastore) {

        List<String[]> regions = readDataFromCustomSeparator(filePath);

        //RegionR[] regionInstances = new RegionR[regions.size()];
        ArrayList<RegionR> regionInstances = new ArrayList<>();

        for (int i = 0; i < regions.size(); i++) { //for (String[] row : regions) {
            String[] row = regions.get(i);
            RegionR region = new RegionR(
                    Integer.parseInt(row[0]),
                    row[1],
                    row[2]
            );
            //datastore.save(region);
            //regionInstances[i] = region;
            regionInstances.add(region);
        }
        datastore.save(regionInstances);
    }

    public static void loadNations(String filePath, Datastore datastore) {

        List<String[]> nations = readDataFromCustomSeparator(filePath);

        ArrayList<NationR> nationInstances = new ArrayList<>();

        for (int i = 0; i < nations.size(); i++) { //for (String[] row : nations) {
            String[] row = nations.get(i);
            NationR nation = new NationR(
                    Integer.parseInt(row[0]),
                    row[1],
                    Integer.parseInt(row[2]),
                    row[3]
            );
            //datastore.save(nation);
            nationInstances.add(nation);
        }
        datastore.save(nationInstances);
    }

    public static void loadCustomers(String filePath, Datastore datastore) {

        List<String[]> customers = readDataFromCustomSeparator(filePath);

        ArrayList<CustomerR> customerInstances = new ArrayList<>();

        for (int i = 0; i < customers.size(); i++) {//for (String[] row : customers) {
            System.out.println("Customer:" + Integer.toString(i) + "/" + Integer.toString(customers.size()));
            //i++;
            String[] row = customers.get(i);
            CustomerR customer = new CustomerR(
                    Integer.parseInt(row[0]),
                    row[1],
                    row[2],
                    Integer.parseInt(row[3]),
                    row[4],
                    Double.parseDouble(row[5]),
                    row[6],
                    row[7]
            );
            //datastore.save(customer);//WriteConcern. UNACKNOWLEDGED
            customerInstances.add(customer);
        }
        datastore.save(customerInstances);
    }

    public static void loadOrders(String filePath, Datastore datastore) {

        List<String[]> orders = readDataFromCustomSeparator(filePath);

        LongAdder counter = new LongAdder();
        int total = orders.size();

        List<OrdersR> orderInstances = orders
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new OrdersR(
                            Integer.parseInt(row[0]),
                            Integer.parseInt(row[1]),
                            row[2],
                            row[4],
                            LocalDate.parse(row[4]),
                            row[5],
                            row[6],
                            row[7],
                            row[8]
                    );
                })
                .toList();

        /*
        // Can be saved like this, but very slow
        // Disclaimer - datastore.save(List<>...) is not working - the instance is missing the annotation!
        // Needs to be ArrayList<>
        */

        // Faster approach, but collection needs to be dropped beforehand!
        MongoCollection<OrdersR> collection =
                datastore.getDatabase()
                        .getCollection("ordersR", OrdersR.class);

        System.out.println("Inserting many orderInstances!");
        collection.insertMany(orderInstances, new InsertManyOptions().ordered(false));

        System.out.println("orders inserted!");
    }

    @Deprecated
    public static void loadOrdersDEPRECATED(String filePath, Datastore datastore) {

        List<String[]> orders = readDataFromCustomSeparator(filePath);

        ArrayList<OrdersR> orderInstances = new ArrayList<>();

        for (int i = 0; i < orders.size(); i++) {//for (String[] row : customers) {
            System.out.println("Order:" + Integer.toString(i) + "/" + Integer.toString(orders.size()));
            //i++;
            String[] row = orders.get(i);
            OrdersR customer = new OrdersR(
                    Integer.parseInt(row[0]),
                    Integer.parseInt(row[1]),
                    row[2],
                    row[4],
                    LocalDate.parse(row[4]),
                    row[5],
                    row[6],
                    row[7],
                    row[8]
            );
            //datastore.save(customer);//WriteConcern. UNACKNOWLEDGED
            orderInstances.add(customer);
        }
        datastore.save(orderInstances);
    }


    public static void loadLineitems(String filePath, Datastore datastore) {

        List<String[]> lineitems = readDataFromCustomSeparator(filePath);

        LongAdder counter = new LongAdder();
        int total = lineitems.size();

        List<LineitemR> lineitemInstances = lineitems
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new LineitemR(
                            Integer.parseInt(row[0]),
                            Integer.parseInt(row[1]),
                            Integer.parseInt(row[2]),
                            Integer.parseInt(row[3]),
                            Integer.parseInt(row[4]),
                            Double.parseDouble(row[5]),
                            Double.parseDouble(row[6]),
                            Double.parseDouble(row[7]),
                            row[8],
                            row[9],
                            LocalDate.parse(row[10]),
                            LocalDate.parse( row[11]),
                            LocalDate.parse(row[12]),
                            row[13],
                            row[14],
                            row[15]
                    );
                })
                //.toArray(LineitemR[]::new);
                .toList();

        /*
        // Can be saved like this, but very slow
        // Disclaimer - datastore.save(List<>...) is not working - the instance is missing the annotation!
        // Needs to be ArrayList<>

        System.out.println("Creating ArrayList<> from List<>");
        ArrayList<LineitemR> lineitemInstancesArrayList = new ArrayList<>(lineitemInstances);

        System.out.println("Saving ArrayList<>");

        datastore.save(lineitemInstancesArrayList, new dev.morphia.InsertManyOptions().ordered(false));

        System.out.println("ArrayList saved!");
        */

        // Faster approach, but collection needs to be dropped beforehand!
        MongoCollection<LineitemR> collection =
                datastore.getDatabase()
                        .getCollection("lineitemR", LineitemR.class);

        System.out.println("Inserting many lineitemInstances!");
        collection.insertMany(lineitemInstances, new InsertManyOptions().ordered(false));

        System.out.println("lineitemInstances inserted!");
    }

    public static void loadLineitemsDEPRECEATED(String filePath, Datastore datastore) {

        List<String[]> lineitems = readDataFromCustomSeparator(filePath);

        ArrayList<LineitemR> lineitemInstances = new ArrayList<>();

        for (int i = 0; i < lineitems.size(); i++) {//for (String[] row : customers) {
            if (i % 10_000 == 0 && !lineitemInstances.isEmpty()) {
                System.out.println("Lineitem:" + Integer.toString(i) + "/" + Integer.toString(lineitems.size()));
                //datastore.save(lineitemInstances);

                MongoCollection<LineitemR> collection =
                        datastore.getDatabase()
                                .getCollection("lineitemR", LineitemR.class);
                collection.insertMany(lineitemInstances, new InsertManyOptions().ordered(false));

                System.out.println("Part of Lineitems saved");
                lineitemInstances.clear();
            }
            //i++;
            String[] row = lineitems.get(i);
            LineitemR lineitem = new LineitemR(
                    Integer.parseInt(row[0]),
                    Integer.parseInt(row[1]),
                    Integer.parseInt(row[2]),

                    Integer.parseInt(row[3]),
                    Integer.parseInt(row[4]),

                    Double.parseDouble(row[5]),
                    Double.parseDouble(row[6]),
                    Double.parseDouble(row[7]),

                    row[8],
                    row[9],
                    LocalDate.parse(row[10]),
                    LocalDate.parse(row[11]),
                    LocalDate.parse(row[12]),
                    row[13],
                    row[14],
                    row[15]
            );
            //datastore.save(customer);//WriteConcern. UNACKNOWLEDGED
            lineitemInstances.add(lineitem);
        }
        datastore.save(lineitemInstances);
    }

    public static void loadPartsupps(String filePath, Datastore datastore) {

        List<String[]> partsupps = readDataFromCustomSeparator(filePath);

        LongAdder counter = new LongAdder();
        int total = partsupps.size();

        List<PartsuppR> partsuppInstances = partsupps
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new PartsuppR(
                            //row[0] + "|" + row[1],
                            Integer.parseInt(row[0]),
                            Integer.parseInt(row[1]),
                            Integer.parseInt(row[2]),
                            Double.parseDouble(row[3]),
                            row[4]
                    );
                })
                .toList();

        /*
        // Can be saved like this, but very slow
        // Disclaimer - datastore.save(List<>...) is not working - the instance is missing the annotation!
        // Needs to be ArrayList<>
        */

        // Faster approach, but collection needs to be dropped beforehand!
        MongoCollection<PartsuppR> collection =
                datastore.getDatabase()
                        .getCollection("partsuppR", PartsuppR.class);

        System.out.println("Inserting many partsuppInstances!");
        collection.insertMany(partsuppInstances, new InsertManyOptions().ordered(false));

        System.out.println("partsupp inserted!");
    }

    public static void loadParts(String filePath, Datastore datastore) {

        List<String[]> parts = readDataFromCustomSeparator(filePath);

        LongAdder counter = new LongAdder();
        int total = parts.size();

        List<PartR> partInstances = parts
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new PartR(
                            Integer.parseInt(row[0]),
                            row[1],
                            row[2],
                            row[3],
                            row[4],
                            Integer.parseInt(row[5]),
                            row[6],
                            Double.parseDouble(row[7]),
                            row[8]
                    );
                })
                .toList();

        /*
        // Can be saved like this, but very slow
        // Disclaimer - datastore.save(List<>...) is not working - the instance is missing the annotation!
        // Needs to be ArrayList<>
        */

        // Faster approach, but collection needs to be dropped beforehand!
        MongoCollection<PartR> collection =
                datastore.getDatabase()
                        .getCollection("partR", PartR.class);

        System.out.println("Inserting many partInstances!");
        collection.insertMany(partInstances, new InsertManyOptions().ordered(false));

        System.out.println("part inserted!");
    }

    public static void loadSuppliers(String filePath, Datastore datastore) {

        List<String[]> suppliers = readDataFromCustomSeparator(filePath);

        LongAdder counter = new LongAdder();
        int total = suppliers.size();

        List<SupplierR> supplierInstances = suppliers
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new SupplierR(
                            Integer.parseInt(row[0]),
                            row[1],
                            row[2],
                            Integer.parseInt(row[3]),
                            row[4],
                            Double.parseDouble(row[5]),
                            row[6]
                    );
                })
                .toList();

        /*
        // Can be saved like this, but very slow
        // Disclaimer - datastore.save(List<>...) is not working - the instance is missing the annotation!
        // Needs to be ArrayList<>
        */

        // Faster approach, but collection needs to be dropped beforehand!
        MongoCollection<SupplierR> collection =
                datastore.getDatabase()
                        .getCollection("supplierR", SupplierR.class);

        System.out.println("Inserting many supplierInstances!");
        collection.insertMany(supplierInstances, new InsertManyOptions().ordered(false));

        System.out.println("suppliers inserted!");
    }

}
