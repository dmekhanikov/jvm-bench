package com.gridgain.benchmark;

import com.gridgain.benchmark.misc.OffHeapArray;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@Fork(1)
public class OffHeapSequentialBenchmark {
    private static final long SIZE = Integer.MAX_VALUE;

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        OffHeapArray array;
        long key;

        @Setup(Level.Trial)
        public void setup() {
            array = new OffHeapArray(SIZE);
            key = 0;
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            array.freeMemory();
        }
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    public void testOffHeapSequentialAccess(BenchmarkState state) {
        long k = state.key;
        state.key = (state.key + 1) % SIZE;

        int v = state.array.get(k);

        state.array.set(k, (byte)(v + 1));
    }
}
