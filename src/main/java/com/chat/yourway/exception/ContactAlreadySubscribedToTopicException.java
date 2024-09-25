package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class ContactAlreadySubscribedToTopicException extends BaseRuntimeException {
  public ContactAlreadySubscribedToTopicException(String message) {
    super(message);
  }
}