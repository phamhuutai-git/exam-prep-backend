package com.example.examprepbackend.dto.request.exams;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExamAttemptRequest {
    private List<SubmitAnswerRequest> answers;
}
