package com.example.examprepbackend.service;

import java.util.List;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.dto.request.CreateUserRequest;
import com.example.examprepbackend.dto.response.UserSummaryResponse;


public interface UsersService {

    List<Users> getAllUsers();

    UserSummaryResponse createUser(CreateUserRequest request);


}
