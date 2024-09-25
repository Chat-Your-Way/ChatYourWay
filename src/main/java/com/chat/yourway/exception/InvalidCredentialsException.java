package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class InvalidCredentialsException extends BaseRuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }
}
