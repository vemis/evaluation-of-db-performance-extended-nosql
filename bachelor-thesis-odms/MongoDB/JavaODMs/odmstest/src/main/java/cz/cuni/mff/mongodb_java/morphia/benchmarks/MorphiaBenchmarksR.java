package cz.cuni.mff.mongodb_java.morphia.benchmarks;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational.CustomerR;
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
        List<LineitemR> a1 = QueriesMorphiaR.A1(datastore);
    }

    /**
     * Benchmark              Mode  Cnt    Score     Error  Units
     * MorphiaBenchmarksR.A2  avgt    5  769,130 ± 206,017  ms/op
     */
    //@Benchmark
    public void A2(){
        List<OrdersR> a2 = QueriesMorphiaR.A2(datastore);
    }


    //MorphiaBenchmarksR.A3  avgt    5   297,817 ±  32,489  ms/op
    //@Benchmark
    public  void A3(){
        List<CustomerR> a3 = QueriesMorphiaR.A3(datastore);
    }


    //MorphiaBenchmarksR.A4  avgt    5   135,689 ±   3,186  ms/op
    //@Benchmark
    public void A4(){
        List<OrdersR> a4 = QueriesMorphiaR.A4(datastore);
    }

    //MorphiaBenchmarksR.B1  avgt    5   249,079 ±  31,396  ms/op
    //@Benchmark
    public  void B1(){
        List<Document> b1 = QueriesMorphiaR.B1(datastore);
    }

    //MorphiaBenchmarksR.B2  avgt    5  1156,219 ± 180,760  ms/op
    //@Benchmark
    public  void B2(){
        List<Document> b2 = QueriesMorphiaR.B2(datastore);
    }


    //MorphiaBenchmarksR.C1  avgt    5  8698,324 ± 554,852  ms/op
    //@Benchmark
    public void C1(){
        List<Document> c1 = QueriesMorphiaR.C1(datastore);
    }

    /**
     * Benchmark              Mode  Cnt     Score       Error  Units
     * MorphiaBenchmarksR.C2  avgt    5  9484,373 ± 11313,979  ms/op
     */
    //@Benchmark
    public void C2(){
        List<Document> c2 = QueriesMorphiaR.C2(datastore);
    }

    //MorphiaBenchmarksR.C3  avgt    5  7197,243 ± 698,923  ms/op
    //@Benchmark
    public void C3(){
        List<Document> c3 = QueriesMorphiaR.C3(datastore);
    }

    //MorphiaBenchmarksR.C4  avgt    5  9273,343 ± 713,795  ms/op
    //@Benchmark
    public void C4(){
        List<Document> c4 = QueriesMorphiaR.C4(datastore);
    }

    //MorphiaBenchmarksR.C5  avgt    5  5262,147 ± 927,128  ms/op
    //@Benchmark
    public void C5(){
        List<Document> c5 = QueriesMorphiaR.C5(datastore);
    }

    /**
     * Benchmark              Mode  Cnt   Score   Error  Units
     * MorphiaBenchmarksR.D1  avgt    5  51,989 ± 8,473  ms/op
     */
    //@Benchmark
    public  void D1(){
        List<Document> d1 = QueriesMorphiaR.D1(datastore);
    }

    //MorphiaBenchmarksR.D2  avgt    5  1365,556 ± 103,776  ms/op
    //@Benchmark
    public void D2(){
        List<Document> d2 = QueriesMorphiaR.D2(datastore);
    }

    //MorphiaBenchmarksR.D3  avgt    5   332,025 ±  35,339  ms/op
    //@Benchmark
    public void D3(){
        List<Document> d3 = QueriesMorphiaR.D3(datastore);
    }

    //MorphiaBenchmarksR.E1  avgt    5   248,624 ±  21,835  ms/op
    //@Benchmark
    public void E1(){
        List<CustomerR> e1 = QueriesMorphiaR.E1(datastore);
    }

    //MorphiaBenchmarksR.E2  avgt    5  2517,833 ± 236,235  ms/op
    //@Benchmark
    public void E2(){
        List<OrdersR> e2 = QueriesMorphiaR.E2(datastore);
    }

    //MorphiaBenchmarksR.E3  avgt    5    18,525 ±   0,563  ms/op
    //@Benchmark
    public void E3(){
        List<Document> e3 = QueriesMorphiaR.E3(datastore);
    }

    //MorphiaBenchmarksR.Q1  avgt    5  2427,137 ± 278,735  ms/op
    //@Benchmark
    public void Q1(){
        List<Document> q1 = QueriesMorphiaR.Q1(datastore);
    }

    //MorphiaBenchmarksR.Q2  avgt    5    176,005 ±   56,392  ms/op
    //@Benchmark
    public void Q2(){
        List<Document> q2 = QueriesMorphiaR.Q2(datastore);
    }

    //MorphiaBenchmarksR.Q3  avgt    5   4130,161 ±  649,557  ms/op
    //@Benchmark
    public void Q3(){
        List<Document> q3 = QueriesMorphiaR.Q3(datastore);
    }

    //MorphiaBenchmarksR.Q4  avgt    5   2233,994 ±  287,848  ms/op
    //@Benchmark
    public void Q4(){
        List<Document> q4 = QueriesMorphiaR.Q4(datastore);
    }

    //MorphiaBenchmarksR.Q5  avgt    5  19415,863 ± 1729,709  ms/op
    //@Benchmark
    public void Q5(){
        List<Document> q5 = QueriesMorphiaR.Q5(datastore);
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