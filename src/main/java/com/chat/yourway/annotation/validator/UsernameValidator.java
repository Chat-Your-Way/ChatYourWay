package com.chat.yourway.annotation.validator;

import com.chat.yourway.annotation.UsernameValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<UsernameValidation, String> {

  private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9а-яА-ЯІіЇї]{4,20}$");

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value.isBlank()) {
      context
          .buildConstraintViolationWithTemplate("The username should not be blank")
          .addConstraintViolation();
      return false;
    } else if (!USERNAME_PATTERN.matcher(value).matches()) {
      context
          .buildConstraintViolationWithTemplate("The username is invalid")
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
