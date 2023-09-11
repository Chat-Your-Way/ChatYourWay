package com.chat.yourway.annotation.validator;

import com.chat.yourway.annotation.EmailValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<EmailValidation, String> {
  private static final int MIN_LENGTH = 6;
  private static final int MAX_LENGTH = 320;
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("[a-z0-9.\\-_]+@[a-z]+\\.[a-z]{2,3}");

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();

    if (value.isBlank()) {
      context
              .buildConstraintViolationWithTemplate("The email should not be blank")
              .addConstraintViolation();
      return false;
    } else if (value.length() < MIN_LENGTH) {
      context
              .buildConstraintViolationWithTemplate("The email must be at least 6 characters")
              .addConstraintViolation();
      return false;
    } else if (value.length() > MAX_LENGTH) {
      context
              .buildConstraintViolationWithTemplate("The email must not be longer than 320 characters")
              .addConstraintViolation();
      return false;
    } else if (!EMAIL_PATTERN.matcher(value).matches()) {
      context
              .buildConstraintViolationWithTemplate("The email is invalid")
              .addConstraintViolation();
      return false;
    }

    return true;
  }
}
