package com.example.examprepbackend.dto.response;

import com.example.examprepbackend.entity.CategoryQuestion;
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
