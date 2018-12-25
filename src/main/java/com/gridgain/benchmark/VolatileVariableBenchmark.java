package com.gridgain.benchmark;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;

import static com.gridgain.benchmark.VolatileVariableBenchmark.THREADS;

@Threads(THREADS)
@Fork(1)
public class VolatileVariableBenchmark {
    static final int THREADS = 8;

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        volatile int value;
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    public int benchmarkReadWriteBenchmark(BenchmarkState state) {
        state.value = ThreadLocalRandom.current().nextInt();

        return state.value;
    }
}
