package com.chat.yourway.annotation.validator;

import com.chat.yourway.annotation.NicknameValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static com.chat.yourway.annotation.constant.ValidatorConstant.PATTERN_SPACE;

public class NicknameValidator implements ConstraintValidator<NicknameValidation, String> {

  private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9а-яА-ЯІіЇї]{4,20}$");

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
          .buildConstraintViolationWithTemplate("The nickname is invalid")
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
