package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.ExamAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Integer> {

    Long countByExam_Id(Integer exam_id);

    Page<ExamAttempt> findByExam(Exam exam, Pageable pageable);

    Page<ExamAttempt> findByExamIn(List<Exam> examList, Pageable pageable);

}
