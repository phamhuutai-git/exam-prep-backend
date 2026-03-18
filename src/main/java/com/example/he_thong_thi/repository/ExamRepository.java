package com.example.he_thong_thi.repository;

import com.example.he_thong_thi.entity.Exam;
import dto.Response.ScoreDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Integer> {

    @Query("""
        SELECT new dto.Response.ScoreDTO(
            CONCAT(u.firstName, ' ', u.lastName),
            e.title,
            ea.score
        )
        FROM ExamAttempt ea
        JOIN ea.student u
        JOIN ea.exam e
        WHERE u.classes.id = :classId
    """)
    List<ScoreDTO> getScoreByClass(@Param("classId") int classId);
}