package com.example.orderservice;

import com.example.userservice.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final Map<String, OrderResponse> orders = new HashMap<>();
    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public OrderServiceImpl(UserServiceGrpc.UserServiceBlockingStub userServiceStub) {
        this.userServiceStub = userServiceStub;
    }

    @Override
    public void createOrder(CreateOrderRequest request,
                            StreamObserver<OrderResponse> responseObserver) {
        // Проверяем существование пользователя
        var userRequest = com.example.userservice.GetUserRequest.newBuilder()
                .setUserId(request.getUserId())
                .build();

        var userResponse = userServiceStub.getUser(userRequest);

        if ("NOT_FOUND".equals(userResponse.getStatus())) {
            responseObserver.onNext(OrderResponse.newBuilder()
                    .setStatus("USER_NOT_FOUND")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        // Создаем заказ
        String orderId = UUID.randomUUID().toString();
        double totalAmount = request.getItemsList().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        OrderResponse order = OrderResponse.newBuilder()
                .setOrderId(orderId)
                .setUserId(request.getUserId())
                .addAllItems(request.getItemsList())
                .setTotalAmount(totalAmount)
                .setStatus("CREATED")
                .build();

        orders.put(orderId, order);
        responseObserver.onNext(order);
        responseObserver.onCompleted();
    }

    @Override
    public void getOrder(GetOrderRequest request,
                         StreamObserver<OrderResponse> responseObserver) {
        String orderId = request.getOrderId();

        if (orders.containsKey(orderId)) {
            responseObserver.onNext(orders.get(orderId));
        } else {
            responseObserver.onNext(OrderResponse.newBuilder()
                    .setOrderId(orderId)
                    .setStatus("NOT_FOUND")
                    .build());
        }
        responseObserver.onCompleted();
    }
}