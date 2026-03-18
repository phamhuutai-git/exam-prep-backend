package com.example.examprepbackend.entity;

import com.example.examprepbackend.constant.ClassExamStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "class_exam")
@Getter
@Setter
public class ClassExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer classId;

    private Integer examId;

    private Integer duration;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private ClassExamStatus status = ClassExamStatus.HAS_EXAM;
}