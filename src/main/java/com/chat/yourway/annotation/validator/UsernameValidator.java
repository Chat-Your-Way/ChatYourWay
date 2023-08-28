package com.chat.yourway.annotation.validator;

import com.chat.yourway.annotation.Username;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<Username, String> {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]{4,20}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("The username should not be blank");
        } else if (!USERNAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("The username is invalid");
        }
        return true;
    }
}
