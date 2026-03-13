package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.response.QuestionResponse;
import com.example.examprepbackend.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {
    Page<QuestionResponse> getAllQuestions(Pageable pageable);
}
