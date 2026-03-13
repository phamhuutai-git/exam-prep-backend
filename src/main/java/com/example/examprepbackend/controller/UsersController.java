package com.example.examprepbackend.controller;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;



}