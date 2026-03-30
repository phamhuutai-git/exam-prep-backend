package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.exams.SubmitExamAttemptRequest;
import com.example.examprepbackend.dto.response.exams.AttemptResultResponse;
import com.example.examprepbackend.dto.response.exams.AttemptReviewDetailResponse;
import com.example.examprepbackend.dto.response.exams.StartExamAttemptResponse;
import com.example.examprepbackend.dto.response.exams.SubmitExamAttemptResponse;
import com.example.examprepbackend.dto.response.questions.AttemptQuestionsFullResponse;
import org.springframework.transaction.annotation.Transactional;


public interface ExamAttemptServiceHieu {

    StartExamAttemptResponse startAttempt(Integer examId);

    AttemptQuestionsFullResponse getAttemptQuestions(Integer attemptId);


    SubmitExamAttemptResponse submitAttempt(Integer attemptId, SubmitExamAttemptRequest request);


    AttemptResultResponse getAttemptResult(Integer attemptId);




    @Transactional(readOnly = true)
    AttemptReviewDetailResponse getAttemptReviewDetail(Integer attemptId);
}
