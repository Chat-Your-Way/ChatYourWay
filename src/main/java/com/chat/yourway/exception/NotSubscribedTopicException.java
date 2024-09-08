package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class NotSubscribedTopicException extends BaseRuntimeException {
  public NotSubscribedTopicException(String message) {
    super(message);
  }
}
