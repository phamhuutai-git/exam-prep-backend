package com.example.he_thong_thi.service;

import dto.Request.ExamUpdateRequest;
import dto.Response.ExamResponse;
import dto.Response.ScoreDTO;

import java.util.List;

public interface ExamService {

    // đã có
    List<ScoreDTO> getScoreByClass(int classId);

    // update
    ExamResponse updateExam(int examId, ExamUpdateRequest request);

    ExamResponse getExamDetail(int examId);
}