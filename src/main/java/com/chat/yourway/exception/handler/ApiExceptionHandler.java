package com.chat.yourway.exception.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.chat.yourway.dto.response.ApiErrorResponseDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.EmailSendingException;
import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.exception.InvalidCredentialsException;
import com.chat.yourway.exception.InvalidTokenException;
import com.chat.yourway.exception.OldPasswordsIsNotEqualToNewException;
import com.chat.yourway.exception.TokenNotFoundException;
import com.chat.yourway.exception.ValueNotUniqException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ApiResponse(responseCode = "ErrorCode", description = "Error response",
    content = @Content(schema = @Schema(implementation = ApiErrorResponseDto.class),
        mediaType = MediaType.APPLICATION_JSON_VALUE))
@RestControllerAdvice
public class ApiExceptionHandler {

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(ContactNotFoundException.class)
  public ApiErrorResponseDto handleContactNotFoundException(ContactNotFoundException exception) {
    return new ApiErrorResponseDto(NOT_FOUND, exception.getMessage());
  }

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(EmailTokenNotFoundException.class)
  public ApiErrorResponseDto handleEmailTokenNotFoundException(
      EmailTokenNotFoundException exception) {
    return new ApiErrorResponseDto(NOT_FOUND, exception.getMessage());
  }

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(TokenNotFoundException.class)
  public ApiErrorResponseDto handleTokenNotFoundException(TokenNotFoundException exception) {
    return new ApiErrorResponseDto(NOT_FOUND, exception.getMessage());
  }

  @ResponseStatus(CONFLICT)
  @ExceptionHandler(ValueNotUniqException.class)
  public ApiErrorResponseDto handleValueNotUniqException(ValueNotUniqException exception) {
    return new ApiErrorResponseDto(CONFLICT, exception.getMessage());
  }

  @ResponseStatus(UNAUTHORIZED)
  @ExceptionHandler(InvalidTokenException.class)
  public ApiErrorResponseDto handleInvalidTokenException(InvalidTokenException exception) {
    return new ApiErrorResponseDto(UNAUTHORIZED, exception.getMessage());
  }

  @ResponseStatus(UNAUTHORIZED)
  @ExceptionHandler(InvalidCredentialsException.class)
  public ApiErrorResponseDto handleInvalidCredentialsException(
      InvalidCredentialsException exception) {
    return new ApiErrorResponseDto(UNAUTHORIZED, exception.getMessage());
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(EmailSendingException.class)
  public ApiErrorResponseDto handleEmailSendingException(EmailSendingException exception) {
    return new ApiErrorResponseDto(BAD_REQUEST, exception.getMessage());
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(OldPasswordsIsNotEqualToNewException.class)
  public ApiErrorResponseDto handleOldPasswordsIsNotEqualToNewException(
      OldPasswordsIsNotEqualToNewException exception) {
    return new ApiErrorResponseDto(BAD_REQUEST, exception.getMessage());
  }

}
