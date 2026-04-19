package cz.cuni.mff.java.microservice.loader;

import cz.cuni.mff.java.kurinna.common.loader.ITPCHDatasetLoaderR;
import cz.cuni.mff.java.kurinna.common.utils.TPCHDatasetLoader;
import cz.cuni.mff.java.microservice.model.relational.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

@Component
public class TPCHDatasetLoaderSpringDataR extends TPCHDatasetLoader implements ITPCHDatasetLoaderR {

    private final MongoTemplate mongoTemplate;

    public TPCHDatasetLoaderSpringDataR(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void loadRegions(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        ArrayList<RegionR> instances = new ArrayList<>();
        for (String[] row : rows) {
            instances.add(new RegionR(Integer.parseInt(row[0]), row[1], row[2]));
        }
        mongoTemplate.insert(instances, RegionR.class);
    }

    @Override
    public void loadNations(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        ArrayList<NationR> instances = new ArrayList<>();
        for (String[] row : rows) {
            instances.add(new NationR(Integer.parseInt(row[0]), row[1], Integer.parseInt(row[2]), row[3]));
        }
        mongoTemplate.insert(instances, NationR.class);
    }

    @Override
    public void loadCustomers(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        ArrayList<CustomerR> instances = new ArrayList<>();
        for (String[] row : rows) {
            instances.add(new CustomerR(
                    Integer.parseInt(row[0]), row[1], row[2],
                    Integer.parseInt(row[3]), row[4],
                    Double.parseDouble(row[5]), row[6], row[7]
            ));
        }
        mongoTemplate.insert(instances, CustomerR.class);
    }

    @Override
    public void loadOrders(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        LongAdder counter = new LongAdder();
        int total = rows.size();

        List<OrdersR> instances = rows.parallelStream()
                .map(row -> {
                    counter.increment();
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("Orders: %d / %d%n", counter.sum(), total);
                    return new OrdersR(
                            Integer.parseInt(row[0]), Integer.parseInt(row[1]),
                            row[2], Double.parseDouble(row[3]),
                            LocalDate.parse(row[4]), row[5], row[6], row[7], row[8]
                    );
                })
                .toList();

        for (List<OrdersR> batch : partition(instances, 200_000)) {
            mongoTemplate.insert(batch, OrdersR.class);
        }
    }

    @Override
    public void loadLineitems(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        LongAdder counter = new LongAdder();
        int total = rows.size();

        List<LineitemR> instances = rows.parallelStream()
                .map(row -> {
                    counter.increment();
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("Lineitems: %d / %d%n", counter.sum(), total);
                    return new LineitemR(
                            Integer.parseInt(row[0]), Integer.parseInt(row[1]),
                            Integer.parseInt(row[2]), Integer.parseInt(row[3]),
                            Integer.parseInt(row[4]), Double.parseDouble(row[5]),
                            Double.parseDouble(row[6]), Double.parseDouble(row[7]),
                            row[8], row[9],
                            LocalDate.parse(row[10]), LocalDate.parse(row[11]),
                            LocalDate.parse(row[12]), row[13], row[14], row[15]
                    );
                })
                .toList();

        for (List<LineitemR> batch : partition(instances, 200_000)) {
            mongoTemplate.insert(batch, LineitemR.class);
        }
    }

    @Override
    public void loadPartsupps(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        LongAdder counter = new LongAdder();
        int total = rows.size();

        List<PartsuppR> instances = rows.parallelStream()
                .map(row -> {
                    counter.increment();
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("Partsupps: %d / %d%n", counter.sum(), total);
                    return new PartsuppR(
                            Integer.parseInt(row[0]), Integer.parseInt(row[1]),
                            Integer.parseInt(row[2]), Double.parseDouble(row[3]), row[4]
                    );
                })
                .toList();

        for (List<PartsuppR> batch : partition(instances, 200_000)) {
            mongoTemplate.insert(batch, PartsuppR.class);
        }
    }

    @Override
    public void loadParts(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        LongAdder counter = new LongAdder();
        int total = rows.size();

        List<PartR> instances = rows.parallelStream()
                .map(row -> {
                    counter.increment();
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("Parts: %d / %d%n", counter.sum(), total);
                    return new PartR(
                            Integer.parseInt(row[0]), row[1], row[2], row[3], row[4],
                            Integer.parseInt(row[5]), row[6], Double.parseDouble(row[7]), row[8]
                    );
                })
                .toList();

        for (List<PartR> batch : partition(instances, 200_000)) {
            mongoTemplate.insert(batch, PartR.class);
        }
    }

    @Override
    public void loadSuppliers(String filePath) {
        List<String[]> rows = readDataFromCustomSeparator(filePath);
        LongAdder counter = new LongAdder();
        int total = rows.size();

        List<SupplierR> instances = rows.parallelStream()
                .map(row -> {
                    counter.increment();
                    if (counter.sum() % 10_000 == 0)
                        System.out.printf("Suppliers: %d / %d%n", counter.sum(), total);
                    return new SupplierR(
                            Integer.parseInt(row[0]), row[1], row[2],
                            Integer.parseInt(row[3]), row[4],
                            Double.parseDouble(row[5]), row[6]
                    );
                })
                .toList();

        for (List<SupplierR> batch : partition(instances, 200_000)) {
            mongoTemplate.insert(batch, SupplierR.class);
        }
    }
}
