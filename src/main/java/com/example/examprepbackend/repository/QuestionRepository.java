package com.example.examprepbackend.repository;

import com.example.examprepbackend.constant.DifficultyLevel;
import com.example.examprepbackend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer>, JpaSpecificationExecutor<Question> {

    List<Question> findByIdIn(List<Integer> ids);

    //stats
    long countByCreator_Username(String username);

    long countByCreator_UsernameAndDifficultyLevel(String username, DifficultyLevel level);
}
