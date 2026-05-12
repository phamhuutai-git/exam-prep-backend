package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.dto.response.admin.AdminStatsResponse;
import com.example.examprepbackend.repository.ClassRepository;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final ClassRepository classRepository;
    private final UsersRepository usersRepository;

    @Override
    public AdminStatsResponse getAdminStats() {
        // Đếm tổng số lớp học
        long totalClasses = classRepository.count();

        // Đếm tổng số học sinh và giáo viên
        long totalStudents = usersRepository.countByRole(Role.STUDENT);
        long totalTeachers = usersRepository.countByRole(Role.TEACHER);

        return new AdminStatsResponse(totalClasses, totalStudents, totalTeachers);
    }
}
