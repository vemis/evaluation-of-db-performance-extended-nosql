package cz.cuni.mff.mongodb_java.morphia;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import cz.cuni.mff.mongodb_java.TPCHDatasetLoader;

import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.*;
import dev.morphia.Datastore;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class TPCHDatasetLoaderMorphiaE extends TPCHDatasetLoader {


    public static void loadOrdersEOnlyOCommentIndexed(String filePathOrders, Datastore datastore) {
        List<String[]> orders = readDataFromCustomSeparator(filePathOrders);


        LongAdder counter = new LongAdder();
        int total = orders.size();

        List<OrdersEOnlyOCommentIndexed> orderInstances = orders
                .parallelStream()
                .map(row ->
                {
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

        MongoCollection<OrdersEOnlyOCommentIndexed> collection =
                datastore.getDatabase()
                        .getCollection("ordersEOnlyOCommentIndexed", OrdersEOnlyOCommentIndexed.class);

        System.out.println("Inserting many ordersEOnlyOCommentIndexed!");
        collection.insertMany(orderInstances, new InsertManyOptions().ordered(false));

        System.out.println("ordersEOnlyOComment insertedIndexed!");
    }

    public static void loadOrdersEOnlyOComment(String filePathOrders, Datastore datastore) {
        List<String[]> orders = readDataFromCustomSeparator(filePathOrders);


        LongAdder counter = new LongAdder();
        int total = orders.size();

        List<OrdersEOnlyOComment> orderInstances = orders
                .parallelStream()
                .map(row ->
                {
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

        MongoCollection<OrdersEOnlyOComment> collection =
                datastore.getDatabase()
                        .getCollection("ordersEOnlyOComment", OrdersEOnlyOComment.class);

        System.out.println("Inserting many ordersEOnlyOComment!");
        collection.insertMany(orderInstances, new InsertManyOptions().ordered(false));

        System.out.println("ordersEOnlyOComment inserted!");
    }

    public static void loadOrdersEWithCustomerWithNationWithRegion(
            String filePathOrders,
            String filePathCustomers,
            String filePathNations,
            String filePathRegions,
            Datastore datastore) {

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
                .map(row ->
                {
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

        MongoCollection<OrdersEWithCustomerWithNationWithRegion> collection =
                datastore.getDatabase()
                        .getCollection("ordersEWithCustomerWithNationWithRegion", OrdersEWithCustomerWithNationWithRegion.class);

        System.out.println("Inserting many ordersEWithCustomerWithNationWithRegion!");
        collection.insertMany(orderInstances, new InsertManyOptions().ordered(false));

        System.out.println("ordersEWithCustomerWithNationWithRegion inserted!");
    }

    public static List<CustomerEOnlyCNameCNation> createCustomerEOnlyCNameCNation(String filePath, NationEOnlyNNameNRegion nation) {

        List<String[]> customers = readDataFromCustomSeparator(filePath);

        LongAdder counter = new LongAdder();
        int total = customers.size();

        List<CustomerEOnlyCNameCNation> customerInstances = customers
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new CustomerEOnlyCNameCNation(
                            Integer.parseInt(row[0]),
                            row[1],
                            Integer.parseInt(row[3]),
                            nation
                    );
                })
                .toList();

        return customerInstances;
    }

    public static List<RegionEOnlyName> createRegionEOnlyName(String filePath) {

        List<String[]> regions = readDataFromCustomSeparator(filePath);

        //RegionR[] regionInstances = new RegionR[regions.size()];
        ArrayList<RegionEOnlyName> regionInstances = new ArrayList<>();

        for (int i = 0; i < regions.size(); i++) { //for (String[] row : regions) {
            String[] row = regions.get(i);
            RegionEOnlyName region = new RegionEOnlyName(
                    Integer.parseInt(row[0]),
                    row[2]
            );
            //datastore.save(region);
            //regionInstances[i] = region;
            regionInstances.add(region);
        }

        return regionInstances;
    }

    public static List<NationEOnlyNNameNRegion> createNationEOnlyNNameNRegion(String filePath, RegionEOnlyName region) {

        List<String[]> nations = readDataFromCustomSeparator(filePath);

        ArrayList<NationEOnlyNNameNRegion> nationInstances = new ArrayList<>();

        for (int i = 0; i < nations.size(); i++) { //for (String[] row : nations) {
            String[] row = nations.get(i);
            NationEOnlyNNameNRegion nation = new NationEOnlyNNameNRegion(
                    Integer.parseInt(row[0]),
                    row[1],
                    Integer.parseInt(row[2]),
                    region
            );
            //datastore.save(nation);
            nationInstances.add(nation);
        }


        return nationInstances;
    }

    public static void loadOrdersEWithLineitemsArrayAsTagsindexed(String filePathOrders, String filePathLineitems , Datastore datastore) {
        List<String[]> orders = readDataFromCustomSeparator(filePathOrders);

        List<String[]> lineitems = readDataFromCustomSeparator(filePathLineitems);

        String[] linetemsRow2 = lineitems.get(1); // 2nd row - unique elements

        LongAdder counter = new LongAdder();
        int total = orders.size();

        List<OrdersEWithLineitemsArrayAsTagsIndexed> orderInstances = orders
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new OrdersEWithLineitemsArrayAsTagsIndexed(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            Arrays.asList( getShuffledLineitemsTagsFromRow(linetemsRow2, Integer.parseInt(row[0])) )
                    );
                })
                .toList();

        MongoCollection<OrdersEWithLineitemsArrayAsTagsIndexed> collection =
                datastore.getDatabase()
                        .getCollection("ordersEWithLineitemsArrayAsTagsIndexed", OrdersEWithLineitemsArrayAsTagsIndexed.class);

        System.out.println("Inserting many ordersEWithLineitemsArrayAsTagsIndexed!");
        collection.insertMany(orderInstances, new InsertManyOptions().ordered(false));

        System.out.println("ordersEWithLineitemsArrayAsTagsIndexed inserted!");
    }

    public static void loadOrdersEWithLineitemsArrayAsTags(String filePathOrders, String filePathLineitems , Datastore datastore) {
        List<String[]> orders = readDataFromCustomSeparator(filePathOrders);

        List<String[]> lineitems = readDataFromCustomSeparator(filePathLineitems);

        String[] linetemsRow2 = lineitems.get(1); // 2nd row - unique elements

        LongAdder counter = new LongAdder();
        int total = orders.size();

        List<OrdersEWithLineitemsArrayAsTags> orderInstances = orders
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new OrdersEWithLineitemsArrayAsTags(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            Arrays.asList( getShuffledLineitemsTagsFromRow(linetemsRow2, Integer.parseInt(row[0])) )
                    );
                })
                .toList();

        MongoCollection<OrdersEWithLineitemsArrayAsTags> collection =
                datastore.getDatabase()
                        .getCollection("ordersEWithLineitemsArrayAsTags", OrdersEWithLineitemsArrayAsTags.class);

        System.out.println("Inserting many ordersEWithLineitemsArrayAsTagsInstances!");
        collection.insertMany(orderInstances, new InsertManyOptions().ordered(false));

        System.out.println("ordersEWithLineitemsArrayAsTags inserted!");
    }

    public static void loadOrdersEWithLineitems(String filePath, List<LineitemE> lineitems ,Datastore datastore) {

        List<String[]> orders = readDataFromCustomSeparator(filePath);

        // LineitemE grouped by l_orderkey
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
                            row[4],
                            LocalDate.parse(row[4]),
                            row[5],
                            row[6],
                            row[7],
                            row[8],
                            lineitemsMappedBy_l_orderkey.get( Integer.parseInt(row[0]) )
                    );
                })
                .toList();

        /*
        // Can be saved like this, but very slow
        // Disclaimer - datastore.save(List<>...) is not working - the instance is missing the annotation!
        // Needs to be ArrayList<>
        */

        // Faster approach, but collection needs to be dropped beforehand!
        MongoCollection<OrdersEWithLineitems> collection =
                datastore.getDatabase()
                        .getCollection("ordersEWithLineitems", OrdersEWithLineitems.class);

        System.out.println("Inserting many ordersEWithLineitemsInstances!");
        collection.insertMany(orderInstances, new InsertManyOptions().ordered(false));

        System.out.println("ordersEWithLineitems inserted!");
    }


    public static List<LineitemE> createLineitemsE(String filePath, Datastore datastore) {

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
                            LocalDate.parse( row[11]),
                            LocalDate.parse(row[12]),
                            row[13],
                            row[14],
                            row[15]
                    );
                })
                //.toArray(LineitemR[]::new);
                .toList();

        return lineitemInstances;
    }



    public static List<OrdersE> createOrders(String filePath, Datastore datastore) {

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

        return orderInstances;
    }

    public static void loadCustomersEWithOrders(String filePath, List<OrdersE> orders , Datastore datastore) {

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
                                    .filter(item -> item.get_o_custkey() == Integer.parseInt(row[0]))
                                    .collect(Collectors.toList())
                    );
                })
                .toList();

        datastore.insert(customerInstances);
    }

    public static List<CustomerE> loadCustomers(String filePath, List<OrdersE> orders , Datastore datastore) {

        List<String[]> customers = readDataFromCustomSeparator(filePath);

        /*ArrayList<CustomerE> customerInstances = new ArrayList<>();

        for (int i = 0; i < customers.size(); i++) {//for (String[] row : customers) {
            System.out.println("Customer:" + Integer.toString(i) + "/" + Integer.toString(customers.size()));
            //i++;
            String[] row = customers.get(i);
            CustomerE customer = new CustomerE(
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
            //datastore.save(customer);//WriteConcern. UNACKNOWLEDGED
            customerInstances.add(customer);
        }*/

        LongAdder counter = new LongAdder();
        int total = customers.size();

        List<CustomerE> customerInstances = customers
                .parallelStream()
                .map(row ->
                {
                    counter.increment();
                    long current = counter.sum();

                    if (current % 10_000 == 0) {
                        System.out.println("Processed " + current + " / " + total);
                    }

                    return new CustomerE(
                            Integer.parseInt(row[0]),
                            row[1],
                            row[2],
                            Integer.parseInt(row[3]),
                            row[4],
                            Double.parseDouble(row[5]),
                            row[6],
                            row[7],
                            orders.stream()
                                    .filter(item -> item.get_o_custkey() == Integer.parseInt(row[0]))
                                    .collect(Collectors.toList())
                    );
                })
                .toList();


        return customerInstances;
    }

    public static void loadNations(String filePath, List<CustomerE> customers,  Datastore datastore) {

        List<String[]> nations = readDataFromCustomSeparator(filePath);

        ArrayList<NationE> nationInstances = new ArrayList<>();

        for (int i = 0; i < nations.size(); i++) { //for (String[] row : nations) {
            String[] row = nations.get(i);
            NationE nation = new NationE(
                    Integer.parseInt(row[0]),
                    row[1],
                    Integer.parseInt(row[2]),
                    row[3],
                    customers.stream()
                            .filter(item -> item.getC_nationkey() == Integer.parseInt(row[0]))
                            .collect(Collectors.toList())
            );
            //datastore.save(nation);
            nationInstances.add(nation);
        }

        for (NationE nation : nationInstances) {
            datastore.insert(nation);
        }

        //return nationInstances;
    }

    public static void loadRegions(String filePath, List<NationE> nations, Datastore datastore) {

        List<String[]> regions = readDataFromCustomSeparator(filePath);

        //RegionR[] regionInstances = new RegionR[regions.size()];
        ArrayList<RegionE> regionInstances = new ArrayList<>();

        for (int i = 0; i < regions.size(); i++) { //for (String[] row : regions) {
            String[] row = regions.get(i);
            RegionE region = new RegionE(
                    Integer.parseInt(row[0]),
                    row[1],
                    row[2],
                    nations.stream()
                            .filter(item -> item.getN_regionkey() == Integer.parseInt(row[0]))
                            .collect(Collectors.toList())
            );
            //datastore.save(region);
            //regionInstances[i] = region;
            regionInstances.add(region);
        }

        for (RegionE region : regionInstances) {
            System.out.println("Inserting region");
            datastore.insert(region);
            System.out.println("Region inserted");
        }

    }

}
