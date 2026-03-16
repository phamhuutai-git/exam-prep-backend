package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.exams.ExamRequestParam;
import com.example.examprepbackend.dto.response.exams.ExamResponse;
import com.example.examprepbackend.dto.request.teacher.Exam.ExamRequestParam;
import com.example.examprepbackend.dto.response.teacher.ExamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ExamService {
    Page<ExamResponse> getAllExams(ExamRequestParam examRequestParam, Pageable pageable);

    Page<ExamResponse> getExamsByTeacherName(Authentication authentication, Pageable pageable);
}
