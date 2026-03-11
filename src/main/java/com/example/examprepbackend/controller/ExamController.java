package com.example.examprepbackend.controller;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.ExamRequestParam;
import com.example.examprepbackend.dto.response.ExamResponse;
import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<ExamResponse>>> getAllExams(ExamRequestParam examRequestParam, @PageableDefault(size = 10, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(examService.getAllExams(examRequestParam, pageable), "get all"));
    }
    @GetMapping("/by-category")
    public ResponseEntity<BaseResponse<Page<ExamResponse>>> getExamByCategory(@RequestParam Integer categoryId, @PageableDefault(size = 10, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(examService.getExamByCategory(categoryId, pageable), "get exam by category"));
    }
}
