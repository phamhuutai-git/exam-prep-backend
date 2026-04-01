package com.example.examprepbackend.repository;

import com.example.examprepbackend.constant.AttemptStatus;
import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.ExamAttempt;
import com.example.examprepbackend.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.util.List;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Integer> {

    Long countByExam_Id(Integer exam_id);

    Optional<ExamAttempt> findByIdAndStudentUsername(Integer id, String username);

    Page<ExamAttempt> findByExam(Exam exam, Pageable pageable);

    Page<ExamAttempt> findByExamIn(List<Exam> examList, Pageable pageable);

    Page<ExamAttempt> findByStudent(Users student, Pageable pageable);

    ExamAttempt findByExamAndStatus(Exam exam, AttemptStatus status);

}
