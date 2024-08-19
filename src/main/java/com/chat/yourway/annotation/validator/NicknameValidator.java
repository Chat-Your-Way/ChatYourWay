package com.chat.yourway.annotation.validator;

import com.chat.yourway.annotation.NicknameValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import static com.chat.yourway.utils.Constants.NICKNAME_PATTERN;
import static com.chat.yourway.utils.Constants.PATTERN_SPACE;

public class NicknameValidator implements ConstraintValidator<NicknameValidation, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();

    if (value.isBlank()) {
      context
          .buildConstraintViolationWithTemplate("The nickname should not be blank")
          .addConstraintViolation();
      return false;
    } else if (!NICKNAME_PATTERN.matcher(value).matches()) {
      context
          .buildConstraintViolationWithTemplate(
              "The nickname should be 4-20 characters long and include only letters, numbers or "
              + "symbols [! @ # $ % ^ & * _ - + = ~ ?]")
          .addConstraintViolation();
      return false;
    } else if (PATTERN_SPACE.matcher(value).matches()) {
      context
          .buildConstraintViolationWithTemplate("The nickname should not include space")
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
