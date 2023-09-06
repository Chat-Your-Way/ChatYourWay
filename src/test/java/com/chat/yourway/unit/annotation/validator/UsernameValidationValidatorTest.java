package com.chat.yourway.unit.annotation.validator;

import com.chat.yourway.annotation.validator.UsernameValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;

class UsernameValidationValidatorTest {
    private UsernameValidator usernameValidator;
    private ConstraintValidatorContext context;

    @BeforeEach
    public void setUp() {
        usernameValidator = new UsernameValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);

        // Mock the behavior of buildConstraintViolationWithTemplate
        Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenAnswer((Answer<ConstraintValidatorContext.ConstraintViolationBuilder>) invocation -> {
                    ConstraintValidatorContext.ConstraintViolationBuilder builder =
                            Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
                    Mockito.when(builder.addConstraintViolation()).thenReturn(context);
                    return builder;
                });
    }

    @DisplayName("UsernameValidator should pass validator when user input correct username")
    @Test
    public void shouldPassValidator_whenUserInputUsername() {
        // Given
        var usernameFirstExample = "user123";
        var usernameSecondExample = "Username123";
        var usernameThirdExample = "abcd1234";

        // When
        var isValidFirstExample = usernameValidator.isValid(usernameFirstExample, context);
        var isValidSecondExample = usernameValidator.isValid(usernameSecondExample, context);
        var isValidThirdExample = usernameValidator.isValid(usernameThirdExample, context);

        // Then
        assertTrue(isValidFirstExample);
        assertTrue(isValidSecondExample);
        assertTrue(isValidThirdExample);
    }

    @DisplayName("UsernameValidator should fail validator when user input username with invalid length")
    @Test
    public void shouldFailValidator_whenUserInputUsernameWithInvalidLength() {
        // Given
        var usernameFirstExample = "a1c";
        var usernameSecondExample = "abcdefghijklmnopqrst122121";

        // When
        var isValidFirstExample =  usernameValidator.isValid(usernameFirstExample, context);
        var isValidSecondExample = usernameValidator.isValid(usernameSecondExample, context);

        // Then
        assertFalse(isValidFirstExample);
        assertFalse(isValidSecondExample);
    }

    @DisplayName("UsernameValidator should fail validator when user input username with invalid characters")
    @Test
    public void shouldFailValidator_whenUserInputUsernameWithInvalidCharacters() {
        // Given
        var usernameFirstExample = "user 123";
        var usernameSecondExample = "user@123";

        // When
        var isValidFirstExample =  usernameValidator.isValid(usernameFirstExample, context);
        var isValidSecondExample = usernameValidator.isValid(usernameSecondExample, context);

        // Then
        assertFalse(isValidFirstExample);
        assertFalse(isValidSecondExample);
    }

    @DisplayName("UsernameValidator should pass validator when user input username with cyrillic")
    @Test
    public void shouldPassValidator_whenUserInputUsernameWithCyrillic() {
        // Given
        var usernameFirstExample = "Дімон123";
        var usernameSecondExample = "Їжачок123";

        // When
        var isValidFirstExample =  usernameValidator.isValid(usernameFirstExample, context);
        var isValidSecondExample = usernameValidator.isValid(usernameSecondExample, context);

        // Then
        assertTrue(isValidFirstExample);
        assertTrue(isValidSecondExample);
    }
}
