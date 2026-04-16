package cz.cuni.mff.mongodb_java.morphia.benchmarks;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class MorphiaBenchmarkRunnerE {
    public static void main(String[] args) throws Exception {

        Options opt = new OptionsBuilder()
                .include(MorphiaBenchmarksE.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
