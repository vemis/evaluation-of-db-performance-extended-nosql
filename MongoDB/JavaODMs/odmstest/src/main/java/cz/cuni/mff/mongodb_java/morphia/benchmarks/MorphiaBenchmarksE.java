package cz.cuni.mff.mongodb_java.morphia.benchmarks;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
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
public class MorphiaBenchmarksE {
    private Datastore datastore;

    /*
    Benchmark              Mode  Cnt     Score     Error  Units
    MorphiaBenchmarksE.C2  avgt    5  1282,559 ± 100,286  ms/op
    */
    //@Benchmark
    public void C2(){
        List<Document> c2 = QueriesMorphiaE.C2(datastore);
    }


    @Setup(Level.Trial)
    public void setup(){
        // 1. Create a MongoClient (connects to local MongoDB by default)
        MongoClient client = MongoClients.create("mongodb://localhost:27017");

        // 2. Configure Morphia
        MapperOptions options = MapperOptions.builder()
                .storeNulls(false)     // <-- THIS makes Morphia write null values
                .build();

        // 3. Create a Datastore instance
        datastore = Morphia.createDatastore(client, "morphia_database_tpch_embedded", options);

        // 4. Tell Morphia to discover your entity classes
        datastore.getMapper().mapPackage("cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded");

        datastore.ensureIndexes();
    }
}
