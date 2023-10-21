package com.chat.yourway.exception;

public class EmailSendingException extends RuntimeException {
  public EmailSendingException(String message) {
    super(message);
  }
}
