package org.demo.notificationservice.service;

public interface IMailService {
    void sendEmail(String to, String subject, String body);
}
