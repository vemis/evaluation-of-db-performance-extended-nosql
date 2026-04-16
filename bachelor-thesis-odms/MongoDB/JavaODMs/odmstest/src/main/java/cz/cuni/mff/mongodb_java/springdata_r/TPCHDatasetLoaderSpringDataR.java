package cz.cuni.mff.mongodb_java.springdata_r;



import cz.cuni.mff.mongodb_java.TPCHDatasetLoader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import cz.cuni.mff.mongodb_java.springdata_r.model.*;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Loading could be done using constructor, but having doubts abour the performance
 */
public class TPCHDatasetLoaderSpringDataR extends TPCHDatasetLoader {

    public static void loadRegions(String filePath, MongoTemplate mongoTemplate) {

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
            //mongoTemplate.save(region);
            //regionInstances[i] = region;
            regionInstances.add(region);
        }
        //mongoTemplate.save(regionInstances);
        mongoTemplate.insert(regionInstances, RegionR.class);
    }

    public static void loadNations(String filePath, MongoTemplate mongoTemplate) {

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
            //mongoTemplate.save(nation);
            nationInstances.add(nation);
        }
        mongoTemplate.insert(nationInstances,  NationR.class);
    }

    public static void loadCustomers(String filePath, MongoTemplate mongoTemplate) {

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
            //mongoTemplate.save(customer);//WriteConcern. UNACKNOWLEDGED
            customerInstances.add(customer);
        }
        mongoTemplate.insert(customerInstances, CustomerR.class);
    }

    public static void loadOrders(String filePath, MongoTemplate mongoTemplate) {

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

        mongoTemplate.insert(orderInstances, OrdersR.class);

        System.out.println("orders inserted!");
    }



    public static void loadLineitems(String filePath, MongoTemplate mongoTemplate) {

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

        for (var batch : batches) {
            mongoTemplate.insert(batch, LineitemR.class);
            System.out.println("Batch inserted!");
        }

        System.out.println("lineitemInstances inserted!");
    }

    public static void loadPartsupps(String filePath, MongoTemplate mongoTemplate) {

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

        var batches = partition(partsuppInstances, 200_000);

        System.out.println("Inserting many partsuppInstances!");

        for (var batch : batches) {
            mongoTemplate.insert(batch, PartsuppR.class);
            System.out.println("Batch inserted!");
        }

        System.out.println("partsupp inserted!");
    }

    public static void loadParts(String filePath, MongoTemplate mongoTemplate) {

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

        for (var batch : batches) {
            mongoTemplate.insert(batch, PartR.class);
            System.out.println("Batch inserted!");
        }

        System.out.println("part inserted!");
    }

    public static void loadSuppliers(String filePath, MongoTemplate mongoTemplate) {

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

        var batches = partition(supplierInstances, 200_000);

        System.out.println("Inserting many supplierInstances!");

        for (var batch : batches) {
            mongoTemplate.insert(batch, SupplierR.class);
            System.out.println("Batch inserted!");
        }

        System.out.println("suppliers inserted!");
    }

}
