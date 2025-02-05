package org.demo.loanservice.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.MessageValue;
import org.demo.loanservice.common.Util;
import org.demo.loanservice.controllers.exception.InterestRateNotFoundException;
import org.demo.loanservice.controllers.exception.TypeMortgagedAssetsNotFoundException;
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
    private final Logger logger= LogManager.getLogger(GlobalExceptionHandler.class);

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
                util.getMessageFromMessageSource(MessageValue.MISSING_PARAMETER),
                "4000",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerMissingRequestHeaderException(MissingRequestHeaderException ex) {
        return createdResponse(util.getMessageFromMessageSource(MessageValue.MISSING_PARAMETER_IN_HEADER) + ex.getParameter().getParameterName(),
                util.getMessageFromMessageSource(MessageValue.MISSING_PARAMETER),
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
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerNoSuchMessageException(Exception ex){
        logger.error(ex.getMessage());
        return createdResponse("Internal_error",
                util.getMessageFromMessageSource(MessageValue.SERVER_ERROR),
                "4000",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(TypeMortgagedAssetsNotFoundException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerTypeMortgagedAssetsNotFoundException(TypeMortgagedAssetsNotFoundException ex){
        logger.info("Handler for TypeMortgagedAssetsNotFoundException invoked");
        return createdResponse(util.getMessageFromMessageSource(MessageValue.TYPE_MORTGAGED_ASSET_NOT_FOUND),
                util.getMessageFromMessageSource(MessageValue.DATA_NOT_FOUND),
                "50000",
                HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<DataResponseWrapper<Object>> createdResponse(Object body, String message, String status, HttpStatus httpStatus) {
        return new ResponseEntity<>(new DataResponseWrapper<>(body, message, status), httpStatus);
    }
}
