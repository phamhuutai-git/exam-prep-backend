package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Integer> {

    Long countByExam_Id(Integer exam_id);

}
