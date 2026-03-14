package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.request.CreateUserRequest;
import com.example.examprepbackend.dto.response.UserSummaryResponse;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.mapper.UserMapper;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.UsersService;
import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.constant.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    @Transactional
    public UserSummaryResponse createUser(CreateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateUserRequest must not be null");
        }


        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }


        String email = request.getEmail().trim().toLowerCase();
        String username = request.getUsername().trim().toLowerCase();


        Optional<Users> existedUser = usersRepository.findByEmailOrUsername(email, username);
        if (existedUser.isPresent()) {
            Users u = existedUser.get();
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) {
                throw new RuntimeException("Email already exists");
            }
            if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(username)) {
                throw new RuntimeException("Username already exists");
            }
            throw new RuntimeException("Email or username already exists");
        }


        Users user = userMapper.toEntity(request);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.STUDENT);
        user.setIsActive(Boolean.TRUE);
        user.setStatus(Status.ACTIVED);
        user.setCreatedDate(LocalDateTime.now());
        user.setFailCount(0);

        Users saved = usersRepository.save(user);
        // map to DTO before returning
        return userMapper.toDto(saved);
    }

}
