package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.constant.AttemptStatus;
import com.example.examprepbackend.dto.response.exams.ExamStartResponse;
import com.example.examprepbackend.dto.response.questions.QuestionPublicResponse;
import com.example.examprepbackend.entity.ClassExam;
import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.ExamAttempt;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.ClassExamRepository;
import com.example.examprepbackend.repository.ExamAttemptRepository;
import com.example.examprepbackend.repository.ExamRepository;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.ExamAttemptServiceTai;
import com.example.examprepbackend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamAttemptServiceImplTai implements ExamAttemptServiceTai {

    private final ExamAttemptRepository examAttemptRepository;
    private final QuestionService questionService;
    private final UsersRepository usersRepository;
    private final ExamRepository examRepository;
    private final ClassExamRepository classExamRepository;

    private ExamStartResponse convertToStartDto(ExamAttempt examAttempt) {
        ExamStartResponse startResponse = new ExamStartResponse();

        startResponse.setAttemptId(examAttempt.getId());
        startResponse.setExamCode(examAttempt.getExam().getCode());
        startResponse.setExamTitle(examAttempt.getExam().getTitle());
        startResponse.setDuration(examAttempt.getExam().getDuration());
        startResponse.setExamType(examAttempt.getExam().getExamType().toString());
        startResponse.setStartTime(examAttempt.getStartTime());

        List<QuestionPublicResponse> questionPublicResponses = questionService.getQuestionsPublicByExamId(examAttempt.getExam().getId());
        startResponse.setQuestions(questionPublicResponses);

        return startResponse;

    }

    @Override
    public ExamStartResponse startExam(Integer examId, Authentication authentication) {

        //Kiem tra hoc sinh dang lam bai thi
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        String username = authentication.getName();
        Optional<Users> usersOptional = usersRepository.findByUsername(username);
        if (usersOptional.isEmpty()) {
            throw new ApplicationException("User not found");
        }

        Users user = usersOptional.get();

        //Kiem tra de thi
        Optional<Exam> examOptional = examRepository.findById(examId);
        if (examOptional.isEmpty()) {
            throw new ApplicationException("Exam not found");
        }

        Exam exam = examOptional.get();

        //Kiem tra de thi co o trong lop cua hoc sinh khong
        ClassExam classExam = classExamRepository.findByClassIdAndExamId(user.getClasses().getId(), examId);
        if (classExam == null) {
            throw new ApplicationException("The selected exam does not belong to this class.");
        }

        //Kiem tra xem duoi database da co attempt voi examId va dang trong trang thai IN_PROGRESS
        ExamAttempt examAttemptExits = examAttemptRepository.findByExamAndStatus(exam, AttemptStatus.IN_PROGRESS);
        if (examAttemptExits != null) {
            throw new ApplicationException("You already have an ongoing attempt for this exam. " +
                    "Please complete or submit it before starting a new one.");
        }

        //Tao exam_attempt va luu xuong database
        ExamAttempt examAttempt = new ExamAttempt();
        examAttempt.setExam(exam);
        examAttempt.setStudent(user);
        examAttempt.setStartTime(LocalDateTime.now());
        examAttempt.setScore(0.0);
        examAttempt.setCorrectCount(0);
        examAttempt.setWrongCount(0);
        examAttempt.setBlankCount(0);
        examAttempt.setTimeSpentSeconds(0);
        examAttempt.setStatus(AttemptStatus.IN_PROGRESS);

        examAttemptRepository.save(examAttempt);

        return convertToStartDto(examAttempt);
    }
}
