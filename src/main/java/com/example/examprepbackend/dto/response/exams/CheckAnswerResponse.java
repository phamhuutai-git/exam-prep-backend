package com.example.examprepbackend.dto.response.exams;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckAnswerResponse {
    private boolean isCorrect;
    private Integer correctAnswerId;
    private String explanation;
}
