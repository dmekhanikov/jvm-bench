package com.gridgain.benchmark;

import com.gridgain.benchmark.misc.PingServer;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
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
import org.openjdk.jmh.annotations.TearDown;

@Fork(1)
public class NetworkLoopbackBenchmark {
    private static final int PORT = 48100;
    private static final String HOST = "127.0.0.1";
    private static final int PACKET_SIZE = 4096;

    @State(Scope.Benchmark)
    public static class NetworkBenchmarkState {
        char[] buf;

        PingServer server;

        private Socket clientSocket;
        Reader clientSocketReader;
        Writer clientSocketWriter;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            byte[] bytes = new byte[PACKET_SIZE];
            ThreadLocalRandom.current().nextBytes(bytes);

            buf = new char[PACKET_SIZE];
            for (int i = 0; i < PACKET_SIZE; i++) {
                buf[i] = (char)bytes[i];
            }

            server = new PingServer(HOST, PORT, PACKET_SIZE);
            server.start();

            clientSocket = new Socket(HOST, PORT);
            clientSocketReader = new InputStreamReader(clientSocket.getInputStream());
            clientSocketWriter = new OutputStreamWriter(clientSocket.getOutputStream());
        }

        @TearDown(Level.Trial)
        public void tearDown() throws IOException {
            clientSocket.close();
            server.close();
        }
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    public void testLoopback(NetworkBenchmarkState state) throws IOException {
        state.clientSocketWriter.write(state.buf);
        state.clientSocketWriter.flush();

        read(state.clientSocketReader, state.buf);
    }

    private boolean read(Reader reader, char[] buf) throws IOException {
        int read = 0;

        while (read < buf.length) {
            int res = reader.read(buf, read, buf.length - read);

            if (res < 0)
                return false;

            read += res;
        }

        return true;
    }
}
