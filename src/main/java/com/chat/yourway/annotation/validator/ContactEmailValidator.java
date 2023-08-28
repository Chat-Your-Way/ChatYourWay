package com.chat.yourway.annotation.validator;

import com.chat.yourway.annotation.ContactEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ContactEmailValidator implements ConstraintValidator<ContactEmail, String> {
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 320;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-z0-9]+@[a-z]+\\.[a-z]{2,3}");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("The email should not be blank");
        } else if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("The email must be at least 6 characters long");
        } else if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("The email must not be longer than 320 characters");
        } else if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("The email is invalid");
        }

        return true;
    }
}
