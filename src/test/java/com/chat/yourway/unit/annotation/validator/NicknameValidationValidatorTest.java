package com.chat.yourway.unit.annotation.validator;

import com.chat.yourway.annotation.validator.NicknameValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;

class NicknameValidationValidatorTest {
  private NicknameValidator nicknameValidator;
  private ConstraintValidatorContext context;

  @BeforeEach
  public void setUp() {
    nicknameValidator = new NicknameValidator();
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

  @DisplayName("NicknameValidator should pass validator when user input correct nickname")
  @Test
  public void shouldPassValidator_whenUserInputNickname() {
    // Given
    var nicknameFirstExample = "user123";
    var nicknameSecondExample = "Username123";
    var nicknameThirdExample = "abcd1234";

    // When
    var isValidFirstExample = nicknameValidator.isValid(nicknameFirstExample, context);
    var isValidSecondExample = nicknameValidator.isValid(nicknameSecondExample, context);
    var isValidThirdExample = nicknameValidator.isValid(nicknameThirdExample, context);

    // Then
    assertTrue(isValidFirstExample);
    assertTrue(isValidSecondExample);
    assertTrue(isValidThirdExample);
  }

  @DisplayName(
      "NicknameValidator should fail validator when user input nickname with invalid length")
  @Test
  public void shouldFailValidator_whenUserInputNicknameWithInvalidLength() {
    // Given
    var nicknameFirstExample = "a1c";
    var nicknameSecondExample = "abcdefghijklmnopqrst122121";

    // When
    var isValidFirstExample = nicknameValidator.isValid(nicknameFirstExample, context);
    var isValidSecondExample = nicknameValidator.isValid(nicknameSecondExample, context);

    // Then
    assertFalse(isValidFirstExample);
    assertFalse(isValidSecondExample);
  }

  @DisplayName(
      "NicknameValidator should fail validator when user input nickname with invalid characters")
  @Test
  public void shouldFailValidator_whenUserInputNicknameWithInvalidCharacters() {
    // Given
    var nicknameFirstExample = "user 123";
    var nicknameSecondExample = "user@123";

    // When
    var isValidFirstExample = nicknameValidator.isValid(nicknameFirstExample, context);
    var isValidSecondExample = nicknameValidator.isValid(nicknameSecondExample, context);

    // Then
    assertFalse(isValidFirstExample);
    assertFalse(isValidSecondExample);
  }

  @DisplayName("NicknameValidator should pass validator when user input nickname with cyrillic")
  @Test
  public void shouldPassValidator_whenUserInputNicknameWithCyrillic() {
    // Given
    var nicknameFirstExample = "Дімон123";
    var nicknameSecondExample = "Їжачок123";

    // When
    var isValidFirstExample = nicknameValidator.isValid(nicknameFirstExample, context);
    var isValidSecondExample = nicknameValidator.isValid(nicknameSecondExample, context);

    // Then
    assertTrue(isValidFirstExample);
    assertTrue(isValidSecondExample);
  }

  @DisplayName("NicknameValidator should fail validator when user input nickname with space")
  @Test
  public void shouldFailValidator_whenUserInputNicknameWithSpace() {
    // Given
    var nicknameFirstExample = "Дімон 123";

    // When
    var isValidFirstExample = nicknameValidator.isValid(nicknameFirstExample, context);

    // Then
    assertFalse(isValidFirstExample);
  }
}
