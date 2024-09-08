package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class MessageHasAlreadyReportedException extends BaseRuntimeException {
  public MessageHasAlreadyReportedException() {
    super("Message has already reported.");
  }
}
