package com.chat.yourway.exception;

public class OldPasswordsIsNotEqualToNewException extends RuntimeException {

  public OldPasswordsIsNotEqualToNewException(String message) {
    super(message);
  }
}
