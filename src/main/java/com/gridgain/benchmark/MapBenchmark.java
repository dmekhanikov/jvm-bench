package com.gridgain.benchmark;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
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
import org.openjdk.jmh.annotations.Threads;

import static com.gridgain.benchmark.MapBenchmark.THREADS;

@Threads(THREADS)
@Fork(1)
public class MapBenchmark {
    static final int THREADS = 8;
    private static final int BOUND = 1000;

    @State(Scope.Benchmark)
    public static class MapBenchmarkState {
        Map<Integer, Integer> map;

        @Setup(Level.Iteration)
        public void setup() {
            map = new ConcurrentHashMap<>();
            // map = Collections.synchronizedMap(new HashMap<>());
            System.gc();
        }
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    public int testRandomNumbersConcurrentHashMapCounter(MapBenchmarkState state) {
        int x = ThreadLocalRandom.current().nextInt(BOUND);

        state.map.compute(x, (k, v) ->
            v == null
                ? 1
                : v + 1
        );

        return state.map.get(x);
    }
}
