package cz.cuni.mff.mongodb_java.springdata_e.benchmarks;


import cz.cuni.mff.mongodb_java.springdata_e.SpringDataMongoAppMainE;
import cz.cuni.mff.mongodb_java.springdata_e.service.LogicServiceE;
import org.bson.Document;
import org.openjdk.jmh.annotations.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class SpringDataBenchmarksE {
    private LogicServiceE logicServiceE;
    private AnnotationConfigApplicationContext context;

    /**
     * Benchmark                 Mode  Cnt     Score     Error  Units
     * SpringDataBenchmarksE.C2  avgt    5  1414,027 ± 141,445  ms/op
     */
    @Benchmark
    public void C2(){
        List<Document> c2 = logicServiceE.C2();
    }

    @Setup(Level.Trial)
    public void setup() {
        //SpringDataMongoAppMainR.main(new String[0]);

        context = new AnnotationConfigApplicationContext(SpringDataMongoAppMainE.class);
        logicServiceE = context.getBean(LogicServiceE.class);
    }

}
