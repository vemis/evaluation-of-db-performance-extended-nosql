package cz.cuni.mff.java.microservice.loader;

import cz.cuni.mff.java.kurinna.common.loader.ITPCHDatasetLoaderR;
import cz.cuni.mff.java.kurinna.common.utils.TPCHDatasetLoader;
import cz.cuni.mff.java.microservice.model.relational.*;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

@Component
public class TPCHDatasetLoaderCouchbaseR extends TPCHDatasetLoader implements ITPCHDatasetLoaderR {

    private static final String SCOPE = "spring_scope_r";
    private static final int BATCH_SIZE = 5_000;

    private final CouchbaseTemplate template;

    public TPCHDatasetLoaderCouchbaseR(CouchbaseTemplate template) {
        this.template = template;
    }

    @Override
    public void loadRegions(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        List<RegionR> instances = rows.stream()
                .map(row -> new RegionR(Integer.parseInt(row[0]), row[1], row[2]))
                .toList();
        batchInsert(instances, RegionR.class, SCOPE, "RegionR");
        System.out.println("RegionR inserted: " + instances.size());
    }

    @Override
    public void loadNations(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        List<NationR> instances = rows.stream()
                .map(row -> new NationR(Integer.parseInt(row[0]), row[1], row[2], row[3]))
                .toList();
        batchInsert(instances, NationR.class, SCOPE, "NationR");
        System.out.println("NationR inserted: " + instances.size());
    }

    @Override
    public void loadCustomers(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        List<CustomerR> instances = rows.stream()
                .map(row -> new CustomerR(
                        Integer.parseInt(row[0]), row[1], row[2], row[3],
                        row[4], Double.parseDouble(row[5]), row[6], row[7]))
                .toList();
        batchInsert(instances, CustomerR.class, SCOPE, "CustomerR");
        System.out.println("CustomerR inserted: " + instances.size());
    }

    @Override
    public void loadOrders(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        LongAdder counter = new LongAdder();
        int total = rows.size();
        List<OrdersR> instances = rows.parallelStream()
                .map(row -> {
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("Orders: %d / %d%n", counter.sum(), total);
                    counter.increment();
                    return new OrdersR(
                            Integer.parseInt(row[0]), row[1], row[2],
                            Double.parseDouble(row[3]), LocalDate.parse(row[4]),
                            row[5], row[6], row[7], row[8]);
                })
                .toList();
        batchInsert(instances, OrdersR.class, SCOPE, "OrdersR");
        System.out.println("OrdersR inserted: " + instances.size());
    }

    @Override
    public void loadLineitems(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        LongAdder counter = new LongAdder();
        int total = rows.size();
        List<LineitemR> instances = rows.parallelStream()
                .map(row -> {
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("Lineitems: %d / %d%n", counter.sum(), total);
                    counter.increment();
                    return new LineitemR(
                            row[0], row[1], row[2],
                            Integer.parseInt(row[3]), Integer.parseInt(row[4]),
                            Double.parseDouble(row[5]), Double.parseDouble(row[6]),
                            Double.parseDouble(row[7]),
                            row[8], row[9],
                            LocalDate.parse(row[10]), LocalDate.parse(row[11]),
                            LocalDate.parse(row[12]),
                            row[13], row[14], row[15]);
                })
                .toList();
        batchInsert(instances, LineitemR.class, SCOPE, "LineitemR");
        System.out.println("LineitemR inserted: " + instances.size());
    }

    @Override
    public void loadPartsupps(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        LongAdder counter = new LongAdder();
        int total = rows.size();
        List<PartsuppR> instances = rows.parallelStream()
                .map(row -> {
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("Partsupps: %d / %d%n", counter.sum(), total);
                    counter.increment();
                    return new PartsuppR(
                            row[0], row[1],
                            Integer.parseInt(row[2]), Double.parseDouble(row[3]), row[4]);
                })
                .toList();
        batchInsert(instances, PartsuppR.class, SCOPE, "PartsuppR");
        System.out.println("PartsuppR inserted: " + instances.size());
    }

    @Override
    public void loadParts(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        LongAdder counter = new LongAdder();
        int total = rows.size();
        List<PartR> instances = rows.parallelStream()
                .map(row -> {
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("Parts: %d / %d%n", counter.sum(), total);
                    counter.increment();
                    return new PartR(
                            Integer.parseInt(row[0]), row[1], row[2], row[3], row[4],
                            Integer.parseInt(row[5]), row[6], Double.parseDouble(row[7]), row[8]);
                })
                .toList();
        batchInsert(instances, PartR.class, SCOPE, "PartR");
        System.out.println("PartR inserted: " + instances.size());
    }

    @Override
    public void loadSuppliers(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        LongAdder counter = new LongAdder();
        int total = rows.size();
        List<SupplierR> instances = rows.parallelStream()
                .map(row -> {
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("Suppliers: %d / %d%n", counter.sum(), total);
                    counter.increment();
                    return new SupplierR(
                            Integer.parseInt(row[0]), row[1], row[2], row[3],
                            row[4], Double.parseDouble(row[5]), row[6]);
                })
                .toList();
        batchInsert(instances, SupplierR.class, SCOPE, "SupplierR");
        System.out.println("SupplierR inserted: " + instances.size());
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
}
