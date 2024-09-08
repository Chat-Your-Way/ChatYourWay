package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class MessageNotFoundException extends BaseRuntimeException {
  public MessageNotFoundException() {
    super("Message is not found.");
  }
}
