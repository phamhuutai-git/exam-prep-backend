package com.example.examprepbackend.dto.response.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeacherStatsResponse {
    private long totalExams;
    private long totalQuestions;
    private long totalStudents;
}