package com.example.examprepbackend.controller.Admin;

import com.example.examprepbackend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/admin/question")
public class AdminQuestionController {
    private final QuestionService questionService;
}
