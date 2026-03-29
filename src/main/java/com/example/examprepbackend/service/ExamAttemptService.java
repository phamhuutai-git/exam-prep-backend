package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.response.exams.ExamStartResponse;
import org.springframework.security.core.Authentication;

public interface ExamAttemptService {

    ExamStartResponse startExam(Integer examId, Authentication authentication);

}
