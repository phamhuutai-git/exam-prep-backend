package com.example.he_thong_thi.repository;

import com.example.he_thong_thi.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Integer> {

    @Modifying
    @Query("DELETE FROM ExamQuestion eq WHERE eq.exam.id = :examId")
    void deleteByExamId(@Param("examId") int examId);

    @Query("SELECT eq FROM ExamQuestion eq WHERE eq.exam.id = :examId")
    List<ExamQuestion> findByExamId(@Param("examId") int examId);
}