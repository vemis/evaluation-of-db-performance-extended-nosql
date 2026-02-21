package cz.cuni.mff.mongodb_java.morphia.benchmarks;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.LineitemR;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.OrdersR;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import org.bson.Document;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class MorphiaBenchmarksR {
    private Datastore datastore;

    /**
     * Benchmark              Mode  Cnt      Score      Error  Units
     * MorphiaBenchmarksR.A1  avgt    5  25410,032 ± 1757,310  ms/op
     */
    //@Benchmark
    public void A1(){
        List<LineitemR> a1 = QueriesR.A1(datastore);
    }

    /**
     * Benchmark              Mode  Cnt    Score     Error  Units
     * MorphiaBenchmarksR.A2  avgt    5  769,130 ± 206,017  ms/op
     */
    //@Benchmark
    public void A2(){
        List<OrdersR> a2 = QueriesR.A2(datastore);
    }

    /**
     * Benchmark              Mode  Cnt    Score    Error  Units
     * MorphiaBenchmarksR.A3  avgt    5  274,807 ± 73,967  ms/op
     */
    //@Benchmark
    public  void A3(){
        List<Document> a3 = QueriesR.B1(datastore);
    }

    /**
     * Benchmark              Mode  Cnt     Score       Error  Units
     * MorphiaBenchmarksR.C2  avgt    5  9484,373 ± 11313,979  ms/op
     */
    //@Benchmark
    public void C2(){
        List<Document> c2 = QueriesR.C2(datastore);
    }

    /**
     * Benchmark              Mode  Cnt   Score   Error  Units
     * MorphiaBenchmarksR.D1  avgt    5  51,989 ± 8,473  ms/op
     */
    //@Benchmark
    public  void D1(){
        List<Document> d1 = QueriesR.D1(datastore);
    }

    @Setup(Level.Trial)
    public void setup() {
        // Build complex object here
        // 1. Create a MongoClient (connects to local MongoDB by default)
        MongoClient client = MongoClients.create("mongodb://localhost:27017");

        // 2. Configure Morphia
        MapperOptions options = MapperOptions.builder()
                .storeNulls(false)     // <-- THIS makes Morphia write null values
                .build();

        // 3. Create a Datastore instance
        datastore = Morphia.createDatastore(client, "morphia_database_tpch_relational", options);

        // 4. Tell Morphia to discover your entity classes
        datastore.getMapper().mapPackage("cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational");

        datastore.ensureIndexes();
    }

}
