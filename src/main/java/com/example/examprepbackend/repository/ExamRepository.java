package com.example.examprepbackend.repository;

import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.entity.Exam;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Integer>, JpaSpecificationExecutor<Exam> {

    List<Exam> findExamsByCreator_Username(String creatorUsername);

    Exam findByCode(String code);

    List<Exam> findByIdIn(List<Integer> idList);

    Page<Exam> findPageByExamTypeAndIdIn(ExamType examType, List<Integer> idList, Pageable pageable);

    @Query(value = "SELECT e.* FROM exam e " +
            "JOIN class_exam ce ON e.id = ce.exam_id " +
            "WHERE ce.class_id = :classId " +
            "AND e.exam_type = 'OFFICIAL' " +
            "AND (e.start_time IS NULL OR e.start_time <= :now) " +
            "AND (e.end_time IS NULL OR e.end_time >= :now) " +
            "AND e.id NOT IN (SELECT exam_id FROM exam_attempt WHERE student_id = :studentId)",
            nativeQuery = true)
    List<Exam> findAvailableOfficialExamsForStudent(
            @Param("classId") Integer classId,
            @Param("studentId") Integer studentId,
            @Param("now") LocalDateTime now
    );

}