package com.example.examprepbackend.controller.ExamAttemptController;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.exams.SubmitExamAttemptRequest;
import com.example.examprepbackend.dto.response.exams.*;
import com.example.examprepbackend.service.ExamAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exams-attempt")
@RequiredArgsConstructor
public class ExamAttemptController {

    private final ExamAttemptService examAttemptService;

//    @PostMapping("/{examId}/attempts")
//    public ResponseEntity<StartExamAttemptResponse> startAttempt(@PathVariable Integer examId) {
//        StartExamAttemptResponse response = examAttemptService.startAttempt(examId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//    @GetMapping("/attempts/{attemptId}/questions")
//    public ResponseEntity<AttemptQuestionsFullResponse> getAttemptQuestions(@PathVariable Integer attemptId) {
//        AttemptQuestionsFullResponse response = examAttemptService.getAttemptQuestions(attemptId);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/exam-id/{id}/start")
    // Bắt đầu một lần làm bài mới cho user trên một đề thi
    public ResponseEntity<BaseResponse<ExamStartResponse>> startExam(@PathVariable Integer id,
                                                                     Authentication authentication) {
        return ResponseEntity.ok().body(new BaseResponse<>(examAttemptService.startExam(id, authentication), "Start exam"));
    }

    // Nộp bài, chấm điểm, khóa bài, lưu kết quả chính thức.
    @PostMapping("/attempts/{attemptId}/submit")
    public ResponseEntity<SubmitExamAttemptResponse> submitAttempt(
            @PathVariable Integer attemptId,
            @RequestBody SubmitExamAttemptRequest request
    ) {
        SubmitExamAttemptResponse response = examAttemptService.submitAttempt(attemptId, request);
        return ResponseEntity.ok(response);
    }


    // Xem kết quả tổng quan sau khi nộp thi that
    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<AttemptResultResponse> getAttemptResult(@PathVariable Integer attemptId) {
        AttemptResultResponse response = examAttemptService.getAttemptResult(attemptId);
        return ResponseEntity.ok(response);
    }

    // Xem chi tiết từng câu, nhưng chỉ khi thỏa business rule, đặc biệt là mock/practice.
    @GetMapping("/attempts/{attemptId}/review-detail")
    public ResponseEntity<AttemptReviewDetailResponse> getAttemptReviewDetail(@PathVariable Integer attemptId) {
        AttemptReviewDetailResponse response = examAttemptService.getAttemptReviewDetail(attemptId);
        return ResponseEntity.ok(response);
    }
}


