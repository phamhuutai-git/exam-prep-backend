package com.example.examprepbackend.service;

import com.example.examprepbackend.entity.Question;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionParserService {
    // Đọc file Word (.docx) trả về List Question (Entity)
    List<Question> parseWordFile(MultipartFile file) throws IOException;

    List<Question> parseRawText(String rawText);
}