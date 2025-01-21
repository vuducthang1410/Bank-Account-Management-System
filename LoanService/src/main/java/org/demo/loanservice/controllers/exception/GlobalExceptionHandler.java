package org.demo.loanservice.controllers.exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.enumDto.MessagesKey;
import org.demo.loanservice.common.Util;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.ValidationFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RequiredArgsConstructor
@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
    private final Util util;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        List<String> messageData = ex.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return createResponse(
                messageData,
                util.getMessageFromMessageSource(MessagesKey.INVALID_DATA.getKey()),
                "0000",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationFailureException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handleValidationFailureException(ValidationFailureException ex) {
        return createResponse(ex.getMessage(),
                util.getMessageFromMessageSource(MessagesKey.INVALID_DATA.getKey()),
                HttpStatus.BAD_REQUEST.toString(),
                HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<DataResponseWrapper<Object>> createResponse(Object body, String message, String status, HttpStatus httpStatus) {
        return new ResponseEntity<>(new DataResponseWrapper<>(body, message, status), httpStatus);
    }
}
