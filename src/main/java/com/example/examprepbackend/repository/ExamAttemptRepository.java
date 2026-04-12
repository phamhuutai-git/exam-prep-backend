package com.example.examprepbackend.repository;

import com.example.examprepbackend.constant.AttemptStatus;
import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.dto.response.teacher.ScoreDistribution;
import com.example.examprepbackend.entity.Exam;
import com.example.examprepbackend.entity.ExamAttempt;
import com.example.examprepbackend.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Integer> {

    Long countByExam_Id(Integer exam_id);

    Optional<ExamAttempt> findByIdAndStudentUsername(Integer id, String username);

    Page<ExamAttempt> findByExam(Exam exam, Pageable pageable);

    Page<ExamAttempt> findByExamIn(List<Exam> examList, Pageable pageable);

    Page<ExamAttempt> findByStudent(Users student, Pageable pageable);

//    ExamAttempt findByExamAndStatus(Exam exam, AttemptStatus status);

    boolean existsByExamAndStudentAndStatus(Integer exam, Integer studentId, AttemptStatus status);

    ExamAttempt findByExamAndStudentAndStatus(Exam exam, Users student, AttemptStatus status);

    Page<ExamAttempt> findByStudentAndExamExamType(Users user, ExamType examType, Pageable pageable);
    @Query("""
                SELECT new com.example.examprepbackend.dto.response.teacher.ScoreDistribution(
                    CASE 
                        WHEN e.score >= 0 AND e.score < 40 THEN '0-40'
                               WHEN e.score >= 40 AND e.score < 50 THEN '40-50'
                               WHEN e.score >= 50 AND e.score < 60 THEN '50-60'
                               WHEN e.score >= 60 AND e.score < 70 THEN '60-70'
                               WHEN e.score >= 70 AND e.score < 80 THEN '70-80'
                               WHEN e.score >= 80 AND e.score < 90 THEN '80-90'
                               WHEN e.score >= 90 AND e.score <= 100 THEN '90-100'
                    END,
                    COUNT(e)
                )
                FROM ExamAttempt e
                WHERE e.status = 'SUBMITTED'
                GROUP BY 
                    CASE 
                               WHEN e.score >= 0 AND e.score < 40 THEN '0-40'
                                 WHEN e.score >= 40 AND e.score < 50 THEN '40-50'
                               WHEN e.score >= 50 AND e.score < 60 THEN '50-60'
                               WHEN e.score >= 60 AND e.score < 70 THEN '60-70'
                               WHEN e.score >= 70 AND e.score < 80 THEN '70-80'
                               WHEN e.score >= 80 AND e.score < 90 THEN '80-90'
                               WHEN e.score >= 90 AND e.score <= 100 THEN '90-100'
                    END
                ORDER BY 1
            """)
    List<ScoreDistribution> getScoreDistribution();
}
