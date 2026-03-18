package com.example.examprepbackend.dto.response.users;

import com.example.examprepbackend.constant.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private Boolean isActive;
}
