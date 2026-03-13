package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.request.CreateUserRequest;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.UsersService;
import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.constant.Status;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final ModelMapper modelMapper;

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
        if (request.getUsername() != null) {
            existedUser = usersRepository.findByUsername(request.getUsername());
            if (existedUser != null) {
                throw new RuntimeException("Username already exists");
            }
        }




        Users user = modelMapper.map(request,Users.class);
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
