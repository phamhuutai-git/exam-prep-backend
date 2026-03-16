package com.example.examprepbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "exam")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    private String title;

    private LocalTime duration;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryQuestion category;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private Users creator;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExamClass> examClasses;
}
