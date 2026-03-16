package com.example.examprepbackend.repository;

import com.example.examprepbackend.constant.DifficultyLevel;
import com.example.examprepbackend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuestionRepository extends JpaRepository<Question, Integer>, JpaSpecificationExecutor<Question> {
    long countByDifficultyLevel(DifficultyLevel difficultyLevel);
}
