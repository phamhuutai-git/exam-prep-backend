package com.example.examprepbackend.dto.request.exams;

import com.example.examprepbackend.entity.CategoryQuestion;
import com.example.examprepbackend.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ExamCreateRequest {

    @NotNull(message = "Exam code not null")
    @NotBlank(message = "Exam code not blank")
    private String code;

    @NotNull(message = "Exam title not null")
    @NotBlank(message = "Exam title not blank")
    private String title;

    @NotNull(message = "Exam duration not null")
    private LocalTime duration;

    @NotNull(message = "Exam category not null")
    @NotBlank(message = "Exam category not blank")
    private String category;

    private List<Integer> questionIds;

}
