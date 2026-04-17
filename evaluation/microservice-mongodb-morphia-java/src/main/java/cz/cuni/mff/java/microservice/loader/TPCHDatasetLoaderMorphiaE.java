package cz.cuni.mff.java.microservice.loader;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import cz.cuni.mff.java.kurinna.common.loader.ITPCHDatasetLoaderE;
import cz.cuni.mff.java.kurinna.common.utils.TPCHDatasetLoader;
import cz.cuni.mff.java.microservice.model.embedded.*;
import dev.morphia.Datastore;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

@Component
public class TPCHDatasetLoaderMorphiaE extends TPCHDatasetLoader implements ITPCHDatasetLoaderE {

    private final Datastore datastore;

    public TPCHDatasetLoaderMorphiaE(Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public void loadOrdersEOnlyOComment(String dataDirectory) {
        String sep = dataDirectory.endsWith("/") ? "" : "/";
        List<String[]> rows = readDataFromCustomSeparator(dataDirectory + sep + "orders.tbl");
        LongAdder counter = new LongAdder();
        int total = rows.size();

        List<OrdersEOnlyOComment> instances = rows.parallelStream()
                .map(row -> {
                    counter.increment();
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("ordersEOnlyOComment: %d / %d%n", counter.sum(), total);
                    return new OrdersEOnlyOComment(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            row[8]);
                })
                .toList();

        MongoCollection<OrdersEOnlyOComment> col =
                datastore.getDatabase().getCollection("ordersEOnlyOComment", OrdersEOnlyOComment.class);
        col.insertMany(instances, new InsertManyOptions().ordered(false));
        System.out.println("ordersEOnlyOComment inserted.");
    }

    @Override
    public void loadOrdersEOnlyOCommentIndexed(String dataDirectory) {
        String sep = dataDirectory.endsWith("/") ? "" : "/";
        List<String[]> rows = readDataFromCustomSeparator(dataDirectory + sep + "orders.tbl");
        LongAdder counter = new LongAdder();
        int total = rows.size();

        List<OrdersEOnlyOCommentIndexed> instances = rows.parallelStream()
                .map(row -> {
                    counter.increment();
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("ordersEOnlyOCommentIndexed: %d / %d%n", counter.sum(), total);
                    return new OrdersEOnlyOCommentIndexed(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            row[8]);
                })
                .toList();

        MongoCollection<OrdersEOnlyOCommentIndexed> col =
                datastore.getDatabase().getCollection("ordersEOnlyOCommentIndexed", OrdersEOnlyOCommentIndexed.class);
        col.insertMany(instances, new InsertManyOptions().ordered(false));
        System.out.println("ordersEOnlyOCommentIndexed inserted.");
    }

    @Override
    public void loadOrdersEWithLineitemsArrayAsTags(String dataDirectory) {
        String sep = dataDirectory.endsWith("/") ? "" : "/";
        List<String[]> orderRows = readDataFromCustomSeparator(dataDirectory + sep + "orders.tbl");
        List<String[]> lineitemRows = readDataFromCustomSeparator(dataDirectory + sep + "lineitem.tbl");
        String[] tagsRow = lineitemRows.get(1);

        LongAdder counter = new LongAdder();
        int total = orderRows.size();

        List<OrdersEWithLineitemsArrayAsTags> instances = orderRows.parallelStream()
                .map(row -> {
                    counter.increment();
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("ordersEWithLineitemsArrayAsTags: %d / %d%n", counter.sum(), total);
                    return new OrdersEWithLineitemsArrayAsTags(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            Arrays.asList(getShuffledLineitemsTagsFromRow(tagsRow, Integer.parseInt(row[0]))));
                })
                .toList();

        MongoCollection<OrdersEWithLineitemsArrayAsTags> col =
                datastore.getDatabase().getCollection("ordersEWithLineitemsArrayAsTags", OrdersEWithLineitemsArrayAsTags.class);
        col.insertMany(instances, new InsertManyOptions().ordered(false));
        System.out.println("ordersEWithLineitemsArrayAsTags inserted.");
    }

    @Override
    public void loadOrdersEWithLineitemsArrayAsTagsIndexed(String dataDirectory) {
        String sep = dataDirectory.endsWith("/") ? "" : "/";
        List<String[]> orderRows = readDataFromCustomSeparator(dataDirectory + sep + "orders.tbl");
        List<String[]> lineitemRows = readDataFromCustomSeparator(dataDirectory + sep + "lineitem.tbl");
        String[] tagsRow = lineitemRows.get(1);

        LongAdder counter = new LongAdder();
        int total = orderRows.size();

        List<OrdersEWithLineitemsArrayAsTagsIndexed> instances = orderRows.parallelStream()
                .map(row -> {
                    counter.increment();
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("ordersEWithLineitemsArrayAsTagsIndexed: %d / %d%n", counter.sum(), total);
                    return new OrdersEWithLineitemsArrayAsTagsIndexed(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            Arrays.asList(getShuffledLineitemsTagsFromRow(tagsRow, Integer.parseInt(row[0]))));
                })
                .toList();

        MongoCollection<OrdersEWithLineitemsArrayAsTagsIndexed> col =
                datastore.getDatabase().getCollection("ordersEWithLineitemsArrayAsTagsIndexed", OrdersEWithLineitemsArrayAsTagsIndexed.class);
        col.insertMany(instances, new InsertManyOptions().ordered(false));
        System.out.println("ordersEWithLineitemsArrayAsTagsIndexed inserted.");
    }

    @Override
    public void loadOrdersEWithCustomerWithNationWithRegion(String dataDirectory) {
        String sep = dataDirectory.endsWith("/") ? "" : "/";
        List<String[]> regionRows  = readDataFromCustomSeparator(dataDirectory + sep + "region.tbl");
        List<String[]> nationRows  = readDataFromCustomSeparator(dataDirectory + sep + "nation.tbl");
        List<String[]> customerRows = readDataFromCustomSeparator(dataDirectory + sep + "customer.tbl");
        List<String[]> orderRows   = readDataFromCustomSeparator(dataDirectory + sep + "orders.tbl");

        Map<Integer, RegionEOnlyName> regionMap = new HashMap<>();
        for (String[] row : regionRows) {
            int key = Integer.parseInt(row[0]);
            regionMap.put(key, new RegionEOnlyName(key, row[1]));
        }

        Map<Integer, NationEOnlyNNameNRegion> nationMap = new HashMap<>();
        for (String[] row : nationRows) {
            int key = Integer.parseInt(row[0]);
            int regionKey = Integer.parseInt(row[2]);
            nationMap.put(key, new NationEOnlyNNameNRegion(key, row[1], regionKey, regionMap.get(regionKey)));
        }

        Map<Integer, CustomerEOnlyCNameCNation> customerMap = new HashMap<>();
        for (String[] row : customerRows) {
            int key = Integer.parseInt(row[0]);
            int nationKey = Integer.parseInt(row[3]);
            customerMap.put(key, new CustomerEOnlyCNameCNation(key, row[1], nationKey, nationMap.get(nationKey)));
        }

        LongAdder counter = new LongAdder();
        int total = orderRows.size();

        List<OrdersEWithCustomerWithNationWithRegion> instances = orderRows.parallelStream()
                .map(row -> {
                    counter.increment();
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("ordersEWithCustomerWithNationWithRegion: %d / %d%n", counter.sum(), total);
                    return new OrdersEWithCustomerWithNationWithRegion(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            customerMap.get(Integer.parseInt(row[1])));
                })
                .toList();

        MongoCollection<OrdersEWithCustomerWithNationWithRegion> col =
                datastore.getDatabase().getCollection("ordersEWithCustomerWithNationWithRegion", OrdersEWithCustomerWithNationWithRegion.class);
        col.insertMany(instances, new InsertManyOptions().ordered(false));
        System.out.println("ordersEWithCustomerWithNationWithRegion inserted.");
    }

    @Override
    public void loadOrdersEWithLineitems(String dataDirectory) {
        String sep = dataDirectory.endsWith("/") ? "" : "/";
        List<String[]> lineitemRows = readDataFromCustomSeparator(dataDirectory + sep + "lineitem.tbl");
        LongAdder liCounter = new LongAdder();
        int liTotal = lineitemRows.size();

        List<LineitemE> lineitems = lineitemRows.parallelStream()
                .map(row -> {
                    liCounter.increment();
                    if (liCounter.sum() % 10_000 == 0)
                        System.out.printf("LineitemE: %d / %d%n", liCounter.sum(), liTotal);
                    return new LineitemE(
                            Integer.parseInt(row[0]),
                            Integer.parseInt(row[1]),
                            Integer.parseInt(row[2]),
                            Integer.parseInt(row[3]),
                            Integer.parseInt(row[4]),
                            Double.parseDouble(row[5]),
                            Double.parseDouble(row[6]),
                            Double.parseDouble(row[7]),
                            row[8], row[9],
                            LocalDate.parse(row[10]),
                            LocalDate.parse(row[11]),
                            LocalDate.parse(row[12]),
                            row[13], row[14], row[15]);
                })
                .toList();

        Map<Integer, List<LineitemE>> lineitemsByOrder = groupListsByKey(lineitems, LineitemE::get_l_orderkey);

        List<String[]> orderRows = readDataFromCustomSeparator(dataDirectory + sep + "orders.tbl");
        LongAdder counter = new LongAdder();
        int total = orderRows.size();

        List<OrdersEWithLineitems> instances = orderRows.parallelStream()
                .map(row -> {
                    counter.increment();
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("ordersEWithLineitems: %d / %d%n", counter.sum(), total);
                    return new OrdersEWithLineitems(
                            Integer.parseInt(row[0]),
                            Integer.parseInt(row[1]),
                            row[2],
                            Double.parseDouble(row[3]),
                            LocalDate.parse(row[4]),
                            row[5], row[6], row[7], row[8],
                            lineitemsByOrder.get(Integer.parseInt(row[0])));
                })
                .toList();

        MongoCollection<OrdersEWithLineitems> col =
                datastore.getDatabase().getCollection("ordersEWithLineitems", OrdersEWithLineitems.class);
        col.insertMany(instances, new InsertManyOptions().ordered(false));
        System.out.println("ordersEWithLineitems inserted.");
    }
}
