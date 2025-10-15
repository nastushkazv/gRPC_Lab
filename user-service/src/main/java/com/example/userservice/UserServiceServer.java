package com.example.userservice;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class UserServiceServer {
    private Server server;
    private final int port = 50051;

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new UserServiceImpl())
                .build()
                .start();

        System.out.println("User Service started on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down User Service");
            UserServiceServer.this.stop();
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args)
            throws IOException, InterruptedException {
        UserServiceServer server = new UserServiceServer();
        server.start();
        server.blockUntilShutdown();
    }
}