package com.example.he_thong_thi.service.impl;

import com.example.he_thong_thi.repository.ExamRepository;
import com.example.he_thong_thi.service.ExamService;
import dto.ScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Override
    public List<ScoreDTO> getScoreByClass(int classId) {

        List<ScoreDTO> list = examRepository.getScoreByClass(classId);

        // có thể xử lý thêm nếu muốn (optional)
        return list;
    }
}