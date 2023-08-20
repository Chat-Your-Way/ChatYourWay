package com.chat.yourway.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailSendingException extends RuntimeException {

  public EmailSendingException(String message) {
    super(message);
  }
}
