package org.demo.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.demo.notificationservice.service.IMailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService implements IMailService {
    private final JavaMailSender javaMailSender;
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your_email@example.com");
        message.setTo(to);
        message.setSubject("[HYGGE]Reset your password!!");
        message.setText(body);
        javaMailSender.send(message);
    }
}
