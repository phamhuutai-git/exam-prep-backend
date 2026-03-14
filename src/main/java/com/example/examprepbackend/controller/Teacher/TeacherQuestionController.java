package com.example.examprepbackend.controller.Teacher;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.teacher.Question.CreateQuestionRequest;
import com.example.examprepbackend.dto.request.teacher.Question.QuestionRequestParam;
import com.example.examprepbackend.dto.response.teacher.QuestionResponse;
import com.example.examprepbackend.service.QuestionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/teacher/questions")
public class TeacherQuestionController {
    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<QuestionResponse>>> getAllQuestions(QuestionRequestParam param, @PageableDefault(size = 10, sort = "difficultyLevel") Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(questionService.getAllQuestions(param, pageable), "Get all question"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<QuestionResponse>> getQuestionById(@PathVariable Integer id) {
        return ResponseEntity.ok((new BaseResponse<>(questionService.getQuestionById(id), "Get all question")));
    }


    @PostMapping

    public ResponseEntity<BaseResponse<QuestionResponse>> createQuestion(
            @RequestBody CreateQuestionRequest request
    ) {

        return ResponseEntity.ok(
                new BaseResponse<>(
                        questionService.createQuestion(request),
                        "Create question successfully"
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<QuestionResponse>> updateQuestion(
            @PathVariable Integer id,
            @RequestBody CreateQuestionRequest request) {

        return ResponseEntity.ok((new BaseResponse<>(questionService.updateQuestion(id, request), "Get all question")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> deleteQuestion(@PathVariable Integer id) {

        questionService.deleteQuestion(id);

        return ResponseEntity.ok((new BaseResponse("DELETE", "Delete question successfully")));
    }

    //    export excel
    @GetMapping("/export-excel")
    public void exportQuestion(HttpServletResponse response) throws IOException {

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=question.xlsx"
        );

        questionService.exportQuestionToExcel(response);

    }

    @PostMapping("/import-excel")
    public ResponseEntity<BaseResponse<String>> importExcel(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        questionService.importQuestionFromExcel(file);

        return ResponseEntity.ok(
                new BaseResponse<>("IMPORT", "Import questions successfully")
        );
    }
}
