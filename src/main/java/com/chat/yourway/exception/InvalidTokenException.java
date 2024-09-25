package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class InvalidTokenException extends BaseRuntimeException {

  public InvalidTokenException(String message) {
    super(message);
  }
}
