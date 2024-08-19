package com.chat.yourway.annotation.validator;

import com.chat.yourway.annotation.PasswordValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import static com.chat.yourway.utils.Constants.*;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    if (value.isBlank()) {
      context.buildConstraintViolationWithTemplate("The password should not be blank.").addConstraintViolation();
      return false;
    } else if (value.length() < MIN_PASSWORD_LENGTH) {
      context.buildConstraintViolationWithTemplate("The password must be at least 4 characters.")
              .addConstraintViolation();
      return false;
    } else if (value.length() > MAX_PASSWORD_LENGTH) {
      context.buildConstraintViolationWithTemplate("The password must not be longer than 12 characters.")
              .addConstraintViolation();
      return false;
    }  else if (PASSWORD_FORBIDEN_SYMBOLS_PATTERN.matcher(value).matches()) {
      context.buildConstraintViolationWithTemplate("Password should not include symbols [< > ; / . : ' [ ] ( ) , ]")
          .addConstraintViolation();
      return false;
    } else if (PASSWORD_FORBIDEN_CYRILLIC_LETTERS_PATTERN.matcher(value).matches()) {
      context.buildConstraintViolationWithTemplate("Password should not include cyrillic letters")
                .addConstraintViolation();
      return false;
    }else if (!PASSWORD_SPECIAL_SYMBOLS_PATTERN.matcher(value).matches()) {
      context.buildConstraintViolationWithTemplate("Password must include at least 1 special symbol: [! @ # $ % ^ & * _ - + = ~ ?]")
              .addConstraintViolation();
      return false;
    } else if (!PASSWORD_UPPER_CASE_PATTERN.matcher(value).matches()) {
      context.buildConstraintViolationWithTemplate("Password must include at least 1 Upper-case letter")
                .addConstraintViolation();
      return false;
    } else if (!PASSWORD_DIGIT_PATTERN.matcher(value).matches()) {
      context.buildConstraintViolationWithTemplate("Password must include at least 1 digit").addConstraintViolation();
      return false;
    } else if (PATTERN_SPACE.matcher(value).matches()) {
      context.buildConstraintViolationWithTemplate("Password should not include space").addConstraintViolation();
      return false;
    }
    return true;
  }
}
