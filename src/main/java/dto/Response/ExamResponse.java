package dto.Response;

import lombok.Data;

import java.util.List;

@Data
public class ExamResponse {
    private Integer id;
    private String code;
    private String title;
    private Integer duration;
    private Integer categoryId;
    private String categoryName;
    private List<Integer> questionIds;
}