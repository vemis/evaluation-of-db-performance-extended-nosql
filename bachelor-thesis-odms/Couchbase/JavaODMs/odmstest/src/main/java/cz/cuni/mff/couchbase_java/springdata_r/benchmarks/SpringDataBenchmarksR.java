package cz.cuni.mff.couchbase_java.springdata_r.benchmarks;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import cz.cuni.mff.couchbase_java.springdata_r.CouchbaseSpringBootMainR;
import cz.cuni.mff.couchbase_java.springdata_r.models.CustomerR;
import cz.cuni.mff.couchbase_java.springdata_r.models.CustomerROrdersR;
import cz.cuni.mff.couchbase_java.springdata_r.models.LineitemR;
import cz.cuni.mff.couchbase_java.springdata_r.models.OrdersR;
import cz.cuni.mff.couchbase_java.springdata_r.repositories.CustomerROrdersRRepository;
import cz.cuni.mff.couchbase_java.springdata_r.repositories.OrdersRRepository;
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
public class SpringDataBenchmarksR {
    private AnnotationConfigApplicationContext context;
    private ReactiveCouchbaseTemplate reactiveCouchbaseTemplate;
    private OrdersRRepository ordersRRepository;
    private Cluster cluster;
    private CustomerROrdersRRepository customerROrdersRRepository;

    /**
     * Benchmark                 Mode  Cnt       Score   Error  Units
     * SpringDataBenchmarksR.A1  avgt       114227,833          ms/op
     */
    //@Benchmark
    public void A1(){
        System.out.println("Query A1 started");
        List<LineitemR> a1 = QueriesSpringDataR.A1(reactiveCouchbaseTemplate);
        System.out.println("Query A1 finished");
    }

    /**
     * Benchmark                 Mode  Cnt     Score   Error  Units
     * SpringDataBenchmarksR.A2  avgt    2  7929,505          ms/op
     */
    //@Benchmark
    public void A2(){
        List<OrdersR> a2 = QueriesSpringDataR.A2(reactiveCouchbaseTemplate);
    }

    @Benchmark
    public void A3(){
        List<CustomerR> a3 = QueriesSpringDataR.A3(reactiveCouchbaseTemplate);
    }

    @Benchmark
    public void A4(){
        List<OrdersR> a4 = QueriesSpringDataR.A4(reactiveCouchbaseTemplate);
    }

    /**
     * Benchmark                 Mode  Cnt     Score     Error  Units
     * SpringDataBenchmarksR.B1  avgt    3  5866,886 ± 388,839  ms/op
     */
    //@Benchmark
    public void B1(){
        List<JsonObject> b1 = QueriesSpringDataR.B1(ordersRRepository);
    }

    @Benchmark
    public void B2(){
        List<JsonObject> b2 = QueriesSpringDataR.B2(cluster);
    }

    @Benchmark
    public void C1() throws Exception {
        List<JsonObject> c1 = QueriesSpringDataR.C1(cluster);
    }

    /**
     * Benchmark                 Mode  Cnt      Score   Error  Units
     * SpringDataBenchmarksR.C2  avgt       46867,164          ms/op
     */
    //@Benchmark
    public void C2(){
        List<CustomerROrdersR> c2 = QueriesSpringDataR.C2(customerROrdersRRepository);
    }

    @Benchmark
    public void C3(){
        List<JsonObject> c3 = QueriesSpringDataR.C3(cluster);
    }

    @Benchmark
    public void C4(){
        List<JsonObject> c4 = QueriesSpringDataR.C4(cluster);
    }

    @Benchmark
    public void C5(){
        List<JsonObject> c5 = QueriesSpringDataR.C5(cluster);
    }

    /**
     * Benchmark                 Mode  Cnt    Score     Error  Units
     * SpringDataBenchmarksR.D1  avgt    5  849,118 ± 237,086  ms/op
     */
    @Benchmark
    public  void D1(){
        List<JsonObject> d1 = QueriesSpringDataR.D1(cluster);
    }

    @Benchmark
    public  void D2(){
        List<JsonObject> d2 = QueriesSpringDataR.D2(cluster);
    }

    @Benchmark
    public  void D3(){
        List<JsonObject> d3 = QueriesSpringDataR.D3(cluster);
    }

    @Benchmark
    public  void E1(){
        List<JsonObject> e1 = QueriesSpringDataR.E1(cluster);
    }

    @Benchmark
    public  void E2(){
        List<JsonObject> e2 = QueriesSpringDataR.E2(cluster);
    }

    @Benchmark
    public  void Q1(){
        List<JsonObject> q1 = QueriesSpringDataR.Q1(cluster);
    }

    @Benchmark
    public  void Q2(){
        List<JsonObject> q2 = QueriesSpringDataR.Q2(cluster);
    }

    @Benchmark
    public  void Q3(){
        List<JsonObject> q3 = QueriesSpringDataR.Q3(cluster);
    }

    @Benchmark
    public  void Q4(){
        List<JsonObject> q4 = QueriesSpringDataR.Q4(cluster);
    }

    @Benchmark
    public  void Q5(){
        List<JsonObject> q5 = QueriesSpringDataR.Q5(cluster);
    }

    @Setup(Level.Trial)
    public void setup() {
        //SpringDataMongoAppMainR.main(new String[0]);

        context = new AnnotationConfigApplicationContext(CouchbaseSpringBootMainR.class);
        System.out.println(1);
        System.out.println(context);

        reactiveCouchbaseTemplate = context.getBean(ReactiveCouchbaseTemplate.class);
        System.out.println(2);
        System.out.println(reactiveCouchbaseTemplate);

        ordersRRepository = context.getBean(OrdersRRepository.class);
        System.out.println(3);
        System.out.println(ordersRRepository);

        cluster = context.getBean(Cluster.class);
        System.out.println(4);
        System.out.println(cluster);

        customerROrdersRRepository = context.getBean(CustomerROrdersRRepository.class);
        System.out.println(5);
        System.out.println(customerROrdersRRepository);
    }

}
