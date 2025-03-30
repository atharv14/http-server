package org.learn.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private final int port;
    private ServerSocket serverSocket;
    private boolean running = false;
    private final ExecutorService threadPool;

    public HttpServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;

        System.out.println("HTTP Server started on port " + port + "\thttp://localhost:8080");

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                threadPool.execute(new ConnectionHandler(clientSocket));
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
    }

    public void stop() {
        running = false;
        threadPool.shutdown();

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }
}
