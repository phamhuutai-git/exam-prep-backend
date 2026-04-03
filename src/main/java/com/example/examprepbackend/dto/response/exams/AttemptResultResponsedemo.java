package com.example.examprepbackend.dto.response.exams;

import lombok.Data;

import java.util.List;

public class AttemptResultResponsedemo {

    @Data
    public class AttemptResultResponse {
        private Integer attemptId;
        private Integer examId;
        private String examTitle;
        private String examType;

        private Integer totalQuestions;
        private Integer correctAnswers;
        private Double score;
        private Boolean passed;
        private String status;

        // true nếu được xem chi tiết (thi thử)
        private Boolean reviewAllowed;

        // null nếu là thi thật
        private List<QuestionReviewResponse> questions;
    }
}
