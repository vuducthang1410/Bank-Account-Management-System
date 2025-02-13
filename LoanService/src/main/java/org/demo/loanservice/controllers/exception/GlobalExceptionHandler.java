package org.demo.loanservice.controllers.exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.MessageData;
import org.demo.loanservice.common.Util;
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
    private final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        List<String> errorList = ex.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return createdResponse(errorList, util.getMessageFromMessageSource(MessageData.INVALID_DATA.getKeyMessage()), "00000", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerMissingParameterException(MissingServletRequestParameterException ex) {
        return createdResponse(util.getMessageFromMessageSource(MessageData.MISSING_PARAMETER.getKeyMessage()) + ex.getParameterName(),
                util.getMessageFromMessageSource(MessageData.MISSING_PARAMETER.getKeyMessage()),
                "4000",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerMissingRequestHeaderException(MissingRequestHeaderException ex) {
        return createdResponse(util.getMessageFromMessageSource(MessageData.MISSING_PARAMETER_IN_HEADER.getKeyMessage()) + ex.getParameter().getParameterName(),
                util.getMessageFromMessageSource(MessageData.MISSING_PARAMETER.getKeyMessage()),
                "4000",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerInDataNotFoundException(DataNotFoundException ex) {
        String data = util.getMessageFromMessageSource(ex.getMessageKey());
        return createdResponse(util.getMessageFromMessageSource(ex.getMessageKey()),
                util.getMessageFromMessageSource(MessageData.DATA_NOT_FOUND.getKeyMessage()),
                ex.getCode(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataNotValidException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerInDataNotFoundException(DataNotValidException ex) {
        return createdResponse(util.getMessageFromMessageSource(ex.getMessageKey()),
                util.getMessageFromMessageSource(MessageData.DATA_NOT_FOUND.getKeyMessage()),
                ex.getCode(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerServerErrorException(ServerErrorException serverErrorException) {
        return createdResponse(util.getMessageFromMessageSource(MessageData.SERVER_ERROR.getKeyMessage()),
                MessageData.SERVER_ERROR.getMessageLog(),
                MessageData.SERVER_ERROR.getCode(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponseWrapper<Object>> handlerNoSuchMessageException(Exception ex) {
        logger.error(ex.getMessage());
        return createdResponse(MessageData.SERVER_ERROR.getMessageLog(),
                util.getMessageFromMessageSource(MessageData.SERVER_ERROR.getKeyMessage()),
                "4000",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<DataResponseWrapper<Object>> createdResponse(Object body, String message, String status, HttpStatus httpStatus) {
        return new ResponseEntity<>(new DataResponseWrapper<>(body, message, status), httpStatus);
    }
}
