package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class EmailSendingException extends BaseRuntimeException {
  public EmailSendingException(String message) {
    super(message);
  }
}
