package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScoreDTO {
    private String fullName;
    private String examTitle;
    private double score;
}