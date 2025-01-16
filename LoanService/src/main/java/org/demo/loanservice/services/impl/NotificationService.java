package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.services.INotificationService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {
    private final StreamBridge streamBridge;

}
