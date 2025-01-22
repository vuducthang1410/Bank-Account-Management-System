package org.demo.loanservice.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.MessageValue;
import org.demo.loanservice.common.Util;
import org.demo.loanservice.controllers.exception.InterestRateNotFoundException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Hidden
public class GlobalExceptionHandler {
    private final Util util;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        List<String> errorList = ex.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return createdResponse(errorList, util.getMessageFromMessageSource(MessageValue.INVALID_DATA), "00000", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerMissingParameterException(MissingServletRequestParameterException ex) {
        return createdResponse(util.getMessageFromMessageSource(MessageValue.MISSING_PARAMETER) + ex.getParameterName(),
                "Missing parameter!!!",
                "4000",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerMissingRequestHeaderException(MissingRequestHeaderException ex) {
        return createdResponse(util.getMessageFromMessageSource(MessageValue.MISSING_PARAMETER_IN_HEADER) + ex.getParameter().getParameterName(),
                "Missing parameter!!!",
                "4000",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InterestRateNotFoundException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerInterestRateNotFoundException(InterestRateNotFoundException ex) {
        return createdResponse(util.getMessageFromMessageSource(MessageValue.INTEREST_RATE_NOT_FOUND),
                util.getMessageFromMessageSource(MessageValue.DATA_NOT_FOUND),
                "4000",
                HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(NoSuchMessageException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerNoSuchMessageException(NoSuchMessageException ex){
        return createdResponse(ex.getMessage(),
                "Error",
                "4000",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<DataResponseWrapper<Object>> createdResponse(Object body, String message, String status, HttpStatus httpStatus) {
        return new ResponseEntity<>(new DataResponseWrapper<>(body, message, status), httpStatus);
    }
}
