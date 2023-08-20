package com.chat.yourway.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvalidTokenException extends RuntimeException {

  public InvalidTokenException(String message) {
    super(message);
  }
}
