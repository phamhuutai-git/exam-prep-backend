package com.example.examprepbackend.dto.response.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgressDTO {
    private Long studentId;
    private String fullName;
    private String email;
    private boolean isCompleted;
    private Integer attemptCount;
    private Double highestScore;
}
