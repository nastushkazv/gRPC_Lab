package com.example.orderservice;

import com.example.userservice.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class OrderServiceServer {
    private Server server;
    private final int port = 50052;
    private ManagedChannel userServiceChannel;

    public void start() throws IOException {
        // Создаем канал к User Service
        userServiceChannel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        UserServiceGrpc.UserServiceBlockingStub userServiceStub =
                UserServiceGrpc.newBlockingStub(userServiceChannel);

        server = ServerBuilder.forPort(port)
                .addService(new OrderServiceImpl(userServiceStub))
                .build()
                .start();

        System.out.println("Order Service started on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down Order Service");
            OrderServiceServer.this.stop();
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
        if (userServiceChannel != null) {
            userServiceChannel.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args)
            throws IOException, InterruptedException {
        OrderServiceServer server = new OrderServiceServer();
        server.start();
        server.blockUntilShutdown();
    }
}