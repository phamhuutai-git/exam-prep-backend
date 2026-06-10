package com.example.examprepbackend.entity;

import com.example.examprepbackend.constant.ExamType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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

    // 🔥 Đã đổi sang Integer để hứng dữ liệu Số phút từ Database
    private Integer duration;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryQuestion category;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private Users creator;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type", nullable = false)
    private ExamType examType;

    @Column(name = "review_allowed", nullable = false)
    private Boolean reviewAllowed;

    @Column(name = "pass_score", nullable = false)
    private Double passScore;

    // 🔥 Bổ sung 2 trường để quản lý thời gian bắt đầu và kết thúc làm bài
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

}