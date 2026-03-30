package cz.cuni.mff.mongodb_java.springdata_r.benchmarks;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import cz.cuni.mff.mongodb_java.springdata_r.SpringDataMongoAppMainR;
import cz.cuni.mff.mongodb_java.springdata_r.config.MongoConfigR;
import cz.cuni.mff.mongodb_java.springdata_r.model.*;
import cz.cuni.mff.mongodb_java.springdata_r.service.LogicServiceR;
import dev.morphia.Datastore;

import dev.morphia.mapping.MapperOptions;
import org.bson.Document;
import org.openjdk.jmh.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class SpringDataBenchmarksR {
    private LogicServiceR logicServiceR;
    private AnnotationConfigApplicationContext context;

    /**
     * Benchmark                 Mode  Cnt      Score      Error  Units
     * SpringDataBenchmarksR.A1  avgt    5  22454,750 ± 6740,384  ms/op
     */
    //@Benchmark
    public void A1(){
        List<LineitemR> a1 = logicServiceR.A1();
    }

    /**
     * Benchmark                 Mode  Cnt    Score     Error  Units
     * SpringDataBenchmarksR.A2  avgt    5  724,200 ± 193,602  ms/op
     */
    //@Benchmark
    public void A2(){
        List<OrdersR> a2 = logicServiceR.A2();
    }

    @Benchmark
    public void A3(){
        List<CustomerR> a3 = logicServiceR.A3();
    }

    @Benchmark
    public void A4(){
        List<OrdersR> a4 = logicServiceR.A4();
    }


    /**
     * Benchmark                 Mode  Cnt    Score     Error  Units
     * SpringDataBenchmarksR.B1  avgt    5  461,682 ± 580,270  ms/op
     */
    //@Benchmark
    public void B1(){
        List<Document> b1 = logicServiceR.B1();
    }

    @Benchmark
    public void B2(){
        List<Document> b2 = logicServiceR.B2();
    }

    @Benchmark
    public void C1(){
        List<Document> c1 = logicServiceR.C1();
    }

    /**
     * Benchmark                 Mode  Cnt     Score      Error  Units
     * SpringDataBenchmarksR.C2  avgt    5  6119,081 ± 3448,677  ms/op
     */
    //@Benchmark
    public void C2(){
        List<Document> c2 = logicServiceR.C2();
    }

    @Benchmark
    public void C3(){
        List<Document> c3 = logicServiceR.C3();
    }

    @Benchmark
    public void C4(){
        List<Document> c4 = logicServiceR.C4();
    }

    @Benchmark
    public void C5(){
        List<Document> c5 = logicServiceR.C5();
    }

    /**
     * Benchmark                 Mode  Cnt   Score   Error  Units
     * SpringDataBenchmarksR.D1  avgt    5  46,793 ± 2,847  ms/op
     */
    @Benchmark
    public  void D1(){
        List<Document> d1 = logicServiceR.D1();
    }

    @Benchmark
    public  void D2(){
        List<Document> d2 = logicServiceR.D2();
    }

    @Benchmark
    public  void D3(){
        List<Document> d3 = logicServiceR.D3();
    }

    @Benchmark
    public  void E1(){
        List<Document> e1 = logicServiceR.E1();
    }

    @Benchmark
    public  void E2(){
        List<OrdersR> e2 = logicServiceR.E2();
    }

    @Benchmark
    public  void Q1(){
        List<Document> q1 = logicServiceR.Q1();
    }

    @Benchmark
    public  void Q2(){
        List<Document> q2 = logicServiceR.Q2();
    }

    @Benchmark
    public  void Q3(){
        List<Document> q3 = logicServiceR.Q3();
    }

    @Benchmark
    public  void Q4(){
        List<Document> q4 = logicServiceR.Q4();
    }

    @Benchmark
    public  void Q5(){
        List<Document> q5 = logicServiceR.Q5();
    }




    @Setup(Level.Trial)
    public void setup() {
        //SpringDataMongoAppMainR.main(new String[0]);

        context = new AnnotationConfigApplicationContext(SpringDataMongoAppMainR.class);
        logicServiceR = context.getBean(LogicServiceR.class);
    }

}
