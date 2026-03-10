package com.example.examprepbackend.specification;

import com.example.examprepbackend.entity.CategoryQuestion;
import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ExamSpecification {
//    private String code;
//    private String title;
//    private CategoryQuestion category;
//    private LocalDateTime createDate;

    public static Specification<Exam> hasCodeLike(String code) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), "%" + code + "%");
        };
    }

    public static Specification<Exam> hasTitleLike(String title) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title + "%");
        };
    }

    public static Specification<Exam> hasCategoryName(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("category").get("name"), categoryName);
        };
    }

    public static Specification<Exam> hasCreateDate(LocalDate minDate, LocalDate maxDate) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.between(root.get("createDate"), minDate, maxDate);
        };
    }


}
