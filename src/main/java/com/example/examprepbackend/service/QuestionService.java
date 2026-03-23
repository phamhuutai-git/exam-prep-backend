package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.teacher.Question.CreateQuestionRequest;
import com.example.examprepbackend.dto.request.teacher.Question.QuestionRequestParam;
import com.example.examprepbackend.dto.response.teacher.QuestionCountResponse;
import com.example.examprepbackend.dto.response.teacher.QuestionResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionService {

    Page<QuestionResponse> getAllQuestions(QuestionRequestParam Param, Pageable pageable);

    Page<QuestionResponse> getMyQuestions(QuestionRequestParam param, Pageable pageable);

    QuestionResponse getQuestionById(Integer id);

    List<QuestionResponse> getQuestionsByExamId(Integer examId);

    QuestionResponse createQuestion(CreateQuestionRequest request);

    QuestionResponse updateQuestion(Integer id, CreateQuestionRequest request);

    void deleteQuestion(Integer id);

    void exportQuestionToExcel(HttpServletResponse response) throws IOException;

    void importQuestionFromExcel(MultipartFile file) throws IOException;

    QuestionCountResponse getAllQuestionsCount();
}
