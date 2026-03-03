package cz.cuni.mff.mongodb_java.morphia;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import cz.cuni.mff.mongodb_java.TPCHDatasetLoader;

import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded.*;
import dev.morphia.Datastore;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class TPCHDatasetLoaderMorphiaE extends TPCHDatasetLoader {
    public static List<OrdersE> loadOrders(String filePath, Datastore datastore) {

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
                            row[4],
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
                                    .filter(item -> item.getO_custkey() == Integer.parseInt(row[0]))
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
                                    .filter(item -> item.getO_custkey() == Integer.parseInt(row[0]))
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
