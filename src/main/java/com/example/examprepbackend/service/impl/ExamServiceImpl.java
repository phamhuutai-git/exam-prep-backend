package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.request.exams.ExamRequestParam;
import com.example.examprepbackend.dto.response.exams.ExamResponse;
import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.ExamAttemptRepository;
import com.example.examprepbackend.repository.ExamQuestionRepository;
import com.example.examprepbackend.repository.ExamRepository;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.ExamService;
import com.example.examprepbackend.specification.ExamSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final UsersRepository usersRepository;

    private ExamResponse convertToDto(Exam exam) {

        ExamResponse examResponse = new ExamResponse();

        BeanUtils.copyProperties(exam, examResponse);
        examResponse.setCategory(exam.getCategory().getName());
        examResponse.setCreatorName(exam.getCreator().getUsername());
        examResponse.setQuestions(examQuestionRepository.countByExam_Id(exam.getId()));
        examResponse.setAttempts(examAttemptRepository.countByExam_Id(exam.getId()));


        return examResponse;
    }

    @Override
    public Page<ExamResponse> getAllExams(ExamRequestParam examRequestParam, Pageable pageable) {

        String code = examRequestParam.getCode();
        String title = examRequestParam.getTitle();
        String categoryName = examRequestParam.getCategoryName();
        LocalDate minDate = examRequestParam.getMinDate();
        LocalDate maxDate = examRequestParam.getMaxDate();

//        log.info("aa1111111111");
//        Specification<Exam> spec = Specification.where(null);   //ver 3.5.7
//        Specification<Exam> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction(); // ver 4.0.3 lamda

        Specification<Exam> spec = Specification.unrestricted();  //ver 4.0.3

        if (code != null && !code.isBlank()) {
            spec = spec.and(ExamSpecification.hasCodeLike(code));
        }

        if (title != null && !title.isBlank()) {
            spec = spec.and(ExamSpecification.hasTitleLike(title));
        }

        if (categoryName != null && !categoryName.isBlank()) {
            spec = spec.and(ExamSpecification.hasCategoryName(categoryName));
        }

        if (minDate != null && maxDate != null) {
            spec = spec.and(ExamSpecification.hasCreateDate(minDate, maxDate));
        }

        return examRepository.findAll(spec, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ExamResponse> getExamsByTeacherName(Authentication authentication, Pageable pageable) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        String username = authentication.getName();



        return examRepository.findExamsByCreator_Username(username, pageable).map(this::convertToDto);
    }
}
