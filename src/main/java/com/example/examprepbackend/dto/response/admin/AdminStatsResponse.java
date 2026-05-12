package com.example.examprepbackend.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsResponse {
    private long totalClasses;
    private long totalStudents;
    private long totalTeachers;
}
