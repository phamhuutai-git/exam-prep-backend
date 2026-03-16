package com.example.examprepbackend.dto.response.exams;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class ExamResponse {

    private Integer id;

    private String code;

    private String title;

    private LocalTime duration;

    private String category;

    private LocalDateTime createDate;

    private Long questions;

    private Long attempts;


}
