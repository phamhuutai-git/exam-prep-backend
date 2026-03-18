package com.example.he_thong_thi.controller;

import com.example.he_thong_thi.service.ExamService;

import dto.Request.ExamUpdateRequest;
import dto.Response.ExamResponse;
import dto.Response.ScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExamController {

    @Autowired
    private ExamService examService;

    @GetMapping("/classes/{classId}/scores")
    //http://localhost:8080/api/classes/1/scores
    public List<ScoreDTO> getScoreByClass(@PathVariable int classId) {
        return examService.getScoreByClass(classId);
    }

    // ================== 🔥 THÊM MỚI ==================

    // UPDATE EXAM
    @PutMapping("/exams/{examId}")
    public ExamResponse updateExam(
            @PathVariable int examId,
            @RequestBody ExamUpdateRequest request) {

        return examService.updateExam(examId, request);
    }

    // GET DETAIL EXAM
    @GetMapping("/exams/{examId}")
    public ExamResponse getExamDetail(@PathVariable int examId) {
        return examService.getExamDetail(examId);
    }
}