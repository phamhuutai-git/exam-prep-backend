package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.ExamAttempt;
import com.example.examprepbackend.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Integer> {

    Long countByExam_Id(Integer exam_id);

    Page<ExamAttempt> findByStudent(Users student, Pageable pageable);

}
