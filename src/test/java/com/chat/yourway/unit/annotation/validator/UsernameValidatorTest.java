package com.chat.yourway.unit.annotation.validator;

import com.chat.yourway.annotation.validator.UsernameValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class UsernameValidatorTest {
    public static final UsernameValidator USERNAME_VALIDATOR = new UsernameValidator();

    @Mock
    public ConstraintValidatorContext context;

    @DisplayName("UsernameValidator should pass validator when user input correct username")
    @Test
    public void shouldPassValidator_whenUserInputUsername() {
        // Given
        var usernameFirstExample = "user123";
        var usernameSecondExample = "Username123";
        var usernameThirdExample = "abcd1234";

        // When
        var isValidFirstExample = USERNAME_VALIDATOR.isValid(usernameFirstExample, context);
        var isValidSecondExample = USERNAME_VALIDATOR.isValid(usernameSecondExample, context);
        var isValidThirdExample = USERNAME_VALIDATOR.isValid(usernameThirdExample, context);

        // Then
        assertTrue(isValidFirstExample);
        assertTrue(isValidSecondExample);
        assertTrue(isValidThirdExample);
    }

    @DisplayName("UsernameValidator should fail validator when user input username with invalid length")
    @Test
    public void shouldPassValidator_whenUserInputUsernameWithInvalidLength() {
        // Given
        var usernameFirstExample = "a1c";
        var usernameSecondExample = "abcdefghijklmnopqrst122121";

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> USERNAME_VALIDATOR.isValid(usernameFirstExample, context));
        assertThrows(IllegalArgumentException.class, () -> USERNAME_VALIDATOR.isValid(usernameSecondExample, context));
    }

    @DisplayName("UsernameValidator should fail validator when user input username with invalid characters")
    @Test
    public void shouldPassValidator_whenUserInputUsernameWithInvalidCharacters() {
        // Given
        var usernameFirstExample = "user 123";
        var usernameSecondExample = "user@123";

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> USERNAME_VALIDATOR.isValid(usernameFirstExample, context));
        assertThrows(IllegalArgumentException.class, () -> USERNAME_VALIDATOR.isValid(usernameSecondExample, context));
    }
}
