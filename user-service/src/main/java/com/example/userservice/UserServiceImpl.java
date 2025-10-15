package com.example.userservice;

import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final Map<String, UserResponse> users = new HashMap<>();

    @Override
    public void getUser(GetUserRequest request,
                        StreamObserver<UserResponse> responseObserver) {
        String userId = request.getUserId();

        if (users.containsKey(userId)) {
            responseObserver.onNext(users.get(userId));
        } else {
            responseObserver.onNext(UserResponse.newBuilder()
                    .setUserId(userId)
                    .setStatus("NOT_FOUND")
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void createUser(CreateUserRequest request,
                           StreamObserver<UserResponse> responseObserver) {
        String userId = UUID.randomUUID().toString();

        UserResponse user = UserResponse.newBuilder()
                .setUserId(userId)
                .setName(request.getName())
                .setEmail(request.getEmail())
                .setStatus("CREATED")
                .build();

        users.put(userId, user);
        responseObserver.onNext(user);
        responseObserver.onCompleted();
    }
}