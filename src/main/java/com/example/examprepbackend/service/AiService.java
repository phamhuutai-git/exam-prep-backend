package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.teacher.Exam.AiGenerateRequest;

public interface AiService {
    String generateQuestions(AiGenerateRequest request);
}
