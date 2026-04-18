package cz.cuni.mff.java.microservice.loader;

import cz.cuni.mff.java.kurinna.common.loader.ITPCHDatasetLoaderE;
import cz.cuni.mff.java.kurinna.common.utils.TPCHDatasetLoader;
import cz.cuni.mff.java.microservice.model.embedded.*;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

@Component
public class TPCHDatasetLoaderCouchbaseE extends TPCHDatasetLoader implements ITPCHDatasetLoaderE {

    private static final String SCOPE = "spring_scope_e";
    private static final int BATCH_SIZE = 5_000;

    private final CouchbaseTemplate template;

    public TPCHDatasetLoaderCouchbaseE(CouchbaseTemplate template) {
        this.template = template;
    }

    @Override
    public void loadOrdersEOnlyOComment(String dataDirectory) {
        String sep = sep(dataDirectory);
        List<String[]> rows = readDataFromCustomSeparator(dataDirectory + sep + "orders.tbl");
        LongAdder counter = new LongAdder();
        List<OrdersEOnlyOComment> instances = rows.parallelStream()
                .map(row -> {
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("ordersEOnlyOComment: %d / %d%n", counter.sum(), rows.size());
                    counter.increment();
                    return new OrdersEOnlyOComment(Integer.parseInt(row[0]), LocalDate.parse(row[4]), row[8]);
                })
                .toList();
        batchInsert(instances, OrdersEOnlyOComment.class, SCOPE, "OrdersEOnlyOComment");
        System.out.println("ordersEOnlyOComment inserted.");
    }

    @Override
    public void loadOrdersEOnlyOCommentIndexed(String dataDirectory) {
        String sep = sep(dataDirectory);
        List<String[]> rows = readDataFromCustomSeparator(dataDirectory + sep + "orders.tbl");
        LongAdder counter = new LongAdder();
        List<OrdersEOnlyOCommentIndexed> instances = rows.parallelStream()
                .map(row -> {
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("ordersEOnlyOCommentIndexed: %d / %d%n", counter.sum(), rows.size());
                    counter.increment();
                    return new OrdersEOnlyOCommentIndexed(Integer.parseInt(row[0]), LocalDate.parse(row[4]), row[8]);
                })
                .toList();
        batchInsert(instances, OrdersEOnlyOCommentIndexed.class, SCOPE, "OrdersEOnlyOCommentIndexed");
        System.out.println("ordersEOnlyOCommentIndexed inserted.");
    }

    @Override
    public void loadOrdersEWithLineitemsArrayAsTags(String dataDirectory) {
        String sep = sep(dataDirectory);
        List<String[]> orderRows = readDataFromCustomSeparator(dataDirectory + sep + "orders.tbl");
        List<String[]> lineitemRows = readDataFromCustomSeparator(dataDirectory + sep + "lineitem.tbl");
        String[] tagsRow = lineitemRows.get(1);

        LongAdder counter = new LongAdder();
        List<OrdersEWithLineitemsArrayAsTags> instances = orderRows.parallelStream()
                .map(row -> {
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("ordersEWithLineitemsArrayAsTags: %d / %d%n", counter.sum(), orderRows.size());
                    counter.increment();
                    return new OrdersEWithLineitemsArrayAsTags(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            Arrays.asList(getShuffledLineitemsTagsFromRow(tagsRow, Integer.parseInt(row[0]))));
                })
                .toList();
        batchInsert(instances, OrdersEWithLineitemsArrayAsTags.class, SCOPE, "OrdersEWithLineitemsArrayAsTags");
        System.out.println("ordersEWithLineitemsArrayAsTags inserted.");
    }

    @Override
    public void loadOrdersEWithLineitemsArrayAsTagsIndexed(String dataDirectory) {
        String sep = sep(dataDirectory);
        List<String[]> orderRows = readDataFromCustomSeparator(dataDirectory + sep + "orders.tbl");
        List<String[]> lineitemRows = readDataFromCustomSeparator(dataDirectory + sep + "lineitem.tbl");
        String[] tagsRow = lineitemRows.get(1);

        LongAdder counter = new LongAdder();
        List<OrdersEWithLineitemsArrayAsTagsIndexed> instances = orderRows.parallelStream()
                .map(row -> {
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("ordersEWithLineitemsArrayAsTagsIndexed: %d / %d%n", counter.sum(), orderRows.size());
                    counter.increment();
                    return new OrdersEWithLineitemsArrayAsTagsIndexed(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            Arrays.asList(getShuffledLineitemsTagsFromRow(tagsRow, Integer.parseInt(row[0]))));
                })
                .toList();
        batchInsert(instances, OrdersEWithLineitemsArrayAsTagsIndexed.class, SCOPE, "OrdersEWithLineitemsArrayAsTagsIndexed");
        System.out.println("ordersEWithLineitemsArrayAsTagsIndexed inserted.");
    }

    @Override
    public void loadOrdersEWithCustomerWithNationWithRegion(String dataDirectory) {
        String sep = sep(dataDirectory);

        Map<Integer, RegionEOnlyName> regionMap = new HashMap<>();
        for (String[] row : readDataFromCustomSeparator(dataDirectory + sep + "region.tbl")) {
            int key = Integer.parseInt(row[0]);
            regionMap.put(key, new RegionEOnlyName(key, row[1]));
        }

        Map<Integer, NationEOnlyNNameNRegion> nationMap = new HashMap<>();
        for (String[] row : readDataFromCustomSeparator(dataDirectory + sep + "nation.tbl")) {
            int key = Integer.parseInt(row[0]);
            int regionKey = Integer.parseInt(row[2]);
            nationMap.put(key, new NationEOnlyNNameNRegion(key, row[1], regionKey, regionMap.get(regionKey)));
        }

        Map<Integer, CustomerEOnlyCNameCNation> customerMap = new HashMap<>();
        for (String[] row : readDataFromCustomSeparator(dataDirectory + sep + "customer.tbl")) {
            int key = Integer.parseInt(row[0]);
            int nationKey = Integer.parseInt(row[3]);
            customerMap.put(key, new CustomerEOnlyCNameCNation(key, row[1], nationKey, nationMap.get(nationKey)));
        }

        List<String[]> orderRows = readDataFromCustomSeparator(dataDirectory + sep + "orders.tbl");
        LongAdder counter = new LongAdder();
        List<OrdersEWithCustomerWithNationWithRegion> instances = orderRows.parallelStream()
                .map(row -> {
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("ordersEWithCustomerWithNationWithRegion: %d / %d%n", counter.sum(), orderRows.size());
                    counter.increment();
                    return new OrdersEWithCustomerWithNationWithRegion(
                            Integer.parseInt(row[0]),
                            LocalDate.parse(row[4]),
                            customerMap.get(Integer.parseInt(row[1])));
                })
                .toList();
        batchInsert(instances, OrdersEWithCustomerWithNationWithRegion.class, SCOPE, "OrdersEWithCustomerWithNationWithRegion");
        System.out.println("ordersEWithCustomerWithNationWithRegion inserted.");
    }

    @Override
    public void loadOrdersEWithLineitems(String dataDirectory) {
        String sep = sep(dataDirectory);
        List<String[]> lineitemRows = readDataFromCustomSeparator(dataDirectory + sep + "lineitem.tbl");
        LongAdder liCounter = new LongAdder();

        List<LineitemE> lineitems = lineitemRows.parallelStream()
                .map(row -> {
                    if (liCounter.sum() % 10_000 == 0)
                        System.out.printf("LineitemE: %d / %d%n", liCounter.sum(), lineitemRows.size());
                    liCounter.increment();
                    return new LineitemE(
                            Integer.parseInt(row[0]), Integer.parseInt(row[1]),
                            Integer.parseInt(row[2]), Integer.parseInt(row[3]),
                            Integer.parseInt(row[4]),
                            Double.parseDouble(row[5]), Double.parseDouble(row[6]),
                            Double.parseDouble(row[7]),
                            row[8], row[9],
                            LocalDate.parse(row[10]), LocalDate.parse(row[11]),
                            LocalDate.parse(row[12]),
                            row[13], row[14], row[15]);
                })
                .toList();

        Map<Integer, List<LineitemE>> lineitemsByOrder = groupListsByKey(lineitems, LineitemE::get_l_orderkey);

        List<String[]> orderRows = readDataFromCustomSeparator(dataDirectory + sep + "orders.tbl");
        LongAdder counter = new LongAdder();
        List<OrdersEWithLineitems> instances = orderRows.parallelStream()
                .map(row -> {
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("ordersEWithLineitems: %d / %d%n", counter.sum(), orderRows.size());
                    counter.increment();
                    return new OrdersEWithLineitems(
                            Integer.parseInt(row[0]), Integer.parseInt(row[1]),
                            row[2], Double.parseDouble(row[3]), LocalDate.parse(row[4]),
                            row[5], row[6], row[7], row[8],
                            lineitemsByOrder.get(Integer.parseInt(row[0])));
                })
                .toList();
        batchInsert(instances, OrdersEWithLineitems.class, SCOPE, "OrdersEWithLineitems");
        System.out.println("ordersEWithLineitems inserted.");
    }

    private <T> void batchInsert(List<T> entities, Class<T> clazz, String scope, String collection) {
        List<List<T>> batches = partition(entities, BATCH_SIZE);
        for (int i = 0; i < batches.size(); i++) {
            template.insertById(clazz)
                    .inScope(scope)
                    .inCollection(collection)
                    .all(batches.get(i));
            System.out.printf("%s batch %d/%d inserted%n", collection, i + 1, batches.size());
        }
    }

    private static String sep(String dir) {
        return dir.endsWith("/") ? "" : "/";
    }
}
