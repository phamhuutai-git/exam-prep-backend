package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.request.teacher.Exam.AiGenerateRequest;
import com.example.examprepbackend.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI; // <--- Thêm thư viện này để xử lý URL
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiServiceImpl implements AiService {

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Override
    @SuppressWarnings("unchecked")
    public String generateQuestions(AiGenerateRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Dịch độ khó sang tiếng Việt
        String doKho = switch (request.getDifficulty()) {
            case "EASY" -> "Dễ";
            case "HARD" -> "Khó";
            default -> "Trung bình";
        };

        // 2. Câu lệnh Prompt ép AI trả về JSON nguyên chất
        String prompt = String.format("""
                Bạn là một chuyên gia ra đề thi trắc nghiệm. Dựa vào nội dung/chủ đề sau: '%s'.
                Hãy tạo ra đúng %d câu hỏi trắc nghiệm ở mức độ %s.
                Mỗi câu hỏi phải có 4 đáp án và chỉ định rõ 1 đáp án đúng.
                BẮT BUỘC trả về kết quả là một mảng JSON nguyên chất theo đúng cấu trúc sau, không kèm văn bản giải thích (không dùng markdown):
                [
                  {
                    "content": "Nội dung câu hỏi?",
                    "explanation": "Giải thích vì sao đúng",
                    "difficulty": "%s",
                    "answers": [
                      { "content": "Đáp án A", "isCorrect": true },
                      { "content": "Đáp án B", "isCorrect": false },
                      { "content": "Đáp án C", "isCorrect": false },
                      { "content": "Đáp án D", "isCorrect": false }
                    ]
                  }
                ]
                """, request.getPromptText(), request.getQuantity(), doKho, request.getDifficulty());

        // 3. Đóng gói Body Request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                        Map.of("text", prompt)
                ))
        ));

        // 4. Thiết lập Header chuẩn bảo mật
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey); // Chuyển API Key vào Header

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 5. Gửi Request bằng URI để tránh lỗi 404
        try {
            // Ép kiểu sang URI để RestTemplate không tự động đổi dấu ":" thành "%3A"
            URI uri = URI.create(apiUrl);
            Map<String, Object> response = restTemplate.postForObject(uri, entity, Map.class);

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

                return (String) parts.get(0).get("text");
            }
            return "[]";

        } catch (Exception e) {
            System.err.println("Lỗi khi gọi API Gemini: " + e.getMessage());
            throw new RuntimeException("Lỗi kết nối AI Server. Hãy kiểm tra lại mạng!");
        }
    }
}