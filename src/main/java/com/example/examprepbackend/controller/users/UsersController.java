package com.example.examprepbackend.controller.users;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.users.ChangePasswordRequest;
import com.example.examprepbackend.dto.request.users.UserProfileUpdateRequest;
import com.example.examprepbackend.dto.response.users.UserResponse;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<UserResponse>>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(usersService.getAllUsers(pageable), "Get all users"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<BaseResponse<Boolean>> changePassword(Authentication authentication,
                                                                @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok().body(new BaseResponse<>(usersService.changePassword(authentication, changePasswordRequest), "Password changed successfully"));
    }

    @PutMapping("/profile")
    public ResponseEntity<BaseResponse<UserSummaryResponse>> updateProfile(Authentication authentication,
                                                                           @Valid @RequestBody UserProfileUpdateRequest profileUpdateRequest) {
        return ResponseEntity.ok().body(new BaseResponse<>(usersService.updateProfile(authentication, profileUpdateRequest), "Profile updated successfully"));
    }


}