package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class ValueNotUniqException extends BaseRuntimeException {
  public ValueNotUniqException(String message) {
    super(message);
  }
}