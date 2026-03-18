package dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScoreDTO {
    private String fullName;
    private String examTitle;
    private double score;
}