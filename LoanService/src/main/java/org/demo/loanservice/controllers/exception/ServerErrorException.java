package org.demo.loanservice.controllers.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServerErrorException extends RuntimeException {
    private String transactionId;
    public ServerErrorException() {
        super();
    }
    public ServerErrorException(String transactionId) {
        super();
        this.transactionId=transactionId;
    }

}
