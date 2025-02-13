package org.demo.loanservice.common;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.controllers.exception.ServerErrorException;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Util{
    public static final String API_RESOURCE="/api/v1";
    private final MessageSource messageSource;
    public String getMessageFromMessageSource(String key) {
        try {
            return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            throw new ServerErrorException();
        }
    }
}