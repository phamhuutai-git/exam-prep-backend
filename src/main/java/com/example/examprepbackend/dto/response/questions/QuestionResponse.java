package com.example.examprepbackend.dto.response.questions;

import com.example.examprepbackend.constant.DifficultyLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionResponse {
    private Integer id;

    private String content;

    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficulty;

    private String category;

//    private UserSummaryResponse creator;
//
//    private LocalDateTime createDate;
}
