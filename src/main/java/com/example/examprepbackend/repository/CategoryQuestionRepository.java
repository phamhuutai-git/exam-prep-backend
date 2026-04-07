package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.CategoryQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryQuestionRepository extends JpaRepository<CategoryQuestion, Integer>, JpaSpecificationExecutor<CategoryQuestion> {
    CategoryQuestion findByName(String categoryName);

    boolean existsByName(String name);
}
