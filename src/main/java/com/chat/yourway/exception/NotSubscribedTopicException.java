package com.chat.yourway.exception;

public class NotSubscribedTopicException extends RuntimeException {
  public NotSubscribedTopicException(String message) {
    super(message);
  }
}
