package com.example.examprepbackend.service;

import com.example.examprepbackend.entity.Question;

import java.util.List;

public interface QuestionParserService {
    /**
     * Biến văn bản thô thành danh sách Question Object
     */
    List<Question> parseRawText(String rawText);
}