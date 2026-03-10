package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.request.CreateUserRequest;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.UsersService;
import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.constant.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public Users createUser(CreateUserRequest request) {
        Users existedUser = usersRepository.findByEmail(request.getEmail());
        if (existedUser != null) {
            throw new RuntimeException("Email already exists");
        }
        if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Password and confirm password do not match");
        }

        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.STUDENT);
        user.setIsActive(Boolean.TRUE);
        user.setStatus(Status.ACTIVED);
        user.setCreatedDate(LocalDateTime.now());

        return usersRepository.save(user);
    }

}
