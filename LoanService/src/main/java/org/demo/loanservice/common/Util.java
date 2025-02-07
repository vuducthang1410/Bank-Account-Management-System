package org.demo.loanservice.common;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Util{

    private final MessageSource messageSource;
    private PasswordEncoder passwordEncoder;
    public String getMessageFromMessageSource(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
    public String generateChecksum(String transactionId,String content){
        return passwordEncoder.encode(transactionId.concat(content));
    }
    public Boolean verifyChecksum(String checkSum,String transactionId,String content){
        return passwordEncoder.matches(transactionId.concat(content),checkSum);
    }
}