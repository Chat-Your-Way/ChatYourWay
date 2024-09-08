package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class OwnerCantUnsubscribedException extends BaseRuntimeException {

  public OwnerCantUnsubscribedException(String message) {
    super(message);
  }
}
