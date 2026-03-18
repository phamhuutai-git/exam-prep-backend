package com.example.he_thong_thi.repository;


import com.example.he_thong_thi.entity.CategoryQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryQuestionRepository extends JpaRepository<CategoryQuestion, Integer> {
}