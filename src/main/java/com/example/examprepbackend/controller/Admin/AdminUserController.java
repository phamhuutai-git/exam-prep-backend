package com.example.examprepbackend.controller.Admin;

import com.example.examprepbackend.dto.request.admin.UpdateUserRequest;
import com.example.examprepbackend.dto.response.users.UserResponse;
import com.example.examprepbackend.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateUserRequest request
    ) throws BadRequestException {
        return ResponseEntity.ok(adminUserService.updateUser(userId, request));
    }
}
