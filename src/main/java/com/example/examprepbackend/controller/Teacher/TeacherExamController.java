package com.example.examprepbackend.controller.Teacher;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.exams.ExamCreateRequest;
import com.example.examprepbackend.dto.request.exams.ExamRequestParam;
import com.example.examprepbackend.dto.request.exams.ExamUpdateRequest;
import com.example.examprepbackend.dto.response.exams.ExamResponse;
import com.example.examprepbackend.dto.response.exams.ExamSummaryResponse;
import com.example.examprepbackend.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/teacher/exams")
@RequiredArgsConstructor
public class TeacherExamController {
    private final ExamService examService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<ExamResponse>>> getAllExams(ExamRequestParam examRequestParam, @PageableDefault(size = 4, sort = "createDate") Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(examService.getAllExams(examRequestParam, pageable), "get all"));
    }

    @GetMapping("/teacher-name")
    public ResponseEntity<BaseResponse<Page<ExamResponse>>> getExamsByTeacherName(Authentication authentication,
                                                                                  @PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(examService.getExamsByTeacherName(authentication, pageable), "Get Exams by Teacher"));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ExamSummaryResponse>> createExam(Authentication authentication, @RequestBody @Valid ExamCreateRequest examCreateRequest) {
        return ResponseEntity.ok().body(new BaseResponse<>(examService.createExam(authentication, examCreateRequest), "Exam created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ExamSummaryResponse>> updateExamById(@PathVariable Integer id, @RequestBody @Valid ExamUpdateRequest examUpdateRequest) {
        return ResponseEntity.ok().body(new BaseResponse<>(examService.updateExamById(id, examUpdateRequest), "Exam updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Boolean>> deleteExamById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(new BaseResponse<>(examService.deleteExamById(id), "Exam deleted"));
    }
}