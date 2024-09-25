package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class TopicSubscriberNotFoundException extends BaseRuntimeException {

  public TopicSubscriberNotFoundException(String message) {
    super(message);
  }
}
