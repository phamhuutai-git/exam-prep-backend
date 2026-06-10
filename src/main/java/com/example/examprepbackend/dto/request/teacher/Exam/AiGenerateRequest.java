package com.example.examprepbackend.dto.request.teacher.Exam;

import lombok.Data;

@Data
public class AiGenerateRequest {
    private String promptText;
    private int quantity;
    private String difficulty;
}