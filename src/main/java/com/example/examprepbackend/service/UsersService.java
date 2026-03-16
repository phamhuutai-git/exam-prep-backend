package com.example.examprepbackend.service;

import java.util.List;

import com.example.examprepbackend.dto.request.users.ChangePasswordRequest;
import com.example.examprepbackend.dto.request.users.UserProfileUpdateRequest;
import com.example.examprepbackend.dto.response.users.UserInfoResponse;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.dto.request.CreateUserRequest;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import org.springframework.security.core.Authentication;


public interface UsersService {

    List<Users> getAllUsers();

    UserSummaryResponse createUser(CreateUserRequest request);


    Boolean changePassword(Authentication authentication, ChangePasswordRequest changePasswordRequest);
    UserInfoResponse getCurrentUser(Authentication authentication);

    UserSummaryResponse updateProfile(Authentication authentication, UserProfileUpdateRequest profileUpdateRequest);

}