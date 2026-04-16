package cz.cuni.mff.couchbase_java.springdata_e.benchmarks;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class SpringDataBenchmarkRunnerE {
    public static void main(String[] args) throws Exception {

        Options opt = new OptionsBuilder()
                .include(SpringDataBenchmarksE.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
