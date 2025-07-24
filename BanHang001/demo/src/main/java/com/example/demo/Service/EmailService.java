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

    public void sendVerificationEmail(String to, String maXacThuc) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Xác nhận đăng ký tài khoản");
        message.setText("Mã xác thực của bạn là: " + maXacThuc + "\nMã có hiệu lực trong 10 phút.");
        mailSender.send(message);
    }
}
