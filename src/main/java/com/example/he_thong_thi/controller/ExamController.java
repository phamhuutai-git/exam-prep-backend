package com.example.he_thong_thi.controller;

import com.example.he_thong_thi.service.ExamService;

import dto.ScoreDTO;
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
    public List<ScoreDTO> getScoreByClass(@PathVariable int classId){
        return examService.getScoreByClass(classId);
    }
}