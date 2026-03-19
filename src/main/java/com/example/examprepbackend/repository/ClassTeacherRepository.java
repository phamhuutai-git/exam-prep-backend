package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.ClassTeacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassTeacherRepository extends JpaRepository<ClassTeacher, Integer> {

    boolean existsByTeacherId(Integer teacherId);
}
