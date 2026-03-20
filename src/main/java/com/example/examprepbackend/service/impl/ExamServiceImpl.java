package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.request.exams.ExamCreateRequest;
import com.example.examprepbackend.dto.request.exams.ExamRequestParam;
import com.example.examprepbackend.dto.request.exams.ExamUpdateRequest;
import com.example.examprepbackend.dto.response.exams.ExamResponse;
import com.example.examprepbackend.dto.response.exams.ExamSummaryResponse;
import com.example.examprepbackend.entity.*;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.*;
import com.example.examprepbackend.service.ExamQuestionService;
import com.example.examprepbackend.service.ExamService;
import com.example.examprepbackend.specification.ExamSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final UsersRepository usersRepository;
    private final CategoryQuestionRepository categoryQuestionRepository;
    private final QuestionRepository questionRepository;

    private final ExamQuestionService examQuestionService;
    private final ModelMapper modelMapper;

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

    @Transactional
    @Override
    public ExamSummaryResponse createExam(Authentication authentication, ExamCreateRequest examCreateRequest) {

        //Check creator
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }
        Optional<Users> optionalUsers = usersRepository.findByUsername(authentication.getName());
        if (optionalUsers.isEmpty()) {
            throw new ApplicationException("User not found");
        }

        //Check code
        Exam exitsByCode = examRepository.findByCode(examCreateRequest.getCode());
        if (exitsByCode != null) {
            throw new ApplicationException("Exam code exited");
        }

        //Check category
        CategoryQuestion category = categoryQuestionRepository.findByName(examCreateRequest.getCategory());
        if (category == null) {
            throw new ApplicationException("Category not found");
        }

        //Save exam
        Users creator = optionalUsers.get();

        Exam newExam = modelMapper.map(examCreateRequest, Exam.class);
        newExam.setCreator(creator);
        newExam.setCategory(category);
        newExam.setCreateDate(LocalDateTime.now());
        examRepository.save(newExam);

        //Save exam-questions
        List<Integer> questionIds = examCreateRequest.getQuestionIds();
        if (questionIds != null) {
            List<Question> questionList = questionRepository.findByIdIn(examCreateRequest.getQuestionIds());
            examQuestionService.createExamQuestions(newExam, questionList);
        }

        return modelMapper.map(newExam, ExamSummaryResponse.class);
    }

    @Transactional
    @Override
    public ExamSummaryResponse updateExamById(Integer id, ExamUpdateRequest examUpdateRequest) {
        //Check exam by id
        Optional<Exam> examOptional = examRepository.findById(id);
        if (examOptional.isEmpty()) {
            throw new ApplicationException("Exam not found");
        }
        Exam exam = examOptional.get();

        //Check code
        String currentCode = exam.getCode();
        String newCode = examUpdateRequest.getCode();

        if (!newCode.equals(currentCode)) {
            Exam exitsByCode = examRepository.findByCode(newCode);
            if (exitsByCode != null) {
                throw new ApplicationException("Exam code exited");
            }
        }

        //Save exam
        modelMapper.map(examUpdateRequest, exam);
        examRepository.save(exam);

        //Update questions
        //Step1: Xóa toàn bộ câu hỏi theo examId trong bảng trung gian exam_question
        //Step2: Lưu lại dữ liệu theo exam-questions đã nhận vào bảng trung gian exam_question
        examQuestionRepository.deleteByExam_Id(id);

        List<Integer> questionIds = examUpdateRequest.getQuestionIds();
        if (questionIds != null) {
            List<Question> questionList = questionRepository.findByIdIn(examUpdateRequest.getQuestionIds());
            examQuestionService.createExamQuestions(exam, questionList);
        }

        return modelMapper.map(exam, ExamSummaryResponse.class);
    }

    @Transactional
    @Override
    public Boolean deleteExamById(Integer id) {
        Optional<Exam> examOptional = examRepository.findById(id);
        if (examOptional.isEmpty()) {
            throw new ApplicationException("Exam not found");
        }

        examRepository.deleteById(id);

        return true;
    }
}
