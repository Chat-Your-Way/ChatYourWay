package com.chat.yourway.handler;

import com.chat.yourway.dto.response.ErrorResponseDto;
import com.chat.yourway.exception.NoEqualsPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoEqualsPasswordException.class)
    public ErrorResponseDto handlerNoEqualsPasswordException(NoEqualsPasswordException e) {
        return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
