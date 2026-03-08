package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
}
