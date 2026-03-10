package com.example.examprepbackend.controller;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.CreateUserRequest;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @GetMapping
    public BaseResponse<?> getAllUsers() {
        return BaseResponse.success(usersService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Users>> createUser(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.ok(new BaseResponse<>(usersService.createUser(request), "User created successfully"));
    }

}