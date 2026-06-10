package com.example.examprepbackend.dto.response.users;

import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.constant.Status;
import com.example.examprepbackend.entity.Classes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudentResponse {
    private Integer id;

    private String username;

    private String firstName;

    private String lastName;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Classes classes;

}
