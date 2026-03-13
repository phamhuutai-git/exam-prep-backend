package com.example.examprepbackend.dto.response;

import com.example.examprepbackend.constant.DifficultyLevel;
import com.example.examprepbackend.entity.CategoryQuestion;
import com.example.examprepbackend.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
