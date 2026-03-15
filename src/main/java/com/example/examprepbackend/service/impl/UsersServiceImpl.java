package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.request.users.ChangePasswordRequest;
import com.example.examprepbackend.dto.request.users.UserProfileUpdateRequest;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Transactional
    @Override
    public Boolean changePassword(Authentication authentication, ChangePasswordRequest changePasswordRequest) {

        //Check authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        //Check users
        String username = authentication.getName();
        Optional<Users> usersOptional = usersRepository.findByUsername(username);

        if (usersOptional.isEmpty()) {
            throw new ApplicationException("User not found");
        }

        //Check and change password
        String password = changePasswordRequest.getPassword();
        String newPassword = changePasswordRequest.getNewPassword();

        Users users = usersOptional.get();
        if (!passwordEncoder.matches(password, users.getPassword())) {
            throw new ApplicationException("Current password is incorrect");
        }

        users.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(users);

        return true;
    }

    @Transactional
    @Override
    public UserSummaryResponse updateProfile(Authentication authentication, UserProfileUpdateRequest profileUpdateRequest) {

        //Check authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        //Check user
        String username = authentication.getName();
        Optional<Users> userOptional = usersRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new ApplicationException("User not found");
        }

        //Check mail and update
        Users user = userOptional.get();

        String currentEmail = user.getEmail();
        String newEmail = profileUpdateRequest.getEmail();

        if (!newEmail.equals(currentEmail)) {
            Users existsByEmail = usersRepository.findUsersByEmail(newEmail);
            if (existsByEmail != null) {
                throw new ApplicationException("This email is already in use");
            }
        }

        modelMapper.map(profileUpdateRequest, user);
        usersRepository.save(user);

        return modelMapper.map(user, UserSummaryResponse.class);
    }
}