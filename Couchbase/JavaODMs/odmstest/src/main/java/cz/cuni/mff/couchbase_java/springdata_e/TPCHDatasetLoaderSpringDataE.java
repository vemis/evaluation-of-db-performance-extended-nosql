package cz.cuni.mff.couchbase_java.springdata_e;


import cz.cuni.mff.couchbase_java.TPCHDatasetLoader;
import cz.cuni.mff.couchbase_java.springdata_e.models.CustomerEWithOrders;
import cz.cuni.mff.couchbase_java.springdata_e.models.LineitemE;
import cz.cuni.mff.couchbase_java.springdata_e.models.OrdersE;
import cz.cuni.mff.couchbase_java.springdata_e.models.OrdersEWithLineitems;
import cz.cuni.mff.couchbase_java.springdata_e.models.CustomerEOnlyCNameCNation;
import cz.cuni.mff.couchbase_java.springdata_e.models.OrdersEOnlyOComment;
import cz.cuni.mff.couchbase_java.springdata_e.models.OrdersEOnlyOCommentIndexed;
import cz.cuni.mff.couchbase_java.springdata_e.models.NationEOnlyNNameNRegion;
import cz.cuni.mff.couchbase_java.springdata_e.models.OrdersEWithCustomerWithNationWithRegion;
import cz.cuni.mff.couchbase_java.springdata_e.models.OrdersEWithLineitemsArrayAsTags;
import cz.cuni.mff.couchbase_java.springdata_e.models.OrdersEWithLineitemsArrayAsTagsIndexed;
import cz.cuni.mff.couchbase_java.springdata_e.models.RegionEOnlyName;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * Loading could be done using constructor, but having doubts abour the performance
 */
public class TPCHDatasetLoaderSpringDataE extends TPCHDatasetLoader {


    public static void loadCustomers(String filePath, List<OrdersE> orders, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        List<String[]> customers = readDataFromCustomSeparator(filePath);

        LongAdder counter = new LongAdder();
        int total = customers.size();

        List<CustomerEWithOrders> customerInstances = customers
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new CustomerEWithOrders(
                            Integer.parseInt(row[0]),
                            row[1],
                            row[2],
                            Integer.parseInt(row[3]),
                            row[4],
                            Double.parseDouble(row[5]),
                            row[6],
                            row[7],
                            orders.stream()
                                    .filter(item -> item.getO_custkey() == Integer.parseInt(row[0]))
                                    .collect(Collectors.toList())
                    );
                })
                .toList();




        /*ArrayList<CustomerEWithOrders> customerInstances = new ArrayList<>();

        for (int i = 0; i < customers.size(); i++) {//for (String[] row : customers) {
            System.out.println("Customer:" + Integer.toString(i) + "/" + Integer.toString(customers.size()));
            //i++;
            String[] row = customers.get(i);
            CustomerEWithOrders customer = new CustomerEWithOrders(
                    Integer.parseInt(row[0]),
                    row[1],
                    row[2],
                    Integer.parseInt(row[3]),
                    row[4],
                    Double.parseDouble(row[5]),
                    row[6],
                    row[7],
                    orders.stream()
                            .filter(item -> item.getO_custkey() == Integer.parseInt(row[0]))
                            .collect(Collectors.toList())
            );
            //reactiveCouchbaseTemplate.save(customer);//WriteConcern. UNACKNOWLEDGED
            customerInstances.add(customer);
        }*/
        //reactiveCouchbaseTemplate.insert(customerInstances, CustomerR.class);
        //saveManyDocuments(customerInstances, reactiveCouchbaseTemplate);

        var batches = partition(customerInstances, 500);

        System.out.println("Inserting many customerInstances!");

        for (int i = 0; i < batches.size(); i++) {
            //reactiveCouchbaseTemplate.insert(batches.get(i), PartsuppR.class);
            saveManyDocuments(batches.get(i), reactiveCouchbaseTemplate);
            System.out.println("Batch inserted! " + (i + 1) + "/" + batches.size());
        }

        System.out.println("customerInstances inserted!");
    }

    public static List<LineitemE> createLineitemsE(String filePath) {

        List<String[]> lineitems = readDataFromCustomSeparator(filePath);

        LongAdder counter = new LongAdder();
        int total = lineitems.size();

        List<LineitemE> lineitemInstances = lineitems
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new LineitemE(
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
                .toList();

        return lineitemInstances;
    }

    public static void loadOrdersEWithLineitems(String filePath, List<LineitemE> lineitems, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        List<String[]> orders = readDataFromCustomSeparator(filePath);

        Map<Integer, List<LineitemE>> lineitemsMappedBy_l_orderkey = groupListsByKey(lineitems, LineitemE::get_l_orderkey);

        LongAdder counter = new LongAdder();
        int total = orders.size();

        List<OrdersEWithLineitems> orderInstances = orders
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new OrdersEWithLineitems(
                            Integer.parseInt(row[0]),
                            Integer.parseInt(row[1]),
                            row[2],
                            Double.parseDouble(row[3]),
                            LocalDate.parse(row[4]),
                            row[5],
                            row[6],
                            row[7],
                            row[8],
                            lineitemsMappedBy_l_orderkey.get(Integer.parseInt(row[0]))
                    );
                })
                .toList();

        var batches = partition(orderInstances, 10_000);

        System.out.println("Inserting many ordersEWithLineitems!");

        for (int i = 0; i < batches.size(); i++) {
            saveManyDocuments(batches.get(i), reactiveCouchbaseTemplate);
            System.out.println("Batch inserted! " + (i + 1) + "/" + batches.size());
        }

        System.out.println("ordersEWithLineitems inserted!");
    }

    public static void loadOrdersEOnlyOComment(String filePathOrders, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        List<String[]> orders = readDataFromCustomSeparator(filePathOrders);

        LongAdder counter = new LongAdder();
        int total = orders.size();

        List<OrdersEOnlyOComment> orderInstances = orders
                .parallelStream()
                .map(row -> {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new OrdersEOnlyOComment(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            row[8]
                    );
                })
                .toList();

        var batches = partition(orderInstances, 10_000);

        System.out.println("Inserting many ordersEOnlyOComment!");

        for (int i = 0; i < batches.size(); i++) {
            saveManyDocuments(batches.get(i), reactiveCouchbaseTemplate);
            System.out.println("Batch inserted! " + (i + 1) + "/" + batches.size());
        }

        System.out.println("ordersEOnlyOComment inserted!");
    }

    public static void loadOrdersEOnlyOCommentIndexed(String filePathOrders, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        List<String[]> orders = readDataFromCustomSeparator(filePathOrders);

        LongAdder counter = new LongAdder();
        int total = orders.size();

        List<OrdersEOnlyOCommentIndexed> orderInstances = orders
                .parallelStream()
                .map(row -> {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new OrdersEOnlyOCommentIndexed(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            row[8]
                    );
                })
                .toList();

        var batches = partition(orderInstances, 10_000);

        System.out.println("Inserting many ordersEOnlyOCommentIndexed!");

        for (int i = 0; i < batches.size(); i++) {
            saveManyDocuments(batches.get(i), reactiveCouchbaseTemplate);
            System.out.println("Batch inserted! " + (i + 1) + "/" + batches.size());
        }

        System.out.println("ordersEOnlyOCommentIndexed inserted!");
    }

    public static void loadOrdersEWithCustomerWithNationWithRegion(
            String filePathOrders,
            String filePathCustomers,
            String filePathNations,
            String filePathRegions,
            ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        List<String[]> orders = readDataFromCustomSeparator(filePathOrders);

        // Build RegionEOnlyName map keyed by r_regionkey
        List<String[]> regionRows = readDataFromCustomSeparator(filePathRegions);
        Map<Integer, RegionEOnlyName> regionMap = new HashMap<>();
        for (String[] row : regionRows) {
            int key = Integer.parseInt(row[0]);
            regionMap.put(key, new RegionEOnlyName(key, row[1]));
        }

        // Build NationEOnlyNNameNRegion map keyed by n_nationkey
        List<String[]> nationRows = readDataFromCustomSeparator(filePathNations);
        Map<Integer, NationEOnlyNNameNRegion> nationMap = new HashMap<>();
        for (String[] row : nationRows) {
            int key = Integer.parseInt(row[0]);
            int regionkey = Integer.parseInt(row[2]);
            nationMap.put(key, new NationEOnlyNNameNRegion(key, row[1], regionkey, regionMap.get(regionkey)));
        }

        // Build CustomerEOnlyCNameCNation map keyed by c_custkey
        List<String[]> customerRows = readDataFromCustomSeparator(filePathCustomers);
        Map<Integer, CustomerEOnlyCNameCNation> customerMap = new HashMap<>();
        for (String[] row : customerRows) {
            int key = Integer.parseInt(row[0]);
            int nationkey = Integer.parseInt(row[3]);
            customerMap.put(key, new CustomerEOnlyCNameCNation(key, row[1], nationkey, nationMap.get(nationkey)));
        }

        LongAdder counter = new LongAdder();
        int total = orders.size();

        List<OrdersEWithCustomerWithNationWithRegion> orderInstances = orders
                .parallelStream()
                .map(row -> {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new OrdersEWithCustomerWithNationWithRegion(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            customerMap.get(Integer.parseInt(row[1]))
                    );
                })
                .toList();

        var batches = partition(orderInstances, 10_000);

        System.out.println("Inserting many ordersEWithCustomerWithNationWithRegion!");

        for (int i = 0; i < batches.size(); i++) {
            saveManyDocuments(batches.get(i), reactiveCouchbaseTemplate);
            System.out.println("Batch inserted! " + (i + 1) + "/" + batches.size());
        }

        System.out.println("ordersEWithCustomerWithNationWithRegion inserted!");
    }

    public static void loadOrdersEWithLineitemsArrayAsTags(String filePathOrders, String filePathLineitems, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        List<String[]> orders = readDataFromCustomSeparator(filePathOrders);
        List<String[]> lineitems = readDataFromCustomSeparator(filePathLineitems);
        String[] linetemsRow2 = lineitems.get(1); // 2nd row — used as source of unique tag values

        LongAdder counter = new LongAdder();
        int total = orders.size();

        List<OrdersEWithLineitemsArrayAsTags> orderInstances = orders
                .parallelStream()
                .map(row -> {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new OrdersEWithLineitemsArrayAsTags(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            Arrays.asList(getShuffledLineitemsTagsFromRow(linetemsRow2, Integer.parseInt(row[0])))
                    );
                })
                .toList();

        var batches = partition(orderInstances, 10_000);

        System.out.println("Inserting many ordersEWithLineitemsArrayAsTags!");

        for (int i = 0; i < batches.size(); i++) {
            saveManyDocuments(batches.get(i), reactiveCouchbaseTemplate);
            System.out.println("Batch inserted! " + (i + 1) + "/" + batches.size());
        }

        System.out.println("ordersEWithLineitemsArrayAsTags inserted!");
    }

    public static void loadOrdersEWithLineitemsArrayAsTagsIndexed(String filePathOrders, String filePathLineitems, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        List<String[]> orders = readDataFromCustomSeparator(filePathOrders);
        List<String[]> lineitems = readDataFromCustomSeparator(filePathLineitems);
        String[] linetemsRow2 = lineitems.get(1); // 2nd row — used as source of unique tag values

        LongAdder counter = new LongAdder();
        int total = orders.size();

        List<OrdersEWithLineitemsArrayAsTagsIndexed> orderInstances = orders
                .parallelStream()
                .map(row -> {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new OrdersEWithLineitemsArrayAsTagsIndexed(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            Arrays.asList(getShuffledLineitemsTagsFromRow(linetemsRow2, Integer.parseInt(row[0])))
                    );
                })
                .toList();

        var batches = partition(orderInstances, 10_000);

        System.out.println("Inserting many ordersEWithLineitemsArrayAsTagsIndexed!");

        for (int i = 0; i < batches.size(); i++) {
            saveManyDocuments(batches.get(i), reactiveCouchbaseTemplate);
            System.out.println("Batch inserted! " + (i + 1) + "/" + batches.size());
        }

        System.out.println("ordersEWithLineitemsArrayAsTagsIndexed inserted!");
    }

    public static List<OrdersE> createOrders(String filePath, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {

        List<String[]> orders = readDataFromCustomSeparator(filePath);

        LongAdder counter = new LongAdder();
        int total = orders.size();

        List<OrdersE> orderInstances = orders
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new OrdersE(
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

        System.out.println("orders created!");

        return orderInstances;
    }



}
