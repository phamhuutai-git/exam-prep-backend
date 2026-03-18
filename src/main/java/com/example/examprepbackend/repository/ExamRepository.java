package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.Exam;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Integer>, JpaSpecificationExecutor<Exam> {

    Page<Exam> findExamsByCreator_Username(String creatorUsername, Pageable pageable);

}
