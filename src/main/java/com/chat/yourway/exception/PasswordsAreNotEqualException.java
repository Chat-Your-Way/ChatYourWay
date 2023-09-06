package com.chat.yourway.exception;

public class PasswordsAreNotEqualException extends RuntimeException {
  public PasswordsAreNotEqualException() {
    super("Passwords are not equal.");
  }
}
