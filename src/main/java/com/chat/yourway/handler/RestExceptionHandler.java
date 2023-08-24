package com.chat.yourway.handler;

import com.chat.yourway.dto.response.ErrorResponseDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.exception.OldPasswordsIsNotEqualToNewException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OldPasswordsIsNotEqualToNewException.class)
    public ErrorResponseDto handlerNoEqualsPasswordException(OldPasswordsIsNotEqualToNewException exception) {
        return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ContactNotFoundException.class,
            EmailTokenNotFoundException.class})
    public ErrorResponseDto handlerNotFoundException(RuntimeException exception) {
        return new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }
}
