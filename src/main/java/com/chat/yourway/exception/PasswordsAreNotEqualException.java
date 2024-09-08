package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class PasswordsAreNotEqualException extends BaseRuntimeException {
  public PasswordsAreNotEqualException() {
    super("Passwords are not equal.");
  }
}
