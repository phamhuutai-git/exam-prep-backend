package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.repository.ExamRepository;
import com.example.examprepbackend.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;

    @Override
    public Page<Exam> getAllExams(Pageable pageable) {
        return examRepository.findAll(pageable);
    }
}
