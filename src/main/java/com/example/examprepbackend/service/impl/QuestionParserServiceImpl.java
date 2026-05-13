package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.constant.DifficultyLevel;
import com.example.examprepbackend.entity.Answer;
import com.example.examprepbackend.entity.Question;
import com.example.examprepbackend.service.QuestionParserService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QuestionParserServiceImpl implements QuestionParserService {

    @Override
    public List<Question> parseWordFile(MultipartFile file) throws IOException {
        StringBuilder sb = new StringBuilder();
        // Sử dụng Apache POI bóc text từ file .docx
        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
            for (XWPFParagraph p : doc.getParagraphs()) {
                sb.append(p.getText()).append("\n");
            }
        }
        return parseRawText(sb.toString());
    }

    @Override
    public List<Question> parseRawText(String rawText) {
        List<Question> questions = new ArrayList<>();

        if (rawText == null || rawText.isBlank()) {
            return questions;
        }

        // 1. Tách các khối câu hỏi dựa trên chữ "Câu X:" (Sử dụng lookahead để không làm mất chữ Câu)
        String[] blocks = rawText.split("(?i)(?=Câu\\s*\\d+[:.])");

        for (String block : blocks) {
            String trimmedBlock = block.trim();
            if (trimmedBlock.isEmpty()) continue;

            String explanation = "";
            String contentToParse = trimmedBlock;

            // 2. TÌM VÀ CẮT PHẦN GIẢI THÍCH (Xử lý ưu tiên)
            // Tìm vị trí chữ "Giải thích:" không phân biệt hoa thường
            String lowerBlock = trimmedBlock.toLowerCase();
            int expIdx = lowerBlock.lastIndexOf("giải thích:");

            if (expIdx != -1) {
                // Lấy nội dung sau chữ "Giải thích:"
                explanation = trimmedBlock.substring(expIdx + 11).trim();
                // Cắt bỏ phần giải thích ra khỏi nội dung chính để bóc đáp án không bị sai
                contentToParse = trimmedBlock.substring(0, expIdx).trim();
            }

            // 3. Tách nội dung câu hỏi (Lấy phần trước khi xuất hiện đáp án A. hoặc *A.)
            // Regex bóc tách nội dung câu hỏi và xóa bỏ chữ "Câu X:" ở đầu
            String[] questionParts = contentToParse.split("(?i)\\s*\\*?[A-D]\\.");
            String questionContent = questionParts[0].replaceAll("(?i)Câu\\s*\\d+[:.]", "").trim();

            Question question = new Question();
            question.setContent(questionContent);
            question.setExplanation(explanation); // <--- ĐÃ GÁN GIẢI THÍCH VÀO ĐÂY
            question.setDifficultyLevel(DifficultyLevel.MEDIUM);

            // 4. Bóc tách danh sách đáp án A, B, C, D
            // Regex: Tìm các cụm (A. nội dung), (*A. nội dung), v.v...
            Pattern ansPattern = Pattern.compile("(?i)(\\*?[A-D])\\.\\s*(.*?)(?=\\s+\\*?[A-D]\\.|\\s*$)");
            Matcher ansMatcher = ansPattern.matcher(contentToParse);

            List<Answer> answers = new ArrayList<>();
            while (ansMatcher.find()) {
                String labelPart = ansMatcher.group(1).toUpperCase(); // Ví dụ: "*B"
                String ansContent = ansMatcher.group(2).trim();

                Answer answer = new Answer();
                answer.setContent(ansContent);

                // Nếu có dấu * thì là đáp án đúng
                if (labelPart.startsWith("*")) {
                    answer.setLabel(labelPart.substring(1)); // Lấy "B" từ "*B"
                    answer.setIsCorrect(true);
                } else {
                    answer.setLabel(labelPart);
                    answer.setIsCorrect(false);
                }

                answer.setQuestion(question);
                answers.add(answer);
            }

            question.setAnswers(answers);
            questions.add(question);
        }

        return questions;
    }
}