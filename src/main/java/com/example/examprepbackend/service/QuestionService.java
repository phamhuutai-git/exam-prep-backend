package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.teacher.Question.CreateQuestionRequest;
import com.example.examprepbackend.dto.request.teacher.Question.QuestionRequestParam;
import com.example.examprepbackend.dto.response.teacher.QuestionResponse;
import com.example.examprepbackend.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {

    Page<QuestionResponse> getAllQuestions(QuestionRequestParam Param, Pageable pageable);

    QuestionResponse getQuestionById(Integer id);

    QuestionResponse createQuestion(CreateQuestionRequest request);

    QuestionResponse updateQuestion(Integer id, CreateQuestionRequest request);

    void deleteQuestion(Integer id);
}
