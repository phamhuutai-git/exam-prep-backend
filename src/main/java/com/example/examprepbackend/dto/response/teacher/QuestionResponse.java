package com.example.examprepbackend.dto.response.teacher;

import com.example.examprepbackend.constant.DifficultyLevel;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"id","content","difficulty","category"})
public class QuestionResponse {
    private Integer id;

    private String content;

    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficulty;

    private String category;

}
