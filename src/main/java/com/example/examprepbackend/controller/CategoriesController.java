package com.example.examprepbackend.controller;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.request.teacher.categoryes.CategoryRequestParam;
import com.example.examprepbackend.dto.request.teacher.categoryes.CategoryesRequest;
import com.example.examprepbackend.dto.response.teacher.CategoryResponse;
import com.example.examprepbackend.service.CategoriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<CategoryResponse>>> getCategories(
            @ModelAttribute CategoryRequestParam param,
            @PageableDefault(size = 1000, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                new BaseResponse<>(
                        categoriesService.getCategories(param, pageable),
                        "Get categories successfully"
                )
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CategoryResponse>> create(
            @RequestBody CategoryesRequest request
    ) {
        return ResponseEntity.ok(
                new BaseResponse<>(
                        categoriesService.createCategories(request),
                        "Create category successfully"
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<CategoryResponse>> update(
            @PathVariable Integer id,
            @RequestBody CategoryesRequest request
    ) {
        return ResponseEntity.ok(
                new BaseResponse<>(
                        categoriesService.updateCategories(id, request),
                        "Update category successfully"
                )
        );
    }
}