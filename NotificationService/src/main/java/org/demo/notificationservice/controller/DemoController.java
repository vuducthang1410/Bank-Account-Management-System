package org.demo.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.demo.notificationservice.service.IMailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoController {
    private final IMailService mailService;

    @GetMapping("send-email")
    public void sendEmail() {
        mailService.sendEmail("123xxthang@gmail.com","abc","abc");
    }
}
