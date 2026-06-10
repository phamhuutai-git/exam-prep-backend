package com.example.examprepbackend.controller.Teacher;

import com.example.examprepbackend.dto.request.teacher.Exam.AiGenerateRequest;
import com.example.examprepbackend.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AiController {

    private final AiService aiService;

    @PostMapping("/generate-questions")
    public ResponseEntity<String> generateQuestionsByAI(@RequestBody AiGenerateRequest request) {
        String jsonResult = aiService.generateQuestions(request);
        return ResponseEntity.ok(jsonResult);
    }
}