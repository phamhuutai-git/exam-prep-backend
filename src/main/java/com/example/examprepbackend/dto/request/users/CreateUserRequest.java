package com.example.examprepbackend.dto.request.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // @Data bao gồm cả @Getter, @Setter, @ToString, @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @Email
    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotBlank(message = "UserName must not be blank")
    private String username;

    // THÊM TRƯỜNG NÀY ĐỂ HẾT ĐỎ setPassword
    @NotBlank(message = "Password must not be blank")
    private String password;

    @NotBlank(message = "ROLE must not be blank")
    private String role;

    // NẾU BẠN DÙNG firstName/lastName TRONG TEST THÌ PHẢI CÓ Ở ĐÂY
    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    // Trường fullName bạn đang có (tùy bạn quyết định dùng fullName hay tách first/last)
    private String fullName;
}