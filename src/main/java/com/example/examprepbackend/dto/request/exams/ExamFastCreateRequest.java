package com.example.examprepbackend.dto.request.exams;

import lombok.Data;

@Data
public class ExamFastCreateRequest {
    private String title;
    private String categoryName;
    private String examType;
    private String duration;
    private Double passScore;
    private Boolean reviewAllowed;
    private String rawText;
}