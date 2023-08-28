package com.chat.yourway.unit.annotation.validator;

import com.chat.yourway.annotation.validator.ContactEmailValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class ContactEmailValidatorTest {
    public static final ContactEmailValidator CONTACT_EMAIL_VALIDATOR = new ContactEmailValidator();

    @Mock
    public ConstraintValidatorContext context;

    @DisplayName("ContactEmailValidator should pass validator when user input correct email")
    @Test
    public void shouldPassValidator_whenUserInputCorrectEmail() {
        // Given
        var email = "user@example.com";

        // When
        var isValid = CONTACT_EMAIL_VALIDATOR.isValid(email, context);

        // Then
        assertTrue(isValid);
    }

    @DisplayName("ContactEmailValidator should fail validator when user input blank email")
    @Test
    public void shouldFailValidator_whenUserInputBlankEmail() {
        // Given
        var email = "";

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> CONTACT_EMAIL_VALIDATOR.isValid(email, context));
    }

    @DisplayName("ContactEmailValidator should fail validator when user input short email")
    @Test
    public void shouldFailValidator_whenUserInputShortEmail() {
        // Given
        var email = "a@b.c";

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> CONTACT_EMAIL_VALIDATOR.isValid(email, context));
    }

    @DisplayName("ContactEmailValidator should fail validator when user input long email")
    @Test
    public void shouldFailValidator_whenUserInputLongEmail() {
        // Given
        var email = "toolongemailaddress1234567890123456789012345678901234567890123456789012345123213123132222222222222222222222222221322222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222111111111111111111111111111111111111111111111112222222222222222222222222222222222222267890@example.com";

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> CONTACT_EMAIL_VALIDATOR.isValid(email, context));
    }

    @DisplayName("ContactEmailValidator should fail validator when user input invalid characters")
    @Test
    public void shouldFailValidator_whenUserInputInvalidCharacters() {
        // Given
        var email = "user!name@example.com";

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> CONTACT_EMAIL_VALIDATOR.isValid(email, context));
    }

    @DisplayName("ContactEmailValidator should fail validator when user input invalid top level domain")
    @Test
    public void shouldFailValidator_whenUserInputInvalidTopLevelDomain() {
        // Given
        var email = "user@example.invalid";

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> CONTACT_EMAIL_VALIDATOR.isValid(email, context));
    }

    @DisplayName("ContactEmailValidator should fail validator when user input email with space")
    @Test
    public void shouldFailValidator_whenUserInputEmailWithSpace() {
        // Given
        var email = "user name@example.com";

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> CONTACT_EMAIL_VALIDATOR.isValid(email, context));
    }
}
