package com.chat.yourway.exception;

public class MessageNotFoundException extends RuntimeException {
  public MessageNotFoundException() {
    super("Message is not found.");
  }
}
