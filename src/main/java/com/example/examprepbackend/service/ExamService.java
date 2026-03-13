package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.ExamRequestParam;
import com.example.examprepbackend.dto.response.ExamResponse;
import com.example.examprepbackend.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExamService {
    Page<ExamResponse> getAllExams(ExamRequestParam examRequestParam, Pageable pageable);

    Page<ExamResponse> getExamsByClassId(Integer classId, ExamRequestParam examRequestParam, Pageable pageable);
}
