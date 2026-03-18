package com.example.examprepbackend.service;

import com.example.examprepbackend.dto.request.clazz.ClassRequest;
import com.example.examprepbackend.dto.request.clazz.ClassRequestParam;
import com.example.examprepbackend.dto.response.clazz.ClassResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClassService {

    Page<ClassResponse> getAllClasses(ClassRequestParam classRequestParam, Pageable pageable);

    ClassResponse createClass(ClassRequest classRequest);

    ClassResponse updateClass(Integer id, ClassRequest classRequest);
}
