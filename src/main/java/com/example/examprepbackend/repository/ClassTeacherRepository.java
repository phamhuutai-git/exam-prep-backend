package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.ClassTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassTeacherRepository extends JpaRepository<ClassTeacher, Integer> {

    @Query("select cl.teacher.id from ClassTeacher cl where cl.classes.id = :classId")
    List<Integer> findByClasses_Id(@Param("classId") Integer classId);

    @Modifying
    @Query("delete from ClassTeacher cl where cl.classes.id = :classId")
    void deleteByClasses_Id(@Param("classId") Integer classId);

}
