package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.ExamQuestion;
import com.example.examprepbackend.entity.ExamQuestionId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, ExamQuestionId> {
    Long countByExam_Id(Integer examId);
}
