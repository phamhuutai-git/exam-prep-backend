package com.example.examprepbackend.controller.Teacher;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.response.teacher.QuestionResponse;
import com.example.examprepbackend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/teacher/question")
public class TeacherQuestionController {
    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<QuestionResponse>>> getAllQuestions(@PageableDefault(size = 10, sort = "difficultyLevel") Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(questionService.getAllQuestions(pageable), "Get all question"));
    }
}
