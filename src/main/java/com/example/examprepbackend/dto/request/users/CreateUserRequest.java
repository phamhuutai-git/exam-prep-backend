package com.example.examprepbackend.dto.request.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @Email
    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotBlank(message = "UserName must not be blank")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6)
    private String password;

    @NotBlank
    @NotNull(message = "FirstName must not be blank")
    private String firstName;

    @NotBlank
    @NotNull(message = "LastName must not be blank")
    private String lastName;

}
