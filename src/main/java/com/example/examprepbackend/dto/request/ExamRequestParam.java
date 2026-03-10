package com.example.examprepbackend.dto.request;

import com.example.examprepbackend.entity.CategoryQuestion;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ExamRequestParam {
    private String code;

    private String title;

    private String categoryName;

    private LocalDate minDate;

    private LocalDate maxDate;
}
