package com.chat.yourway.unit.annotation.validator;

import com.chat.yourway.annotation.validator.PasswordValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidationValidatorTest {
  public PasswordValidator passwordValidator;
  public ConstraintValidatorContext context;

  @BeforeEach
  public void setUp() {
    passwordValidator = new PasswordValidator();
    context = Mockito.mock(ConstraintValidatorContext.class);

    // Mock the behavior of buildConstraintViolationWithTemplate
    Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString()))
        .thenAnswer(
            (Answer<ConstraintValidatorContext.ConstraintViolationBuilder>)
                invocation -> {
                  ConstraintValidatorContext.ConstraintViolationBuilder builder =
                      Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
                  Mockito.when(builder.addConstraintViolation()).thenReturn(context);
                  return builder;
                });
  }

  @DisplayName("PasswordValidator should pass validator when user input correct password")
  @Test
  public void shouldPassValidator_whenUserInputCorrectPassword() {
    // Given
    var password = "P-ssw0rd";

    // When
    var isValid = passwordValidator.isValid(password, context);

    // Then
    assertTrue(isValid);
  }

  @DisplayName("PasswordValidator should fail validator when user input blank password")
  @Test
  public void shouldFailValidator_whenUserInputBlankPassword() {
    // Given
    var password = "";

    // When
    var isValid = passwordValidator.isValid(password, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName("PasswordValidator should fail validator when user input short password")
  @Test
  public void shouldFailValidator_whenUserInputShortPassword() {
    // Given
    var password = "abc";

    // When
    var isValid = passwordValidator.isValid(password, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName("PasswordValidator should fail validator when user input long password")
  @Test
  public void shouldFailValidator_whenUserInputLongPassword() {
    // Given
    var password = "verylongpassword";

    // When
    var isValid = passwordValidator.isValid(password, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName(
      "PasswordValidator should fail validator when user input password without special symbol")
  @Test
  public void shouldFailValidator_whenUserInputPasswordWithoutSpecialSymbol() {
    // Given
    var password = "Password123";

    // When
    var isValid = passwordValidator.isValid(password, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName(
      "PasswordValidator should fail validator when user input password without upper-case character")
  @Test
  public void shouldFailValidator_whenUserInputPasswordWithoutUpperCase() {
    // Given
    var password = "password123!";

    // When
    var isValid = passwordValidator.isValid(password, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName("PasswordValidator should fail validator when user input password without digit")
  @Test
  public void shouldFailValidator_whenUserInputPasswordWithoutDigit() {
    // Given
    var password = "Password!";

    // When
    var isValid = passwordValidator.isValid(password, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName("PasswordValidator should fail validator when user input password with space")
  @Test
  public void shouldFailValidator_whenUserInputPasswordWithSpace() {
    // Given
    var password = "Passw ord! ";

    // When
    var isValid = passwordValidator.isValid(password, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName(
      "PasswordValidator should fail validator when user input password with forbidden symbols")
  @Test
  public void shouldFailValidator_whenUserInputPasswordWithForbiddenSymbols() {
    // Given
    var password = "<Password!";

    // When
    var isValid = passwordValidator.isValid(password, context);

    // Then
    assertFalse(isValid);
  }
}
