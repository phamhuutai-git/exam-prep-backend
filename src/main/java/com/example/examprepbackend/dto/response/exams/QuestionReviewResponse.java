package com.example.examprepbackend.dto.response.exams;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter

public class QuestionReviewResponse {
    private Integer questionId;
    private String questionContent;
    private String explanation;
    private Boolean isCorrect;

    private List<Integer> selectedOptionIds;
    private List<Integer> correctOptionIds;

    private List<OptionReviewResponse> options;
}
