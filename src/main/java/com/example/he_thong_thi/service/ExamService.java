package com.example.he_thong_thi.service;

import dto.ScoreDTO;

import java.util.List;

public interface ExamService {

    List<ScoreDTO> getScoreByClass(int classId);

}