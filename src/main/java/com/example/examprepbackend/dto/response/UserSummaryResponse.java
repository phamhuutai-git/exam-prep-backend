package com.example.examprepbackend.dto.response;

import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.constant.Status;
import com.example.examprepbackend.entity.Classes;
import com.example.examprepbackend.entity.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserSummaryResponse {
    private String email;

    private String username;

    private String firstName;

    private String lastName;

}
