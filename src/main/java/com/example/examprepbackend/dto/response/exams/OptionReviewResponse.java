package com.example.examprepbackend.dto.response.exams;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionReviewResponse {
    private Integer optionId;
    private String optionContent;
    private Boolean isCorrect;
    private Boolean isSelected;
}
