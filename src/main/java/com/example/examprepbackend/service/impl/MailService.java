package com.example.examprepbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j // Dùng log thay vì System.out để chuyên nghiệp hơn
public class MailService {

    private final JavaMailSender mailSender;

    // Lấy email gửi đi từ file application.yml (spring.mail.username)
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendEmail(String toEmail, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail.trim());
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("Email đã gửi thành công tới: {}", toEmail);

        } catch (Exception e) {
            // Đây là nơi bạn sẽ thấy lỗi "đỏ lòm" trong Console nếu sai mật khẩu
            log.error("Lỗi khi gửi email tới {}: {}", toEmail, e.getMessage());
            e.printStackTrace();
        }
    }
}