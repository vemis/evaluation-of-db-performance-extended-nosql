package cz.cuni.mff.couchbase_java.springdata_r;


import cz.cuni.mff.couchbase_java.TPCHDatasetLoader;
import cz.cuni.mff.couchbase_java.springdata_r.models.*;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;

/**
 * Loading could be done using constructor, but having doubts abour the performance
 */
public class TPCHDatasetLoaderSpringDataR extends TPCHDatasetLoader {


    public static void loadRegions(String filePath, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

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
            //reactiveCouchbaseTemplate.save(region);
            //regionInstances[i] = region;
            regionInstances.add(region);
        }
        //reactiveCouchbaseTemplate.save(regionInstances);
        saveManyDocuments(regionInstances, reactiveCouchbaseTemplate);

    }

    public static void loadNations(String filePath, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        List<String[]> nations = readDataFromCustomSeparator(filePath);

        ArrayList<NationR> nationInstances = new ArrayList<>();

        for (int i = 0; i < nations.size(); i++) { //for (String[] row : nations) {
            String[] row = nations.get(i);
            NationR nation = new NationR(
                    Integer.parseInt(row[0]),
                    row[1],
                    row[2],
                    row[3]
            );
            //reactiveCouchbaseTemplate.save(nation);
            nationInstances.add(nation);
        }
        //reactiveCouchbaseTemplate.insert(nationInstances,  NationR.class);
        saveManyDocuments(nationInstances, reactiveCouchbaseTemplate);
    }

    public static void loadCustomers(String filePath, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

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
                    row[3],
                    row[4],
                    Double.parseDouble(row[5]),
                    row[6],
                    row[7]
            );
            //reactiveCouchbaseTemplate.save(customer);//WriteConcern. UNACKNOWLEDGED
            customerInstances.add(customer);
        }
        //reactiveCouchbaseTemplate.insert(customerInstances, CustomerR.class);
        saveManyDocuments(customerInstances, reactiveCouchbaseTemplate);
    }

    public static void loadOrders(String filePath, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

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
                            row[1],
                            row[2],
                            Double.parseDouble(row[3]),
                            LocalDate.parse(row[4]),
                            row[5],
                            row[6],
                            row[7],
                            row[8]
                    );
                })
                .toList();


        System.out.println("Inserting many orderInstances!");

        //reactiveCouchbaseTemplate.insert(orderInstances, OrdersR.class);
        saveManyDocuments(orderInstances, reactiveCouchbaseTemplate);
        System.out.println("orders inserted!");
    }



    public static void loadLineitems(String filePath, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        List<String[]> lineitems = readDataFromCustomSeparator(filePath);

        //lineitems = lineitems.subList(0 ,1_000_000);

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
                            row[0],
                            row[1],
                            row[2],
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
                })
                //.toArray(LineitemR[]::new);
                .toList();

        var batches = partition(lineitemInstances, 200_000);

        System.out.println("Inserting many lineitemInstances!");

        for (int i = 0; i < batches.size(); i++) {
            //reactiveCouchbaseTemplate.insert(batches.get(i), LineitemR.class);
            saveManyDocuments(batches.get(i), reactiveCouchbaseTemplate);
            System.out.println("Batch inserted! " + (i + 1) + "/" + batches.size());
        }

        System.out.println("lineitemInstances inserted!");
    }

    public static void loadPartsupps(String filePath, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

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
                            row[0],
                            row[1],
                            Integer.parseInt(row[2]),
                            Double.parseDouble(row[3]),
                            row[4]
                    );
                })
                .toList();

        var batches = partition(partsuppInstances, 200_000);

        System.out.println("Inserting many partsuppInstances!");

        for (int i = 0; i < batches.size(); i++) {
            //reactiveCouchbaseTemplate.insert(batches.get(i), PartsuppR.class);
            saveManyDocuments(batches.get(i), reactiveCouchbaseTemplate);
            System.out.println("Batch inserted! " + (i + 1) + "/" + batches.size());
        }

        System.out.println("partsupp inserted!");
    }

    public static void loadParts(String filePath, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

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

        var batches = partition(partInstances, 200_000);

        System.out.println("Inserting many partInstances!");

        for (int i = 0; i < batches.size(); i++) {
            //reactiveCouchbaseTemplate.insert(batches.get(i), PartR.class);
            saveManyDocuments(batches.get(i),  reactiveCouchbaseTemplate);
            System.out.println("Batch inserted! " + (i + 1) + "/" + batches.size());
        }

        System.out.println("part inserted!");
    }

    public static void loadSuppliers(String filePath, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

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
                            row[3],
                            row[4],
                            Double.parseDouble(row[5]),
                            row[6]
                    );
                })
                .toList();

        var batches = partition(supplierInstances, 200_000);

        System.out.println("Inserting many supplierInstances!");

        for (int i = 0; i < batches.size(); i++) {
            //reactiveCouchbaseTemplate.insert(batches.get(i), SupplierR.class);
            saveManyDocuments(batches.get(i), reactiveCouchbaseTemplate);
            System.out.println("Batch inserted! " + (i + 1) + "/" + batches.size());
        }

        System.out.println("suppliers inserted!");
    }

}
