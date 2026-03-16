package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.request.CreateUserRequest;
import com.example.examprepbackend.dto.response.users.UserInfoResponse;
import com.example.examprepbackend.dto.request.users.ChangePasswordRequest;
import com.example.examprepbackend.dto.request.users.UserProfileUpdateRequest;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.exception.DuplicateResourceException;
import com.example.examprepbackend.mapper.UserMapper;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.UsersService;
import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.constant.Status;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    @Transactional
    public UserSummaryResponse createUser(CreateUserRequest request) {
        String email = normalizeEmail(request.getEmail());
        String username = normalizeUsername(request.getUsername());

        checkDuplicate(email, username);

        Users user = userMapper.toEntity(request);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        user.setIsActive(true);
        user.setStatus(Status.ACTIVED);
        user.setCreatedDate(LocalDateTime.now());
        user.setFailCount(0);

        try {
            Users savedUser = usersRepository.save(user);
            return userMapper.toDto(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Email or username already exists");
        }
    }

    private void checkDuplicate(String email, String username) {
        boolean emailExists = usersRepository.existsByEmail(email);
        boolean usernameExists = usersRepository.existsByUsernameIgnoreCase(username);

        if (emailExists && usernameExists) {
            throw new DuplicateResourceException("Email and username already exist");
        }
        if (emailExists) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (usernameExists) {
            throw new DuplicateResourceException("Username already exists");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeUsername(String username) {
        return username.trim();
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
    @Override
    public UserInfoResponse getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }
        String username = authentication.getName();
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );
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
        Users existsByEmail  = usersRepository.findUsersByEmail(profileUpdateRequest.getEmail());
        if (existsByEmail  != null) {
            throw new ApplicationException("This email is already in use");
        }

        Users user = userOptional.get();
        modelMapper.map(profileUpdateRequest, user);
        usersRepository.save(user);

        return modelMapper.map(user, UserSummaryResponse.class);
    }
}