package cz.cuni.mff.couchbase_java.springdata_e.benchmarks;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import cz.cuni.mff.couchbase_java.springdata_e.CouchbaseSpringBootMainE;
import org.openjdk.jmh.annotations.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class SpringDataBenchmarksE {
    private AnnotationConfigApplicationContext context;
    private ReactiveCouchbaseTemplate reactiveCouchbaseTemplate;
    private Cluster cluster;


    /**
     * Benchmark                 Mode  Cnt     Score     Error  Units
     * SpringDataBenchmarksE.C2  avgt    5  3512,466 ± 840,590  ms/op
     */
    @Benchmark
    public void C2(){
        List<JsonObject> c2 = QueriesSpringDataE.C2(cluster);
    }


    @Setup(Level.Trial)
    public void setup() {
        //SpringDataMongoAppMainR.main(new String[0]);

        context = new AnnotationConfigApplicationContext(CouchbaseSpringBootMainE.class);
        System.out.println(1);
        System.out.println(context);

        cluster = context.getBean(Cluster.class);
        System.out.println(4);
        System.out.println(cluster);

    }

}
