package com.example.examprepbackend.controller.users;
import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.users.CreateUserRequest;
import com.example.examprepbackend.dto.request.users.ChangePasswordRequest;
import com.example.examprepbackend.dto.request.users.UserProfileUpdateRequest;
import com.example.examprepbackend.dto.response.users.UserResponse;
import com.example.examprepbackend.dto.response.users.UserInfoResponse;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<BaseResponse<UserSummaryResponse>> updateProfile(Authentication authentication, @Valid @RequestBody UserProfileUpdateRequest profileUpdateRequest) {
        return ResponseEntity.ok().body(new BaseResponse<>(usersService.updateProfile(authentication, profileUpdateRequest), "Profile updated successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserInfoResponse>> getUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse<>(null, "Please login to access this resource"));
        }
        UserInfoResponse userSummary = usersService.getCurrentUser(authentication);
        return ResponseEntity.ok().body(new BaseResponse<>(userSummary, "Get current user successfully"));
    }


    @PostMapping
    public ResponseEntity<BaseResponse<UserSummaryResponse>> createUser(@RequestBody @Valid CreateUserRequest request) {
        UserSummaryResponse dto = usersService.createUser(request);
        return ResponseEntity.ok(new BaseResponse<>(dto, "User created successfully"));
    }
}
