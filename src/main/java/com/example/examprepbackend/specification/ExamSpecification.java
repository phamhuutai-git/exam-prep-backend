package com.example.examprepbackend.specification;

import com.example.examprepbackend.entity.Exam;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ExamSpecification {
//    private String code;
//    private String title;
//    private CategoryQuestion category;
//    private LocalDateTime createDate;

    public static Specification<Exam> hasCreatorUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("creator").get("username"), username);
        };
    }

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

    public static Specification<Exam> hasClassId(Integer classId) {
        return (root, query, criteriaBuilder) -> {
            if (classId == null) return null;
            return criteriaBuilder.equal(root.join("examClasses").join("classes").get("id"), classId);
        };

    }
}
