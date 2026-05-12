package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.request.teacher.Exam.AiGenerateRequest;
import com.example.examprepbackend.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

        // 1. Kiểm tra đầu vào
        String topic = (request.getPromptText() != null) ? request.getPromptText() : "Kiến thức chung";
        int count = (request.getQuantity() > 0) ? request.getQuantity() : 5;

        // 2. Prompt tối ưu (Ép định dạng chuẩn)
        String prompt = String.format("""
        Hãy tạo %d câu hỏi trắc nghiệm về chủ đề: '%s'.
        Yêu cầu:
        - 4 đáp án A, B, C, D. Đánh dấu * vào trước đáp án đúng.
        - BẮT BUỘC có thêm một dòng "Giải thích: [Nội dung giải thích]" ngay sau đáp án D.
        - Chỉ trả về nội dung câu hỏi, không chào hỏi, không dùng markdown.
        Ví dụ:
        Câu 1: Thủ đô của Việt Nam là gì?
        A. TP.HCM
        *B. Hà Nội
        C. Đà Nẵng
        D. Cần Thơ
        Giải thích: Hà Nội là thủ đô của nước Cộng hòa Xã hội chủ nghĩa Việt Nam từ năm 1976.
        """, count, topic);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

        // 3. Cấu hình Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Gắn Key thẳng vào URL thay vì Header để tương thích tốt nhất với Gemini
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        String finalUrl = apiUrl + "?key=" + apiKey;

        try {
            System.out.println("--- ĐANG GỌI GEMINI API VỚI CHỦ ĐỀ: " + topic + " ---");

            // Dùng postForObject lấy thẳng ra Map, không cần ResponseEntity nữa
            Map response = restTemplate.postForObject(finalUrl, entity, Map.class);

            // LOG TOÀN BỘ RESPONSE ĐỂ DEBUG
            System.out.println("FULL RESPONSE TỪ GOOGLE: " + response);

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (candidates.isEmpty()) return "Google AI không tìm thấy kết quả phù hợp.";

                Map<String, Object> firstCandidate = candidates.get(0);

                // KIỂM TRA LÝ DO DỪNG (Nếu bị chặn do an toàn)
                if (firstCandidate.containsKey("finishReason") && !firstCandidate.get("finishReason").equals("STOP")) {
                    return "AI từ chối trả lời do vi phạm chính sách nội dung (Safety Filter).";
                }

                Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

                String rawText = (String) parts.get(0).get("text");
                return rawText.replace("```", "").trim();
            }

            return "Cấu trúc phản hồi không xác định.";

        } catch (Exception e) {
            System.err.println("!!! LỖI CHI TIẾT: " + e.getMessage());
            return "Lỗi kết nối AI: " + e.getMessage();
        }
    }
}