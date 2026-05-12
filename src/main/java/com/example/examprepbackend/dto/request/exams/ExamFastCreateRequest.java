package com.example.examprepbackend.dto.request.exams;

import lombok.Data;

@Data
public class ExamFastCreateRequest {
    private String title;
    private String categoryName;
    private String examType; // PRACTICE, OFFICIAL, MOCK
    private String duration; // Định dạng "HH:mm:ss" hoặc "HH:mm"
    private Double passScore;
    private Boolean reviewAllowed;
    private String rawText;// Toàn bộ nội dung giáo viên đã gõ
}