package com.example.examprepbackend.repository;
import com.example.examprepbackend.entity.ClassExam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassExamRepository extends JpaRepository<ClassExam, Long> {
    List<ClassExam> findByClassId(Long classId);
}
