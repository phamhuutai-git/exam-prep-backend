package com.example.examprepbackend.dto.request.exams;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {
    private Integer questionId;
    private List<Integer> selectedAnswerIds;
}
