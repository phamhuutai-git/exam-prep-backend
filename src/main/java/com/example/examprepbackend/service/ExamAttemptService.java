package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.exams.SubmitExamAttemptRequest;
import com.example.examprepbackend.dto.response.exams.*;
import com.example.examprepbackend.dto.response.questions.AttemptQuestionsFullResponse;
import com.example.examprepbackend.dto.response.teacher.ScoreDistribution;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ExamAttemptService {

    StartExamAttemptResponse startAttempt(Integer examId);

    AttemptQuestionsFullResponse getAttemptQuestions(Integer attemptId);


    SubmitExamAttemptResponse submitAttempt(Integer attemptId, SubmitExamAttemptRequest request);


    AttemptResultResponse getAttemptResult(Integer attemptId);


    ExamStartResponse startExam(Integer examId, Authentication authentication);


    @Transactional(readOnly = true)
    AttemptReviewDetailResponse getAttemptReviewDetail(Integer attemptId);

    List<ScoreDistribution> getScoreDistribution();
}
