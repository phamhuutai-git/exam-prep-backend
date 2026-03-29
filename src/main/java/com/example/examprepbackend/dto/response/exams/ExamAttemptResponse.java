package com.example.examprepbackend.dto.response.exams;

import com.example.examprepbackend.constant.AttemptStatus;
import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExamAttemptResponse {
    private Integer id;

    private Exam exam;

    private Users student;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double score;

    @Enumerated(EnumType.STRING)

    private AttemptStatus status;
}
