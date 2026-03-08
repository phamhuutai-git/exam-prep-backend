package com.example.examprepbackend.service;

import com.example.examprepbackend.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExamService {
    Page<Exam> getAllExams(Pageable pageable);
}
