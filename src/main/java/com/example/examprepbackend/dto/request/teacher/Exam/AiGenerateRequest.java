package com.example.examprepbackend.dto.request.teacher.Exam;

import lombok.Data;

@Data
public class AiGenerateRequest {
    private String promptText; // Chủ đề giáo viên nhập
    private int quantity;      // Số lượng câu hỏi
    private String difficulty; // Độ khó (EASY, MEDIUM, HARD)
}