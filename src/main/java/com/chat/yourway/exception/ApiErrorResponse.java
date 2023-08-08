package com.chat.yourway.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * {@link ApiErrorResponse}
 *
 * @author Dmytro Trotsenko on 8/8/23
 */

@Getter
@RequiredArgsConstructor
public class ApiErrorResponse {

  private final HttpStatus httpStatus;
  private final String message;

}
