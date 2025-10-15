package com.example.userservice;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TestClient {
    public static void main(String[] args) {
        System.out.println("Запуск тестового клиента...");

        try {
            // 1. Тестируем User Service
            System.out.println("1. Подключаемся к User Service...");
            ManagedChannel userChannel = ManagedChannelBuilder.forAddress("localhost", 50051)
                    .usePlaintext()
                    .build();

            UserServiceGrpc.UserServiceBlockingStub userStub =
                    UserServiceGrpc.newBlockingStub(userChannel);

            // Создаем пользователя
            System.out.println("2. Создаем пользователя...");
            var createUserResponse = userStub.createUser(
                    CreateUserRequest.newBuilder()
                            .setName("Анна Петрова")
                            .setEmail("anna@example.com")
                            .build()
            );

            System.out.println("Пользователь создан:");
            System.out.println("ID: " + createUserResponse.getUserId());
            System.out.println("Имя: " + createUserResponse.getName());
            System.out.println("Email: " + createUserResponse.getEmail());
            System.out.println("Статус: " + createUserResponse.getStatus());

            // 2. Тестируем Order Service
            System.out.println("3. Подключаемся к Order Service...");
            ManagedChannel orderChannel = ManagedChannelBuilder.forAddress("localhost", 50052)
                    .usePlaintext()
                    .build();

            com.example.orderservice.OrderServiceGrpc.OrderServiceBlockingStub orderStub =
                    com.example.orderservice.OrderServiceGrpc.newBlockingStub(orderChannel);

            // Создаем заказ для этого пользователя
            System.out.println("4. Создаем заказ для пользователя...");
            var createOrderResponse = orderStub.createOrder(
                    com.example.orderservice.CreateOrderRequest.newBuilder()
                            .setUserId(createUserResponse.getUserId())
                            .addItems(com.example.orderservice.OrderItem.newBuilder()
                                    .setProductId("NOTEBOOK")
                                    .setQuantity(1)
                                    .setPrice(45000.0)
                                    .build())
                            .addItems(com.example.orderservice.OrderItem.newBuilder()
                                    .setProductId("MOUSE")
                                    .setQuantity(2)
                                    .setPrice(2500.0)
                                    .build())
                            .build()
            );

            System.out.println("Заказ создан:");
            System.out.println("ID заказа: " + createOrderResponse.getOrderId());
            System.out.println("ID пользователя: " + createOrderResponse.getUserId());
            System.out.println("Общая сумма: " + createOrderResponse.getTotalAmount() + " руб.");
            System.out.println("Статус: " + createOrderResponse.getStatus());
            System.out.println("Товаров в заказе: " + createOrderResponse.getItemsCount());

            userChannel.shutdown();
            orderChannel.shutdown();

            System.out.println("Тестирование завершено успешно!");

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}