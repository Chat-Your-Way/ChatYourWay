package com.chat.yourway.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvalidCredentialsException extends RuntimeException {

  public InvalidCredentialsException(String message) {
    super(message);
  }
}
