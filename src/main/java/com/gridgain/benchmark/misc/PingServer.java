package com.gridgain.benchmark.misc;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class PingServer implements Closeable {
    private String host;
    private int port;
    private int packetSize;
    private Thread serverThread;
    private Socket socket;

    public PingServer(String host, int port, int packetSize) {
        this.host = host;
        this.port = port;
        this.packetSize = packetSize;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(host, port));

        serverThread = new Thread(() -> {
            try {
                socket = serverSocket.accept();
                Reader socketReader = new InputStreamReader(socket.getInputStream());
                Writer socketWriter = new OutputStreamWriter(socket.getOutputStream());

                char[] buf = new char[packetSize];

                while (!Thread.currentThread().isInterrupted()) {
                    if (!read(socketReader, buf))
                        return;

                    socketWriter.write(buf);
                    socketWriter.flush();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverThread.start();
    }

    @Override public void close() throws IOException {
        socket.close();
        serverThread.interrupt();
        try {
            serverThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
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
