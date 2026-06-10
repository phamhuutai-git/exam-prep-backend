package com.example.examprepbackend.service;

import com.example.examprepbackend.entity.Question;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionParserService {

    List<Question> parseWordFile(MultipartFile file) throws IOException;

    List<Question> parseRawText(String rawText);
}