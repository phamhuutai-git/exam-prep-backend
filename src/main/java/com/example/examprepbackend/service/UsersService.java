package com.example.examprepbackend.service;

import java.util.List;

import com.example.examprepbackend.dto.request.users.ChangePasswordRequest;
import com.example.examprepbackend.dto.request.users.UserProfileUpdateRequest;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.entity.Users;
import org.springframework.security.core.Authentication;


public interface UsersService {


    Boolean changePassword(Authentication authentication, ChangePasswordRequest changePasswordRequest);

    UserSummaryResponse updateProfile(Authentication authentication, UserProfileUpdateRequest profileUpdateRequest);

}