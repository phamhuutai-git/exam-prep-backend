package com.example.examprepbackend.controller.exam.attempt;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.response.exams.ExamStartResponse;
import com.example.examprepbackend.service.ExamAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/exam-attempt")
@RequiredArgsConstructor
public class ExamAttemptControllerTai {

    private final ExamAttemptService examAttemptService;

    @PostMapping("/exam-id/{id}/start")
    public ResponseEntity<BaseResponse<ExamStartResponse>> startExam(@PathVariable Integer id,
                                                                     Authentication authentication) {
        return ResponseEntity.ok().body(new BaseResponse<>(examAttemptService.startExam(id, authentication), "Start exam"));
    }

}
