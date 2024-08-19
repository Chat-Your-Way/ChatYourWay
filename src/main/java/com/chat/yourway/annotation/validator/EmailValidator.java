package com.chat.yourway.annotation.validator;

import com.chat.yourway.annotation.EmailValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static com.chat.yourway.utils.Constants.*;

public class EmailValidator implements ConstraintValidator<EmailValidation, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();

    if (value.isBlank()) {
      context.buildConstraintViolationWithTemplate("The email should not be blank").addConstraintViolation();
      return false;
    } else if (value.length() < MIN_EMAIL_LENGTH) {
      context.buildConstraintViolationWithTemplate("The email must be at least 6 characters").addConstraintViolation();
      return false;
    } else if (value.length() > MAX_EMAIL_LENGTH) {
      context.buildConstraintViolationWithTemplate("The email must not be longer than 255 characters")
                .addConstraintViolation();
      return false;
    } else if (!EMAIL_PATTERN.matcher(value).matches()) {
      context.buildConstraintViolationWithTemplate("The email is invalid").addConstraintViolation();
      return false;
    }
    return true;
  }
}
