package com.chat.yourway.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(
      EntityNotFoundException exception) {
    return ResponseEntity.status(NOT_FOUND)
        .body(new ApiErrorResponse(NOT_FOUND, exception.getMessage()));
  }

  @ExceptionHandler(ValueNotUniqException.class)
  public ResponseEntity<ApiErrorResponse> handleValueNotUniqException(
      ValueNotUniqException exception) {
    return ResponseEntity.status(CONFLICT)
        .body(new ApiErrorResponse(CONFLICT, exception.getMessage()));
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidTokenException(
      InvalidTokenException exception) {
    return ResponseEntity.status(UNAUTHORIZED)
        .body(new ApiErrorResponse(UNAUTHORIZED, exception.getMessage()));
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidCredentialsException(
      InvalidCredentialsException exception) {
    return ResponseEntity.status(UNAUTHORIZED)
        .body(new ApiErrorResponse(UNAUTHORIZED, exception.getMessage()));
  }

  @ExceptionHandler(EmailSendingException.class)
  public ResponseEntity<ApiErrorResponse> handleEmailSendingException(
      EmailSendingException exception) {
    return ResponseEntity.status(BAD_REQUEST)
        .body(new ApiErrorResponse(BAD_REQUEST, exception.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException exception) {
    return ResponseEntity.status(NOT_ACCEPTABLE)
        .body(new ApiErrorResponse(NOT_ACCEPTABLE, exception.getMessage()));
  }

}
