package dto.Request;

import lombok.Data;

import java.util.List;

@Data
public class ExamUpdateRequest {
    private String code;
    private String title;
    private Integer duration;
    private Integer categoryId;
    private List<Integer> questionIds;
}