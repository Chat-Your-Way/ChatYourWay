package com.chat.yourway.annotation.validator;

import com.chat.yourway.annotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 12;
  private static final Pattern PASSWORD_SPECIAL_SYMBOLS_PATTERN =
      Pattern.compile(".*[.,\\\\-_+&!;:'\"#*?].*");
    private static final Pattern PASSWORD_UPPER_CASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_DIGIT_PATTERN = Pattern.compile(".*\\d.*");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("The password should not be blank");
        } else if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("The password must be at least 4 characters long");
        } else if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("The password must not be longer than 12 characters");
        } else if (!PASSWORD_SPECIAL_SYMBOLS_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Password must include at least 1 special symbol: . , - _ + & ! ; : ' “ # * ?");
        } else if (!PASSWORD_UPPER_CASE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Password must include at least 1 Upper-case letter");
        } else if (!PASSWORD_DIGIT_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Password must include at least 1 digit");
        }

        return true;
    }
}
