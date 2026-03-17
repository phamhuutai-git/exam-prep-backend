package com.example.examprepbackend.controller.Admin;


import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.clazz.ClassRequest;
import com.example.examprepbackend.dto.request.clazz.ClassRequestParam;
import com.example.examprepbackend.dto.response.clazz.ClassResponse;
import com.example.examprepbackend.service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/classes")
@RequiredArgsConstructor
public class AdminClassController {

    private final ClassService classService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<ClassResponse>>> getAllClasses(ClassRequestParam classRequestParam, @PageableDefault(size = 5) Pageable pageable) {
        return ResponseEntity.ok().body(new BaseResponse<>(classService.getAllClasses(classRequestParam, pageable), "Get All Classes"));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ClassResponse>> createClass(@RequestBody @Valid ClassRequest classRequest) {
        return ResponseEntity.ok().body(new BaseResponse<>(classService.createClass(classRequest), "Created class"));
    }

    @PutMapping
    public ResponseEntity<BaseResponse<ClassResponse>> updateClass(@RequestBody @Valid ClassRequest classRequest) {
        return ResponseEntity.ok().body(new BaseResponse<>(classService.updateClass(classRequest), "Updated class"));
    }
}
