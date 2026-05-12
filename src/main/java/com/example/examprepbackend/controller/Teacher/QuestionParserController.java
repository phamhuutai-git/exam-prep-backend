package com.example.examprepbackend.controller.Teacher;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.teacher.Question.ParserRequest;
import com.example.examprepbackend.entity.Question;
import com.example.examprepbackend.service.QuestionParserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher/questions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class QuestionParserController {

    private final QuestionParserService questionParserService;

    @PostMapping("/parse-preview")
    public ResponseEntity<BaseResponse<List<QuestionPreviewDTO>>> previewQuestions(@RequestBody ParserRequest request) {
        if (request.getRawText() == null || request.getRawText().isBlank()) {
            return ResponseEntity.ok().body(new BaseResponse<>(null, "Nội dung trống"));
        }

        // 1. Gọi Service lấy danh sách Entity
        List<Question> parsedQuestions = questionParserService.parseRawText(request.getRawText());

        // 2. Chuyển đổi Entity -> DTO
        List<QuestionPreviewDTO> responseData = parsedQuestions.stream().map(q -> {
            QuestionPreviewDTO qDto = new QuestionPreviewDTO();
            qDto.setContent(q.getContent());

            // --- DÒNG QUAN TRỌNG NHẤT: Gán giải thích vào đây ---
            qDto.setExplanation(q.getExplanation());

            if (q.getAnswers() != null) {
                qDto.setAnswers(q.getAnswers().stream().map(a ->
                        new AnswerPreviewDTO(a.getContent(), a.getLabel(), a.getIsCorrect())
                ).collect(Collectors.toList()));
            }
            return qDto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok().body(new BaseResponse<>(responseData, "Bóc tách thành công"));
    }

    // --- CÁC DTO ĐÃ ĐƯỢC CẬP NHẬT ---

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionPreviewDTO {
        private String content;
        private String explanation; // <--- ĐÃ THÊM TRƯỜNG NÀY
        private List<AnswerPreviewDTO> answers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerPreviewDTO {
        private String content;
        private String label;
        private boolean isCorrect;
    }
}