package com.chat.yourway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * {@link ApiExceptionHandler}
 *
 * @author Dmytro Trotsenko on 8/8/23
 */

@ControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler({ServiceException.class})
  public ResponseEntity<ApiErrorResponse> handleNotFoundException(ServiceException exception) {
    return ResponseEntity.status(exception.getHttpStatus())
        .body(new ApiErrorResponse(exception.getHttpStatus(), exception.getMessage()));
  }

}
