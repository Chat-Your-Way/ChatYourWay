package com.chat.yourway.exception;

public class OwnerCantUnsubscribedException extends RuntimeException {

  public OwnerCantUnsubscribedException(String message) {
    super(message);
  }
}
