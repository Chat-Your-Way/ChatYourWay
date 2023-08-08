package com.chat.yourway.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * {@link ServiceException}
 *
 * @author Dmytro Trotsenko on 8/8/23
 */

@Getter
@RequiredArgsConstructor
public class ServiceException extends RuntimeException {

  private final HttpStatus httpStatus;

  public ServiceException(HttpStatus httpStatus, String message) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
