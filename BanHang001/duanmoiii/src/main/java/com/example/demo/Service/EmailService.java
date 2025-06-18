package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    // Sử dụng thư viện để gửi email như JavaMailSender hoặc bất kỳ thư viện nào bạn đang sử dụng
    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String email, String token) {
        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Yêu cầu đặt lại mật khẩu");
        message.setText("Để đặt lại mật khẩu, vui lòng nhấn vào liên kết sau: " + resetLink);

        mailSender.send(message);
    }
}
