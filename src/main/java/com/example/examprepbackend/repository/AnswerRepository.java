package com.example.examprepbackend.repository;
import com.example.examprepbackend.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    List<Answer> findByQuestionIdIn(List<Integer> questionIds);

    void deleteByQuestion_Id(Integer questionId);
}
