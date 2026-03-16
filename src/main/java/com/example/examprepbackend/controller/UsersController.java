package com.example.examprepbackend.controller;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.CreateUserRequest;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
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
    public ResponseEntity<BaseResponse<UserSummaryResponse>> createUser(@RequestBody @Valid CreateUserRequest request) {
        UserSummaryResponse dto = usersService.createUser(request);
        return ResponseEntity.ok(new BaseResponse<>(dto, "User created successfully"));
    }

}