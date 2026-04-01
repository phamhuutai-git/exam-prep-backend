package com.example.examprepbackend.controller.auth;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.exams.CheckAnswerRequest;
import com.example.examprepbackend.dto.response.exams.CheckAnswerResponse;
import com.example.examprepbackend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/exam-attempts")
@RequiredArgsConstructor
public class ExamAttemptControllerNG {

    private final QuestionService questionService;

    @PostMapping("/check-answer")
    public ResponseEntity<BaseResponse<CheckAnswerResponse>> checkAnswer(@RequestBody CheckAnswerRequest request) {
        CheckAnswerResponse responseData = questionService.checkAnswer(request);
        return ResponseEntity.ok(BaseResponse.success(responseData));
    }
}
