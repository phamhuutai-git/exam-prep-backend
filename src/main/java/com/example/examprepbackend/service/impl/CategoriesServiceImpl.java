package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.request.teacher.categoryes.CategoryRequestParam;
import com.example.examprepbackend.dto.request.teacher.categoryes.CategoryesRequest;
import com.example.examprepbackend.dto.response.teacher.CategoryResponse;
import com.example.examprepbackend.entity.CategoryQuestion;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.CategoryQuestionRepository;
import com.example.examprepbackend.service.CategoriesService;
import com.example.examprepbackend.specification.CategorySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoryQuestionRepository categoryQuestionRepository;

    private CategoryResponse convertToDto(CategoryQuestion category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    @Override
    public Page<CategoryResponse> getCategories(
            CategoryRequestParam param,
            Pageable pageable
    ) {

        Specification<CategoryQuestion> spec = CategorySpecification.alwaysTrue();
        if (param != null) {

            if (param.getName() != null && !param.getName().isBlank()) {
                spec = spec.and(CategorySpecification.hasNameLike(param.getName()));
            }

            if (param.getId() != null) {
                spec = spec.and(CategorySpecification.hasId(param.getId()));
            }
        }

        return categoryQuestionRepository.findAll(spec, pageable)
                .map(this::convertToDto);
    }

    @Override
    public CategoryResponse createCategories(CategoryesRequest request) {

        String name = request.getName().trim();

        if (categoryQuestionRepository.existsByName(name)) {
            throw new ApplicationException("Category already exists");
        }

        CategoryQuestion category = new CategoryQuestion();
        category.setName(name);
        return convertToDto(categoryQuestionRepository.save(category));
    }

    @Override
    public CategoryResponse updateCategories(Integer id, CategoryesRequest request) {

        CategoryQuestion category = categoryQuestionRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Category not found"));

        String newName = request.getName().trim();

        if (!category.getName().equalsIgnoreCase(newName)
                && categoryQuestionRepository.existsByName(newName)) {
            throw new ApplicationException("Category already exists");
        }
        category.setName(newName);
        return convertToDto(categoryQuestionRepository.save(category));
    }
}