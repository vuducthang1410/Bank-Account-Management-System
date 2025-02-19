package org.demo.loanservice.controllers.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataNotValidWithConditionException extends RuntimeException {
    private final String messageKey;
    private final String code;
    private final String condition;

    public DataNotValidWithConditionException(String messageKey, String code, String condition) {
        super();
        this.messageKey = messageKey;
        this.code = code;
        this.condition = condition;
    }
}
