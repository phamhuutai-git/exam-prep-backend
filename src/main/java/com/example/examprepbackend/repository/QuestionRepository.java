package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
