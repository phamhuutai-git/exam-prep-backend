package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.teacher.categoryes.CategoryRequestParam;
import com.example.examprepbackend.dto.request.teacher.categoryes.CategoryesRequest;
import com.example.examprepbackend.dto.response.teacher.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


public interface CategoriesService {

    Page<CategoryResponse> getCategories(CategoryRequestParam param, Pageable pageable);

    CategoryResponse createCategories(CategoryesRequest request);

    CategoryResponse updateCategories(Integer id, CategoryesRequest request);
}
