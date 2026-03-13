package com.example.examprepbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamClassId implements Serializable {
    @Column(name = "exam_id")
    private Integer examId;

    @Column(name = "class_id")
    private Integer classId;
}