package com.chat.yourway.unit.annotation.validator;

import com.chat.yourway.annotation.validator.EmailValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidationValidatorTest {
  public EmailValidator emailValidator;
  public ConstraintValidatorContext context;

  @BeforeEach
  public void setUp() {
    emailValidator = new EmailValidator();
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

  @DisplayName("EmailValidator should pass validator when user input correct email")
  @Test
  public void shouldPassValidator_whenUserInputCorrectEmail() {
    // Given
    var email = "user@example.com";

    // When
    var isValid = emailValidator.isValid(email, context);

    // Then
    assertTrue(isValid);
  }

  @DisplayName("EmailValidator should fail validator when user input blank email")
  @Test
  public void shouldFailValidator_whenUserInputBlankEmail() {
    // Given
    var email = "";

    // When
    var isValid = emailValidator.isValid(email, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName("EmailValidator should fail validator when user input short email")
  @Test
  public void shouldFailValidator_whenUserInputShortEmail() {
    // Given
    var email = "a@b.c";

    // When
    var isValid = emailValidator.isValid(email, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName("EmailValidator should fail validator when user input long email")
  @Test
  public void shouldFailValidator_whenUserInputLongEmail() {
    // Given
    var email =
        "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddemailaddressforthispurposeonlyabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz@example.com";

    // When
    var isValid = emailValidator.isValid(email, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName("EmailValidator should fail validator when user input invalid characters")
  @Test
  public void shouldFailValidator_whenUserInputInvalidCharacters() {
    // Given
    var email = "user!name@example.com";

    // When
    var isValid = emailValidator.isValid(email, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName(
      "EmailValidator should fail validator when user input invalid top level domain")
  @Test
  public void shouldFailValidator_whenUserInputInvalidTopLevelDomain() {
    // Given
    var email = "user@example.invalid";

    // When
    var isValid = emailValidator.isValid(email, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName("EmailValidator should fail validator when user input email with space")
  @Test
  public void shouldFailValidator_whenUserInputEmailWithSpace() {
    // Given
    var email = "user name@example.com";

    // When
    var isValid = emailValidator.isValid(email, context);

    // Then
    assertFalse(isValid);
  }

  @DisplayName(
      "EmailValidator should pass validator when user input email with dots, underscores and dashes")
  @Test
  public void shouldPassValidator_whenUserInputEmailWithDotsUnderscoresAndDashes() {
    // Given
    var email = "user.name-one_win@example.com";

    // When
    var isValid = emailValidator.isValid(email, context);

    // Then
    assertTrue(isValid);
  }
}
