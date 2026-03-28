package com.example.examprepbackend.controller.ExamAttemptController;

import com.example.examprepbackend.dto.request.exams.SubmitExamAttemptRequest;
import com.example.examprepbackend.dto.response.exams.AttemptResultResponse;
import com.example.examprepbackend.dto.response.exams.AttemptReviewDetailResponse;
import com.example.examprepbackend.dto.response.exams.StartExamAttemptResponse;
import com.example.examprepbackend.dto.response.exams.SubmitExamAttemptResponse;
import com.example.examprepbackend.dto.response.questions.AttemptQuestionsFullResponse;
import com.example.examprepbackend.service.ExamAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class ExamAttemptController {

    private final ExamAttemptService examAttemptService;

    @PostMapping("/{examId}/attempts")
    public ResponseEntity<StartExamAttemptResponse> startAttempt(@PathVariable Integer examId) {
        StartExamAttemptResponse response = examAttemptService.startAttempt(examId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/attempts/{attemptId}/questions")
    public ResponseEntity<AttemptQuestionsFullResponse> getAttemptQuestions(@PathVariable Integer attemptId) {
        AttemptQuestionsFullResponse response = examAttemptService.getAttemptQuestions(attemptId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/attempts/{attemptId}/submit")
    public ResponseEntity<SubmitExamAttemptResponse> submitAttempt(
            @PathVariable Integer attemptId,
            @RequestBody SubmitExamAttemptRequest request
    ) {
        SubmitExamAttemptResponse response = examAttemptService.submitAttempt(attemptId, request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<AttemptResultResponse> getAttemptResult(@PathVariable Integer attemptId) {
        AttemptResultResponse response = examAttemptService.getAttemptResult(attemptId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/attempts/{attemptId}/review-detail")
    public ResponseEntity<AttemptReviewDetailResponse> getAttemptReviewDetail(@PathVariable Integer attemptId) {
        AttemptReviewDetailResponse response = examAttemptService.getAttemptReviewDetail(attemptId);
        return ResponseEntity.ok(response);
    }

}
