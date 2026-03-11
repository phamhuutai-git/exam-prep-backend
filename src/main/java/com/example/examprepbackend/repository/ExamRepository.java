package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Integer>, JpaSpecificationExecutor<Exam> {

    List<Exam> findByCategoryId(Integer categoryId);

}
