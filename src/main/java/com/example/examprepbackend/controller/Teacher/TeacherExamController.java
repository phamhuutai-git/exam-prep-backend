package com.example.examprepbackend.controller.Teacher;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.ExamRequestParam;
import com.example.examprepbackend.dto.response.teacher.ExamResponse;
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
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/teacher/exams")
@RequiredArgsConstructor
public class TeacherExamController {
    private final ExamService examService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<ExamResponse>>> getAllExams(ExamRequestParam examRequestParam, @PageableDefault(size = 10, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(examService.getAllExams(examRequestParam, pageable), "get all"));
    }
}
