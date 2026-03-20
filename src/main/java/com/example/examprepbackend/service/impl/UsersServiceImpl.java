package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.dto.request.users.ChangePasswordRequest;
import com.example.examprepbackend.dto.request.users.UserProfileUpdateRequest;
import com.example.examprepbackend.dto.response.users.UserResponse;
import com.example.examprepbackend.dto.response.users.UserInfoResponse;
import com.example.examprepbackend.dto.response.users.UserSummaryResponse;
import com.example.examprepbackend.entity.Classes;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.ClassRepository;
import com.example.examprepbackend.repository.ClassTeacherRepository;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ClassRepository classRepository;
    private final ClassTeacherRepository classTeacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private UserResponse convertToDto(Users users) {
        UserResponse userResponse = new UserResponse();

        BeanUtils.copyProperties(users, userResponse);

        return userResponse;

    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return usersRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    public List<UserResponse> getAllStudents() {
        return usersRepository.findByRole(Role.STUDENT).stream().map(this::convertToDto).toList();
    }

    @Override
    public List<UserResponse> getStudentsByClassId(Integer id) {
        Optional<Classes> classesOptional = classRepository.findById(id);
        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }

        return usersRepository.findByRoleAndClasses_Id(Role.STUDENT, id).stream().map(this::convertToDto).toList();
    }

    @Override
    public List<UserResponse> getAllTeachers() {
        return usersRepository.findByRole(Role.TEACHER).stream().map(this::convertToDto).toList();
    }

    @Override
    public List<UserResponse> getTeachersByClassId(Integer classId) {

        Optional<Classes> classesOptional = classRepository.findById(classId);
        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }

        List<Integer> teacherIdList = classTeacherRepository.findByClasses_Id(classId);

        return usersRepository.findByRoleAndIdIn(Role.TEACHER, teacherIdList).stream().map(this::convertToDto).toList();
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