package com.example.examprepbackend.dto.response.exams;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class ExamSummaryResponse {
    private Integer id;

    private String code;

    private String title;

}
