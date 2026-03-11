package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.request.ExamRequestParam;
import com.example.examprepbackend.dto.response.ExamResponse;
import com.example.examprepbackend.dto.response.UserSummaryResponse;
import com.example.examprepbackend.entity.CategoryQuestion;
import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.repository.ExamRepository;
import com.example.examprepbackend.service.ExamService;
import com.example.examprepbackend.specification.ExamSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {


    private ExamResponse convertToDto(Exam exam) {

        ExamResponse examResponse = new ExamResponse();
        UserSummaryResponse creator = new UserSummaryResponse();

        BeanUtils.copyProperties(exam, examResponse);
        BeanUtils.copyProperties(exam.getCreator(), creator);

        examResponse.setCreator(creator);

        return examResponse;
    }

    private final ExamRepository examRepository;

    @Override
    public Page<ExamResponse> getAllExams(ExamRequestParam examRequestParam, Pageable pageable) {

        String code = examRequestParam.getCode();
        String title = examRequestParam.getTitle();
        String categoryName = examRequestParam.getCategoryName();
        LocalDate minDate = examRequestParam.getMinDate();
        LocalDate maxDate = examRequestParam.getMaxDate();

        log.info("aa1111111111");
//        Specification<Exam> spec = Specification.where((Specification<Exam>) null);
//
//        if (code != null && !code.isBlank()) {
//            spec = spec.and(ExamSpecification.hasCodeLike(code));
//        }
//
//        if (title != null && !title.isBlank()) {
//            spec = spec.and(ExamSpecification.hasTitleLike(title));
//        }
//
//        if (categoryName != null && !categoryName.isBlank()) {
//            spec = spec.and(ExamSpecification.hasCategoryName(categoryName));
//        }
//
//        if (minDate != null && maxDate != null) {
//            spec = spec.and(ExamSpecification.hasCreateDate(minDate, maxDate));
//        }

        return examRepository.findAll(pageable).map(this::convertToDto);
    }
    @Override
    public Page<ExamResponse> getExamByCategory(Integer categoryId, Pageable pageable) {

        return examRepository
                .findByCategoryId(categoryId, pageable)
                .map(this::convertToDto);
    }
}
