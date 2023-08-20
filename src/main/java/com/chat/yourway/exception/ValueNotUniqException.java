package com.chat.yourway.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValueNotUniqException extends RuntimeException {

  public ValueNotUniqException(String message) {
    super(message);
  }
}
