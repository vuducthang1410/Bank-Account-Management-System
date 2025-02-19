package org.demo.loanservice.controllers.exception;

import lombok.Getter;

@Getter
public class DataNotValidException extends RuntimeException {
    private final String messageKey;
    private final String code;
    public DataNotValidException(String messageKey, String code) {
        super();
        this.messageKey = messageKey;
        this.code = code;
    }
}
