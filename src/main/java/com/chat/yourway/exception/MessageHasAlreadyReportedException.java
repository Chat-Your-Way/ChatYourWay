package com.chat.yourway.exception;

public class MessageHasAlreadyReportedException extends RuntimeException {
  public MessageHasAlreadyReportedException() {
    super("Message has already reported.");
  }
}
